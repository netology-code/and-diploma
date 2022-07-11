from dataclasses import asdict

from django.http import JsonResponse
from rest_framework.status import HTTP_401_UNAUTHORIZED

from nmedia.data.serializer import ErrorSerializer
from nmedia.domain.errors import Error


def auth_error():
    serializer = ErrorSerializer(data=asdict(Error("Authorization required")))
    serializer.is_valid(raise_exception=True)
    return JsonResponse(serializer.data, status=HTTP_401_UNAUTHORIZED)