from dataclasses import asdict

from django.http import JsonResponse
from drf_yasg.openapi import Parameter, IN_HEADER
from drf_yasg.utils import swagger_auto_schema
from rest_framework.generics import CreateAPIView
from rest_framework.parsers import MultiPartParser
from rest_framework.status import HTTP_401_UNAUTHORIZED, HTTP_400_BAD_REQUEST, HTTP_500_INTERNAL_SERVER_ERROR, \
    HTTP_200_OK

from media.serializers import MediaResponseSerializer, MediaCreateRequestSerializer
from media.services.media_service import MediaService
from nmedia.data.response import auth_error
from nmedia.data.serializer import ErrorSerializer
from nmedia.dependencies import DependencyContainer
from nmedia.domain.errors import TextError, Error
from users.domain.services import UserService

media_service: MediaService = DependencyContainer.get_instance().media_service
user_service: UserService = DependencyContainer.get_instance().user_service


class MediaCreateView(CreateAPIView):
    parser_classes = [MultiPartParser]

    @swagger_auto_schema(
        manual_parameters=[
            Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string"),
        ],
        responses={
            HTTP_200_OK: MediaResponseSerializer(),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
            HTTP_401_UNAUTHORIZED: ErrorSerializer(),
            HTTP_500_INTERNAL_SERVER_ERROR: ErrorSerializer(),
        },
        request_body=MediaCreateRequestSerializer,
        tags=["Media"],
    )
    def post(self, request, *args, **kwargs):
        if "Authorization" not in request.headers:
            return auth_error()
        else:
            token = request.headers["Authorization"]
        user = user_service.get_by_token(token)
        if user is None:
            return auth_error()
        request_serializer = MediaCreateRequestSerializer(data=request.data)
        request_serializer.is_valid(raise_exception=True)
        media_file = request_serializer.validated_data["file"]
        avatar_result = media_service.save(media_file)
        if type(avatar_result) is TextError:
            serializer = ErrorSerializer(data=asdict(avatar_result))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_500_INTERNAL_SERVER_ERROR)
        else:
            serializer = MediaResponseSerializer(data=asdict(avatar_result))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data)
