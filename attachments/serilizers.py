from rest_framework import serializers


class AttachmentSerializer(serializers.Serializer):
    url = serializers.CharField()
    type = serializers.CharField()
