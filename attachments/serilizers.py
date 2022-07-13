from rest_framework import serializers

from attachments.domain.attachment_type import AttachmentType


class AttachmentSerializer(serializers.Serializer):
    url = serializers.CharField()
    type = serializers.ChoiceField(choices=AttachmentType)
