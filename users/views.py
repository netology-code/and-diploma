from django.http import JsonResponse
from drf_yasg.utils import swagger_auto_schema
from rest_framework.generics import CreateAPIView, RetrieveAPIView
from rest_framework.parsers import MultiPartParser
from rest_framework.status import HTTP_200_OK, HTTP_400_BAD_REQUEST, HTTP_500_INTERNAL_SERVER_ERROR, HTTP_404_NOT_FOUND
from nmedia.data.serializer import ErrorSerializer
from nmedia.dependencies import DependencyContainer
from nmedia.domain.errors import TextError
from users.domain.models import Token
from users.serializers import RegistrationRequestSerializer, TokenSerializer, AuthenticationRequestSerializer, \
    UserResponseSerializer

user_service = DependencyContainer.get_instance().user_service
media_service = DependencyContainer.get_instance().media_service


class UsersRegistrationView(CreateAPIView):
    parser_classes = [MultiPartParser]

    @swagger_auto_schema(
        responses={
            HTTP_200_OK: TokenSerializer(),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
            HTTP_500_INTERNAL_SERVER_ERROR: ErrorSerializer(),
        },
        request_body=RegistrationRequestSerializer,
        tags=["Users"],
    )
    def post(self, request, *args, **kwargs):
        serializer = RegistrationRequestSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)

        deserialized_request = serializer.validated_data
        avatar_file = deserialized_request['file']
        if avatar_file is not None:
            avatar_result = media_service.save(avatar_file)
            if type(avatar_result) is TextError:
                error_serializer = ErrorSerializer(data=avatar_result.__dict__)
                error_serializer.is_valid(raise_exception=True)
                return JsonResponse(error_serializer.data, status=HTTP_500_INTERNAL_SERVER_ERROR)
            else:
                avatar_url = avatar_result.url
        else:
            avatar_url = None
        token = user_service.register(
            login=deserialized_request['login'],
            password=deserialized_request['password'],
            name=deserialized_request['name'],
            avatar=avatar_url,
        )
        if type(token) is Token:
            token_serializer = TokenSerializer(data=token.__dict__)
            token_serializer.is_valid(raise_exception=True)
            return JsonResponse(token_serializer.data)
        else:
            error_serializer = ErrorSerializer(data=token.__dict__)
            error_serializer.is_valid(raise_exception=True)
            return JsonResponse(error_serializer.data, status=HTTP_400_BAD_REQUEST)


class UsersAuthenticationView(CreateAPIView):
    @swagger_auto_schema(
        responses={
            HTTP_200_OK: TokenSerializer(),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
        },
        request_body=AuthenticationRequestSerializer,
        tags=["Users"],
    )
    def post(self, request, *args, **kwargs):
        serializer = AuthenticationRequestSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        token = user_service.login(login=serializer['login'].value, password=serializer['password'].value)
        if type(token) is Token:
            token_serializer = TokenSerializer(data=token.__dict__)
            token_serializer.is_valid(raise_exception=True)
            return JsonResponse(token_serializer.data)
        else:
            error_serializer = ErrorSerializer(data=token.__dict__)
            error_serializer.is_valid(raise_exception=True)
            return JsonResponse(error_serializer.data, status=HTTP_400_BAD_REQUEST)


class UsersGetAllView(RetrieveAPIView):
    @swagger_auto_schema(
        responses={
            HTTP_200_OK: UserResponseSerializer(many=True),
        },
        tags=["Users"],
    )
    def get(self, request, *args, **kwargs):
        users = user_service.get_all()
        serializer = UserResponseSerializer(data=list(map(lambda item: item.__dict__, users)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class UsersGetByIdView(RetrieveAPIView):
    @swagger_auto_schema(
        responses={
            HTTP_200_OK: UserResponseSerializer(),
            HTTP_404_NOT_FOUND: ErrorSerializer(),
        },
        tags=["Users"],
    )
    def get(self, request, *args, **kwargs):
        user_id = kwargs['user_id']
        user = user_service.get_by_id(user_id)
        if user is None:
            serializer = ErrorSerializer(data=TextError("User not found").__dict__)
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_404_NOT_FOUND)
        serializer = UserResponseSerializer(data=user.__dict__)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data)
