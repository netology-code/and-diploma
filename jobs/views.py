from dataclasses import asdict

from django.http import JsonResponse
from drf_yasg.openapi import IN_HEADER, Parameter
from drf_yasg.utils import swagger_auto_schema
from rest_framework.generics import RetrieveAPIView, CreateAPIView, DestroyAPIView
from rest_framework.status import HTTP_401_UNAUTHORIZED, HTTP_200_OK, HTTP_403_FORBIDDEN
from jobs.domain.job_dto import JobDto
from jobs.domain.job_service import JobService
from jobs.serializers import JobSerializer
from nmedia.data.serializer import ErrorSerializer
from nmedia.dependencies import DependencyContainer
from nmedia.domain.errors import Error, CodeTextError

job_service: JobService = DependencyContainer.get_instance().job_service


class JobGetAllByUserIdView(RetrieveAPIView):
    serializer_class = JobSerializer

    @swagger_auto_schema(
        tags=["Jobs"],
    )
    def get(self, request, *args, **kwargs):
        user_id = int(kwargs['user_id'])
        jobs = job_service.get_all_by_user_id(user_id)
        serializer = self.get_serializer(data=list(map(lambda item: asdict(item), jobs)), many=True)
        serializer.is_valid(raise_exception=True)
        return JsonResponse(serializer.data, safe=False)


class JobGetAllOrCreateView(RetrieveAPIView, CreateAPIView):
    serializer_class = JobSerializer

    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string")],
        responses={
            HTTP_200_OK: JobSerializer(),
            HTTP_401_UNAUTHORIZED: ErrorSerializer(),
        },
        tags=["Jobs"],
    )
    def get(self, request, *args, **kwargs):
        if "Authorization" not in request.headers:
            serializer = ErrorSerializer(data=asdict(Error("Authorization required")))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=HTTP_401_UNAUTHORIZED)
        else:
            token = request.headers["Authorization"]
        result = job_service.get_all_token(token)
        if isinstance(result, CodeTextError):
            serializer = ErrorSerializer(data=asdict(Error(result.text)))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=result.code)
        else:
            serializer = self.get_serializer(data=list(map(lambda item: asdict(item), result)), many=True)
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, safe=False)

    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string")],
        responses={
            HTTP_200_OK: JobSerializer(),
            HTTP_401_UNAUTHORIZED: ErrorSerializer(),
            HTTP_403_FORBIDDEN: ErrorSerializer(),
        },
        request_body=JobSerializer,
        tags=["Jobs"],
    )
    def post(self, request, *args, **kwargs):
        request_serializer = self.get_serializer(data=request.data)
        request_serializer.is_valid(raise_exception=True)
        job = JobDto(
            id=request_serializer.validated_data['id'],
            name=request_serializer.validated_data['name'],
            position=request_serializer.validated_data['position'],
            start=request_serializer.validated_data['start'],
            finish=request_serializer.validated_data['finish'],
            link=request_serializer.validated_data['link'],
        )
        if "Authorization" not in request.headers:
            request_serializer = ErrorSerializer(data=asdict(Error("Authorization required")))
            request_serializer.is_valid(raise_exception=True)
            return JsonResponse(request_serializer.data, status=HTTP_401_UNAUTHORIZED)
        else:
            token = request.headers["Authorization"]
        created_job = job_service.save(job, token)
        if type(created_job) is JobDto:
            response_serializer = self.get_serializer(data=asdict(created_job))
            response_serializer.is_valid(raise_exception=True)
            return JsonResponse(response_serializer.data)
        else:
            serializer = ErrorSerializer(data=asdict(Error(created_job.text)))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=created_job.code)


class JobDeleteByIdView(DestroyAPIView):
    @swagger_auto_schema(
        manual_parameters=[Parameter(in_=IN_HEADER, name="Authorization", required=True, type="string")],
        responses={
            HTTP_200_OK: "{}",
            HTTP_401_UNAUTHORIZED: ErrorSerializer(),
            HTTP_403_FORBIDDEN: ErrorSerializer(),
        },
        tags=["Jobs"],
    )
    def delete(self, request, *args, **kwargs):
        if "Authorization" not in request.headers:
            request_serializer = ErrorSerializer(data=asdict(Error("Authorization required")))
            request_serializer.is_valid(raise_exception=True)
            return JsonResponse(request_serializer.data, status=HTTP_401_UNAUTHORIZED)
        else:
            token = request.headers["Authorization"]
        job_id = int(kwargs['job_id'])
        result = job_service.delete_by_id(job_id=job_id, token=token)
        if isinstance(result, CodeTextError):
            serializer = ErrorSerializer(data=asdict(Error(result.text)))
            serializer.is_valid(raise_exception=True)
            return JsonResponse(serializer.data, status=result.code)
        else:
            return JsonResponse(data=dict(), safe=False)
