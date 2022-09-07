from django.db import models

from coordinates.domain.coordinates_dto import CoordinatesDto


class CoordinatesModel(models.Model):
    lat = models.DecimalField(decimal_places=6, max_digits=9)
    long = models.DecimalField(decimal_places=6, max_digits=9)

    def to_dto(self) -> CoordinatesDto:
        return CoordinatesDto(lat=self.lat, long=self.long)
