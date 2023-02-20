from dataclasses import asdict

from django.http import JsonResponse
from drf_yasg.openapi import Parameter, IN_HEADER, IN_QUERY
from drf_yasg.utils import swagger_auto_schema
from rest_framework import mixins
from rest_framework.generics import RetrieveAPIView, CreateAPIView, DestroyAPIView
from rest_framework.status import HTTP_200_OK, HTTP_400_BAD_REQUEST, HTTP_401_UNAUTHORIZED, HTTP_403_FORBIDDEN, \
    HTTP_404_NOT_FOUND
from rest_framework.views import APIView

from attachments.domain.attachment import Attachment
from attachments.domain.attachment_type import AttachmentType
from coordinates.domain.coordinates_dto import CoordinatesDto
from nmedia.data.response import auth_error
from nmedia.data.serializer import ErrorSerializer
from nmedia.dependencies import DependencyContainer
from nmedia.domain.errors import Error, CodeTextError
from posts.domain.models.post_create_request import PostCreateRequest
from posts.serializers import PostResponseSerializer, PostCreateRequestSerializer

post_service = DependencyContainer.get_instance().post_service
user_service = DependencyContainer.get_instance().user_service


class PostsGetAllOrCreateView(RetrieveAPIView, CreateAPIView):

    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=False, type="string")],
        responses={
            HTTP_200_OK: PostResponseSerializer(many=True),
        },
        tags=["Posts"],
    )
    def get(self, request, *args, **kwargs):
        token = request.headers.get("Authorization")
        posts = post_service.get_all(token=token)
        serializer = PostResponseSerializer(data=list(map(lambda item: asdict(item), posts)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)

    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string")],
        responses={
            HTTP_200_OK: PostResponseSerializer(),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
            HTTP_401_UNAUTHORIZED: ErrorSerializer(),
            HTTP_403_FORBIDDEN: ErrorSerializer(),
        },
        request_body=PostCreateRequestSerializer,
        tags=["Posts"],
    )
    def post(self, request, *args, **kwargs):
        serializer = PostCreateRequestSerializer(data=request.data)
        if 'attachment' in request.data:
            attachment_request = request.data['attachment']
            if attachment_request is not None:
                attachment_request['type'] = AttachmentType.from_str(attachment_request['type'])
        serializer.is_valid(raise_exception=True)
        if "coords" in serializer.validated_data:
            coords_data = serializer.validated_data["coords"]
            if coords_data is None:
                coords = None
            else:
                coords = CoordinatesDto(lat=coords_data['lat'], long=coords_data['long'])
        else:
            coords = None
        if "attachment" in serializer.validated_data:
            attachment_data = serializer.validated_data["attachment"]
            if attachment_data is None:
                attachment = None
            else:
                attachment = Attachment(attachment_data['url'], AttachmentType.from_str(attachment_data['type']))
        else:
            attachment = None
        post = PostCreateRequest(
            id=serializer.validated_data['id'],
            content=serializer.validated_data['content'],
            coords=coords,
            link=serializer.validated_data['link'],
            attachment=attachment,
            mentionIds=serializer.validated_data['mentionIds'],
        )
        if "Authorization" not in request.headers:
            serializer = ErrorSerializer(data=asdict(Error("Authorization required")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_401_UNAUTHORIZED)
        else:
            token = request.headers["Authorization"]
        created_post = post_service.save(post, token)
        if type(created_post) is CodeTextError:
            serializer = ErrorSerializer(data=asdict(Error(created_post.text)))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=created_post.code)
        else:
            serializer = PostResponseSerializer(data=asdict(created_post))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data)


class PostsGetAllWallView(RetrieveAPIView):

    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=False, type="string")],
        responses={
            HTTP_200_OK: PostResponseSerializer(many=True),
        },
        tags=["Wall"]
    )
    def get(self, request, *args, **kwargs):
        token = request.headers.get("Authorization")
        author_id = int(kwargs['author_id'])
        posts = post_service.get_all_by_author_id(author_id=author_id, token=token)
        serializer = PostResponseSerializer(data=list(map(lambda item: asdict(item), posts)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class PostsGetAllMyWallView(RetrieveAPIView):

    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string")],
        responses={
            HTTP_200_OK: PostResponseSerializer(many=True),
        },
        tags=["MyWall"]
    )
    def get(self, request, *args, **kwargs):
        token = request.headers.get("Authorization")
        user = user_service.get_by_token(token)
        if user is not None:
            user_id = user.id
        else:
            return auth_error()
        posts = post_service.get_all_by_author_id(author_id=user_id, token=token)
        serializer = PostResponseSerializer(data=list(map(lambda item: asdict(item), posts)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class PostsGetLatestByAuthorView(RetrieveAPIView):
    @swagger_auto_schema(
        manual_parameters=[
            Parameter(in_=IN_HEADER, name="Authorization", required=False, type="string"),
            Parameter(in_=IN_QUERY, name="count", required=True, type="integer"),
        ],
        responses={
            HTTP_200_OK: PostResponseSerializer(many=True),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
        },
        tags=["Wall"],
    )
    def get(self, request, *args, **kwargs):
        count = request.GET['count']
        if not count.isdigit():
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be int")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        count_digit = int(count)
        if count_digit <= 0:
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be not zero")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        author_id = int(kwargs['author_id'])
        posts = post_service.get_latest_by_author(
            author_id=author_id, count=count_digit, token=request.headers.get("Authorization")
        )
        serializer = PostResponseSerializer(data=list(map(lambda item: asdict(item), posts)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class PostsGetLatestView(RetrieveAPIView):
    @swagger_auto_schema(
        manual_parameters=[
            Parameter(in_=IN_HEADER, name="Authorization", required=False, type="string"),
            Parameter(in_=IN_QUERY, name="count", required=True, type="integer"),
        ],
        responses={
            HTTP_200_OK: PostResponseSerializer(many=True),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
        },
        tags=["Posts"],
    )
    def get(self, request, *args, **kwargs):
        count = request.GET['count']
        if not count.isdigit():
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be int")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        count_digit = int(count)
        if count_digit <= 0:
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be not zero")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        posts = post_service.get_latest(count=count_digit, token=request.headers.get("Authorization"))
        serializer = PostResponseSerializer(data=list(map(lambda item: asdict(item), posts)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class PostsGetLatestMyView(RetrieveAPIView):
    @swagger_auto_schema(
        manual_parameters=[
            Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string"),
            Parameter(in_=IN_QUERY, name="count", required=True, type="integer"),
        ],
        responses={
            HTTP_200_OK: PostResponseSerializer(many=True),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
        },
        tags=["MyWall"],
    )
    def get(self, request, *args, **kwargs):
        count = request.GET['count']
        if not count.isdigit():
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be int")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        count_digit = int(count)
        if count_digit <= 0:
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be not zero")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        token = request.headers.get("Authorization")
        user = user_service.get_by_token(token)
        if user is not None:
            user_id = user.id
        else:
            return auth_error()
        posts = post_service.get_latest_by_author(author_id=user_id, count=count_digit, token=token)
        serializer = PostResponseSerializer(data=list(map(lambda item: asdict(item), posts)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class PostsGetByIdOrRemoveView(RetrieveAPIView, DestroyAPIView):
    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string")],
        responses={
            HTTP_200_OK: "{}",
            HTTP_401_UNAUTHORIZED: ErrorSerializer(),
            HTTP_403_FORBIDDEN: ErrorSerializer(),
        },
        tags=["Posts"],
    )
    def delete(self, request, *args, **kwargs):
        post_id = int(kwargs['post_id'])
        if "Authorization" not in request.headers:
            serializer = ErrorSerializer(data=asdict(Error("Authorization required")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_401_UNAUTHORIZED)
        else:
            token = request.headers["Authorization"]
        result = post_service.delete_by_id(post_id=post_id, token=token)
        if isinstance(result, CodeTextError):
            serializer = ErrorSerializer(data=asdict(Error(result.text)))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=result.code)
        else:
            return JsonResponse(data=dict(), safe=False)

    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=False, type="string")],
        responses={
            HTTP_200_OK: PostResponseSerializer(),
            HTTP_404_NOT_FOUND: ErrorSerializer(),
        },
        tags=["Posts"],
    )
    def get(self, request, *args, **kwargs):
        post_id = int(kwargs['post_id'])
        result = post_service.get_by_id(post_id=post_id, token=request.headers.get("Authorization"))
        if isinstance(result, CodeTextError):
            serializer = ErrorSerializer(data=asdict(Error(result.text)))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=result.code)
        else:
            serializer = PostResponseSerializer(data=asdict(result))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data)


class PostsGetAfterWallView(RetrieveAPIView):
    @swagger_auto_schema(
        manual_parameters=[
            Parameter(in_=IN_HEADER, name="Authorization", required=False, type="string"),
            Parameter(in_=IN_QUERY, name="count", required=True, type="integer"),
        ],
        responses={
            HTTP_200_OK: PostResponseSerializer(many=True),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
        },
        tags=["Wall"],
    )
    def get(self, request, *args, **kwargs):
        count = request.GET['count']
        if not count.isdigit():
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be int")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        count_digit = int(count)
        if count_digit <= 0:
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be not zero")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        post_id = int(kwargs['post_id'])
        author_id = int(kwargs['author_id'])
        posts = post_service.get_after_by_author(
            author_id=author_id, id=post_id, count=count_digit, token=request.headers.get("Authorization")
        )
        serializer = PostResponseSerializer(data=list(map(lambda item: asdict(item), posts)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class PostsGetAfterView(RetrieveAPIView):
    @swagger_auto_schema(
        manual_parameters=[
            Parameter(in_=IN_HEADER, name="Authorization", required=False, type="string"),
            Parameter(in_=IN_QUERY, name="count", required=True, type="integer"),
        ],
        responses={
            HTTP_200_OK: PostResponseSerializer(many=True),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
        },
        tags=["Posts"],
    )
    def get(self, request, *args, **kwargs):
        count = request.GET['count']
        if not count.isdigit():
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be int")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        count_digit = int(count)
        if count_digit <= 0:
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be not zero")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        post_id = int(kwargs['post_id'])
        posts = post_service.get_after(id=post_id, count=count_digit, token=request.headers.get("Authorization"))
        serializer = PostResponseSerializer(data=list(map(lambda item: asdict(item), posts)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class PostsGetAfterMyWallView(RetrieveAPIView):
    @swagger_auto_schema(
        manual_parameters=[
            Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string"),
            Parameter(in_=IN_QUERY, name="count", required=True, type="integer"),
        ],
        responses={
            HTTP_200_OK: PostResponseSerializer(many=True),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
            HTTP_401_UNAUTHORIZED: ErrorSerializer(),
        },
        tags=["MyWall"],
    )
    def get(self, request, *args, **kwargs):
        count = request.GET['count']
        if not count.isdigit():
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be int")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        count_digit = int(count)
        if count_digit <= 0:
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be not zero")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        post_id = int(kwargs['post_id'])
        token = request.headers.get("Authorization")
        user = user_service.get_by_token(token)
        if user is not None:
            user_id = user.id
        else:
            return auth_error()
        posts = post_service.get_after_by_author(
            author_id=user_id, id=post_id, count=count_digit, token=request.headers.get("Authorization")
        )
        serializer = PostResponseSerializer(data=list(map(lambda item: asdict(item), posts)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class PostsGetBeforeWallView(RetrieveAPIView):
    @swagger_auto_schema(
        manual_parameters=[
            Parameter(in_=IN_HEADER, name="Authorization", required=False, type="string"),
            Parameter(in_=IN_QUERY, name="count", required=True, type="integer"),
        ],
        responses={
            HTTP_200_OK: PostResponseSerializer(many=True),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
        },
        tags=["Wall"],
    )
    def get(self, request, *args, **kwargs):
        count = request.GET['count']
        if not count.isdigit():
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be int")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        count_digit = int(count)
        if count_digit <= 0:
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be not zero")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        post_id = int(kwargs['post_id'])
        author_id = int(kwargs['author_id'])
        posts = post_service.get_before_by_author(
            author_id=author_id, id=post_id, count=count_digit, token=request.headers.get("Authorization")
        )
        serializer = PostResponseSerializer(data=list(map(lambda item: asdict(item), posts)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class PostsGetBeforeView(RetrieveAPIView):
    @swagger_auto_schema(
        manual_parameters=[
            Parameter(in_=IN_HEADER, name="Authorization", required=False, type="string"),
            Parameter(in_=IN_QUERY, name="count", required=True, type="integer"),
        ],
        responses={
            HTTP_200_OK: PostResponseSerializer(many=True),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
        },
        tags=["Posts"],
    )
    def get(self, request, *args, **kwargs):
        count = request.GET['count']
        if not count.isdigit():
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be int")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        count_digit = int(count)
        if count_digit <= 0:
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be not zero")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        post_id = int(kwargs['post_id'])
        posts = post_service.get_before(id=post_id, count=count_digit, token=request.headers.get("Authorization"))
        serializer = PostResponseSerializer(data=list(map(lambda item: asdict(item), posts)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class PostsGetBeforeMyWallView(RetrieveAPIView):
    @swagger_auto_schema(
        manual_parameters=[
            Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string"),
            Parameter(in_=IN_QUERY, name="count", required=True, type="integer"),
        ],
        responses={
            HTTP_200_OK: PostResponseSerializer(many=True),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
            HTTP_401_UNAUTHORIZED: ErrorSerializer(),
        },
        tags=["MyWall"],
    )
    def get(self, request, *args, **kwargs):
        count = request.GET['count']
        if not count.isdigit():
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be int")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        count_digit = int(count)
        if count_digit <= 0:
            serializer = ErrorSerializer(data=asdict(Error("Count parameter must be not zero")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_400_BAD_REQUEST)
        post_id = int(kwargs['post_id'])
        token = request.headers.get("Authorization")
        user = user_service.get_by_token(token)
        if user is not None:
            user_id = user.id
        else:
            return auth_error()
        posts = post_service.get_before_by_author(author_id=user_id, id=post_id, count=count_digit, token=token)
        serializer = PostResponseSerializer(data=list(map(lambda item: asdict(item), posts)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class PostsGetNewerView(RetrieveAPIView):
    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=False, type="string")],
        responses={
            HTTP_200_OK: PostResponseSerializer(many=True),
        },
        tags=["Posts"],
    )
    def get(self, request, *args, **kwargs):
        post_id = int(kwargs['post_id'])
        posts = post_service.get_newer(id=post_id, token=request.headers.get("Authorization"))
        serializer = PostResponseSerializer(data=list(map(lambda item: asdict(item), posts)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class PostsGetNewerWallView(RetrieveAPIView):
    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=False, type="string")],
        responses={
            HTTP_200_OK: PostResponseSerializer(many=True),
        },
        tags=["Wall"],
    )
    def get(self, request, *args, **kwargs):
        post_id = int(kwargs['post_id'])
        author_id = int(kwargs['author_id'])
        posts = post_service.get_newer_by_author(
            author_id=author_id, post_id=post_id, token=request.headers.get("Authorization")
        )
        serializer = PostResponseSerializer(data=list(map(lambda item: asdict(item), posts)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class PostsGetNewerMyWallView(RetrieveAPIView):
    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string")],
        responses={
            HTTP_200_OK: PostResponseSerializer(many=True),
            HTTP_401_UNAUTHORIZED: ErrorSerializer(),
        },
        tags=["MyWall"],
    )
    def get(self, request, *args, **kwargs):
        post_id = int(kwargs['post_id'])
        token = request.headers.get("Authorization")
        user = user_service.get_by_token(token)
        if user is not None:
            user_id = user.id
        else:
            return auth_error()
        posts = post_service.get_newer_by_author(
            author_id=user_id, post_id=post_id, token=request.headers.get("Authorization")
        )
        serializer = PostResponseSerializer(data=list(map(lambda item: asdict(item), posts)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class PostsLikeOrDislikeView(APIView, mixins.CreateModelMixin, mixins.DestroyModelMixin):
    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string")],
        responses={
            HTTP_200_OK: PostResponseSerializer(),
            HTTP_401_UNAUTHORIZED: ErrorSerializer(),
            HTTP_403_FORBIDDEN: ErrorSerializer(),
        },
        tags=["Posts"],
    )
    def delete(self, request, **kwargs):
        post_id = int(kwargs['post_id'])
        if "Authorization" not in request.headers:
            serializer = ErrorSerializer(data=asdict(Error("Authorization required")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_401_UNAUTHORIZED)
        else:
            token = request.headers["Authorization"]
        result = post_service.unlike_by_id(post_id=post_id, token=token)
        if isinstance(result, CodeTextError):
            serializer = ErrorSerializer(data=asdict(Error(result.text)))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=result.code)
        else:
            serializer = PostResponseSerializer(data=asdict(result))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data)

    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string")],
        responses={
            HTTP_200_OK: PostResponseSerializer(),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
            HTTP_401_UNAUTHORIZED: ErrorSerializer(),
            HTTP_404_NOT_FOUND: ErrorSerializer(),
        },
        tags=["Posts"],
    )
    def post(self, request, **kwargs):
        if "Authorization" not in request.headers:
            serializer = ErrorSerializer(data=asdict(Error("Authorization required")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_401_UNAUTHORIZED)
        else:
            token = request.headers["Authorization"]
        post_id = int(kwargs['post_id'])
        result = post_service.like_by_id(post_id, token)
        if isinstance(result, CodeTextError):
            serializer = ErrorSerializer(data=asdict(Error(result.text)))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=result.code)
        else:
            serializer = PostResponseSerializer(data=asdict(result))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data)
