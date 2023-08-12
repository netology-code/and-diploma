from django.db import models

from coordinates.domain.coordinates_dto import CoordinatesDto


class CoordinatesModel(models.Model):
    lat = models.DecimalField(decimal_places=20, max_digits=23)
    long = models.DecimalField(decimal_places=20, max_digits=23)

    def to_dto(self) -> CoordinatesDto:
        return CoordinatesDto(lat=self.lat, long=self.long)
