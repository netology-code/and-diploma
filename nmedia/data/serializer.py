from rest_framework import serializers


class ErrorSerializer(serializers.Serializer):
    reason = serializers.CharField()
