import decimal
from dataclasses import dataclass


@dataclass
class CoordinatesDto:
    lat: decimal
    long: decimal
