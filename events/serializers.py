from rest_framework import serializers

from attachments.serilizers import AttachmentSerializer
from coordinates.serilizers import CoordinatesSerializer
from events.domain.models.event_type import EventType
from users.serializers import UserPreviewSerializer


class EventResponseSerializer(serializers.Serializer):
    id = serializers.IntegerField()
    authorId = serializers.IntegerField()
    author = serializers.CharField()
    authorAvatar = serializers.CharField(required=False, allow_null=True, default=None)
    authorJob = serializers.CharField(required=False, allow_null=True, default=None)
    content = serializers.CharField()
    datetime = serializers.DateTimeField()
    published = serializers.DateTimeField()
    coords = CoordinatesSerializer(required=False, allow_null=True, default=None)
    type = serializers.ChoiceField(choices=EventType)
    likeOwnerIds = serializers.ListField(child=serializers.IntegerField(), default=list())
    likedByMe = serializers.BooleanField()
    speakerIds = serializers.ListField(child=serializers.IntegerField(), default=list())
    participantsIds = serializers.ListField(child=serializers.IntegerField(), default=list())
    participatedByMe = serializers.BooleanField()
    attachment = AttachmentSerializer(required=False, allow_null=True, default=None)
    link = serializers.CharField(required=False, allow_null=True, default=None)
    ownedByMe = serializers.BooleanField()
    users = serializers.DictField(child=UserPreviewSerializer())


class EventCreateRequestSerializer(serializers.Serializer):
    id = serializers.IntegerField()
    content = serializers.CharField()
    datetime = serializers.DateTimeField(required=True, allow_null=False)
    coords = CoordinatesSerializer(required=False, allow_null=True, default=None)
    type = serializers.ChoiceField(choices=EventType, required=False, allow_null=True, default=None)
    attachment = AttachmentSerializer(required=False, allow_null=True, default=None)
    link = serializers.CharField(required=False, allow_null=True, default=None)
    speakerIds = serializers.ListField(child=serializers.IntegerField(), required=False, allow_null=True, default=None)
