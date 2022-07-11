from rest_framework import serializers


class JobSerializer(serializers.Serializer):
    id = serializers.IntegerField()
    name = serializers.CharField()
    position = serializers.CharField()
    start = serializers.DateTimeField()
    finish = serializers.DateTimeField(required=False, allow_null=True, default=None)
    link = serializers.CharField(required=False, allow_null=True, default=None)
