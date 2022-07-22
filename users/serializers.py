from rest_framework import serializers


class RegistrationRequestSerializer(serializers.Serializer):
    login = serializers.CharField()
    password = serializers.CharField()
    name = serializers.CharField()
    file = serializers.FileField(required=False, allow_null=True, default=None)


class TokenSerializer(serializers.Serializer):
    id = serializers.IntegerField()
    token = serializers.CharField()


class UserPreviewSerializer(serializers.Serializer):
    name = serializers.CharField()
    avatar = serializers.CharField(required=False, allow_null=True, default=None)


class AuthenticationRequestSerializer(serializers.Serializer):
    login = serializers.CharField()
    password = serializers.CharField()


class UserResponseSerializer(serializers.Serializer):
    id = serializers.IntegerField()
    login = serializers.CharField()
    name = serializers.CharField()
    avatar = serializers.CharField(required=False, allow_null=True, default=None)
