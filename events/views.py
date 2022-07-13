from dataclasses import asdict

from django.http import JsonResponse
from drf_yasg.openapi import Parameter, IN_HEADER
from drf_yasg.utils import swagger_auto_schema
from rest_framework import mixins
from rest_framework.generics import RetrieveAPIView, CreateAPIView, DestroyAPIView
from rest_framework.status import HTTP_200_OK, HTTP_401_UNAUTHORIZED, HTTP_403_FORBIDDEN, HTTP_400_BAD_REQUEST, \
    HTTP_404_NOT_FOUND
from rest_framework.views import APIView
from attachments.domain.attachment import Attachment
from attachments.domain.attachment_type import AttachmentType
from coordinates.domain.coordinates_dto import CoordinatesDto
from events.domain.event_service import EventService
from events.domain.models.event_create_request import EventCreateRequest
from events.domain.models.event_type import EventType
from events.serializers import EventResponseSerializer, EventCreateRequestSerializer
from nmedia.data.serializer import ErrorSerializer
from nmedia.dependencies import DependencyContainer
from nmedia.domain.errors import Error, CodeTextError
from users.domain.services import UserService

event_service: EventService = DependencyContainer.get_instance().event_service
user_service: UserService = DependencyContainer.get_instance().user_service


class EventsGetAllOrCreateView(RetrieveAPIView, CreateAPIView):
    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=False, type="string")],
        responses={
            HTTP_200_OK: EventResponseSerializer(many=True),
        },
        tags=["Events"],
    )
    def get(self, request, *args, **kwargs):
        token = request.headers.get("Authorization")
        events = event_service.get_all(token=token)
        serializer = EventResponseSerializer(data=list(map(lambda item: asdict(item), events)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)

    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string")],
        responses={
            HTTP_200_OK: EventResponseSerializer(),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
            HTTP_401_UNAUTHORIZED: ErrorSerializer(),
            HTTP_403_FORBIDDEN: ErrorSerializer(),
        },
        request_body=EventCreateRequestSerializer,
        tags=["Events"],
    )
    def post(self, request, *args, **kwargs):
        request.data['type'] = EventType.from_str(request.data['type'])
        serializer = EventCreateRequestSerializer(data=request.data)
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
        if "speaker_ids" in serializer.validated_data:
            speaker_ids_or_null = serializer.validated_data['speaker_ids']
            if speaker_ids_or_null is None:
                speaker_ids = None
            else:
                speaker_ids = set(speaker_ids_or_null)
        else:
            speaker_ids = set()
        event = EventCreateRequest(
            id=serializer.validated_data['id'],
            content=serializer.validated_data['content'],
            type=serializer.validated_data['type'],
            coords=coords,
            link=serializer.validated_data['link'],
            attachment=attachment,
            datetime=serializer.validated_data['datetime'],
            speaker_ids=speaker_ids,
        )
        if "Authorization" not in request.headers:
            serializer = ErrorSerializer(data=asdict(Error("Authorization required")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_401_UNAUTHORIZED)
        else:
            token = request.headers["Authorization"]
        created_event = event_service.save(event, token)
        if type(created_event) is CodeTextError:
            serializer = ErrorSerializer(data=asdict(Error(created_event.text)))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=created_event.code)
        else:
            serializer = EventResponseSerializer(data=asdict(created_event))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data)


class EventsGetLatestView(RetrieveAPIView):
    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=False, type="string")],
        responses={
            HTTP_200_OK: EventResponseSerializer(many=True),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
        },
        tags=["Events"],
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
        events = event_service.get_latest(count=count_digit, token=request.headers.get("Authorization"))
        serializer = EventResponseSerializer(data=list(map(lambda item: asdict(item), events)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class EventsGetByIdOrRemoveView(RetrieveAPIView, DestroyAPIView):
    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string")],
        responses={
            HTTP_200_OK: "{}",
            HTTP_401_UNAUTHORIZED: ErrorSerializer(),
            HTTP_403_FORBIDDEN: ErrorSerializer(),
        },
        tags=["Events"],
    )
    def delete(self, request, *args, **kwargs):
        event_id = kwargs['event_id']
        if "Authorization" not in request.headers:
            serializer = ErrorSerializer(data=asdict(Error("Authorization required")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_401_UNAUTHORIZED)
        else:
            token = request.headers["Authorization"]
        result = event_service.delete_by_id(event_id=event_id, token=token)
        if isinstance(result, CodeTextError):
            serializer = ErrorSerializer(data=asdict(Error(result.text)))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=result.code)
        else:
            return JsonResponse(data=dict(), safe=False)

    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string")],
        responses={
            HTTP_200_OK: EventResponseSerializer(many=True),
            HTTP_404_NOT_FOUND: ErrorSerializer(),
        },
        tags=["Events"],
    )
    def get(self, request, *args, **kwargs):
        event_id = kwargs['event_id']
        result = event_service.get_by_id(event_id=event_id, token=request.headers.get("Authorization"))
        if isinstance(result, CodeTextError):
            serializer = ErrorSerializer(data=asdict(Error(result.text)))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=result.code)
        else:
            serializer = EventResponseSerializer(data=asdict(result))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data)


class EventsGetAfterView(RetrieveAPIView):
    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=False, type="string")],
        responses={
            HTTP_200_OK: EventResponseSerializer(many=True),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
        },
        tags=["Events"],
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
        event_id = kwargs['event_id']
        events = event_service.get_after(id=event_id, count=count_digit, token=request.headers.get("Authorization"))
        serializer = EventResponseSerializer(data=list(map(lambda item: asdict(item), events)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class EventsGetBeforeView(RetrieveAPIView):
    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=False, type="string")],
        responses={
            HTTP_200_OK: EventResponseSerializer(many=True),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
        },
        tags=["Events"],
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
        event_id = kwargs['event_id']
        events = event_service.get_before(id=event_id, count=count_digit, token=request.headers.get("Authorization"))
        serializer = EventResponseSerializer(data=list(map(lambda item: asdict(item), events)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class EventsGetNewerView(RetrieveAPIView):
    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=False, type="string")],
        responses={
            HTTP_200_OK: EventResponseSerializer(many=True),
        },
        tags=["Events"],
    )
    def get(self, request, *args, **kwargs):
        event_id = kwargs['event_id']
        events = event_service.get_newer(id=event_id, token=request.headers.get("Authorization"))
        serializer = EventResponseSerializer(data=list(map(lambda item: asdict(item), events)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class EventsLikeOrDislikeView(APIView, mixins.CreateModelMixin, mixins.DestroyModelMixin):
    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string")],
        responses={
            HTTP_200_OK: EventResponseSerializer(),
            HTTP_401_UNAUTHORIZED: ErrorSerializer(),
            HTTP_403_FORBIDDEN: ErrorSerializer(),
        },
        tags=["Events"],
    )
    def delete(self, request, **kwargs):
        event_id = kwargs['event_id']
        if "Authorization" not in request.headers:
            serializer = ErrorSerializer(data=asdict(Error("Authorization required")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_401_UNAUTHORIZED)
        else:
            token = request.headers["Authorization"]
        result = event_service.unlike_by_id(event_id=event_id, token=token)
        if isinstance(result, CodeTextError):
            serializer = ErrorSerializer(data=asdict(Error(result.text)))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=result.code)
        else:
            serializer = EventResponseSerializer(data=asdict(result))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data)

    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string")],
        responses={
            HTTP_200_OK: EventResponseSerializer(),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
            HTTP_401_UNAUTHORIZED: ErrorSerializer(),
            HTTP_404_NOT_FOUND: ErrorSerializer(),
        },
        tags=["Events"],
    )
    def post(self, request, **kwargs):
        if "Authorization" not in request.headers:
            serializer = ErrorSerializer(data=asdict(Error("Authorization required")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_401_UNAUTHORIZED)
        else:
            token = request.headers["Authorization"]
        event_id = kwargs['event_id']
        result = event_service.like_by_id(event_id, token)
        if isinstance(result, CodeTextError):
            serializer = ErrorSerializer(data=asdict(Error(result.text)))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=result.code)
        else:
            serializer = EventResponseSerializer(data=asdict(result))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data)


class EventsParticipateOrUnparticipateView(APIView, mixins.CreateModelMixin, mixins.DestroyModelMixin):

    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string")],
        responses={
            HTTP_200_OK: EventResponseSerializer(),
            HTTP_401_UNAUTHORIZED: ErrorSerializer(),
            HTTP_403_FORBIDDEN: ErrorSerializer(),
        },
        tags=["Events"],
    )
    def delete(self, request, **kwargs):
        event_id = kwargs['event_id']
        if "Authorization" not in request.headers:
            serializer = ErrorSerializer(data=asdict(Error("Authorization required")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_401_UNAUTHORIZED)
        else:
            token = request.headers["Authorization"]
        result = event_service.unparticipate_by_id(event_id=event_id, token=token)
        if isinstance(result, CodeTextError):
            serializer = ErrorSerializer(data=asdict(Error(result.text)))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=result.code)
        else:
            serializer = EventResponseSerializer(data=asdict(result))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data)

    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string")],
        responses={
            HTTP_200_OK: EventResponseSerializer(),
            HTTP_400_BAD_REQUEST: ErrorSerializer(),
            HTTP_401_UNAUTHORIZED: ErrorSerializer(),
            HTTP_404_NOT_FOUND: ErrorSerializer(),
        },
        tags=["Events"],
    )
    def post(self, request, **kwargs):
        if "Authorization" not in request.headers:
            serializer = ErrorSerializer(data=asdict(Error("Authorization required")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_401_UNAUTHORIZED)
        else:
            token = request.headers["Authorization"]
        event_id = kwargs['event_id']
        result = event_service.participate_by_id(event_id, token)
        if isinstance(result, CodeTextError):
            serializer = ErrorSerializer(data=asdict(Error(result.text)))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=result.code)
        else:
            serializer = EventResponseSerializer(data=asdict(result))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data)
