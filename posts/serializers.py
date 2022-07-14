from rest_framework import serializers

from attachments.serilizers import AttachmentSerializer
from coordinates.serilizers import CoordinatesSerializer


class PostResponseSerializer(serializers.Serializer):
    id = serializers.IntegerField()
    authorId = serializers.IntegerField()
    author = serializers.CharField()
    authorAvatar = serializers.CharField(required=False, allow_null=True, default=None)
    content = serializers.CharField()
    published = serializers.DateTimeField()
    coords = CoordinatesSerializer(required=False, allow_null=True, default=None)
    link = serializers.CharField(required=False, allow_null=True, default=None)
    likeOwnerIds = serializers.ListField(child=serializers.IntegerField(), default=list())
    mentionIds = serializers.ListField(child=serializers.IntegerField(), default=list())
    mentionedMe = serializers.BooleanField()
    likedByMe = serializers.BooleanField()
    attachment = AttachmentSerializer(required=False, allow_null=True, default=None)
    ownedByMe = serializers.BooleanField()


class PostCreateRequestSerializer(serializers.Serializer):
    id = serializers.IntegerField()
    content = serializers.CharField()
    coords = CoordinatesSerializer(required=False, allow_null=True, default=None)
    link = serializers.CharField(required=False, allow_null=True, default=None)
    attachment = AttachmentSerializer(required=False, allow_null=True, default=None)
    mentionIds = serializers.ListField(child=serializers.IntegerField(), default=list())
