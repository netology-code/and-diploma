from rest_framework import serializers


class CoordinatesSerializer(serializers.Serializer):
    lat = serializers.DecimalField(decimal_places=6, max_digits=8)
    long = serializers.DecimalField(decimal_places=6, max_digits=8)
