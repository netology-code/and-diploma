from rest_framework import serializers


class MediaResponseSerializer(serializers.Serializer):
    url = serializers.CharField()


class MediaCreateRequestSerializer(serializers.Serializer):
    file = serializers.FileField()
