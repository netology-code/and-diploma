from rest_framework import serializers


class CoordinatesSerializer(serializers.Serializer):
    lat = serializers.DecimalField(decimal_places=20, max_digits=22)
    long = serializers.DecimalField(decimal_places=20, max_digits=22)
