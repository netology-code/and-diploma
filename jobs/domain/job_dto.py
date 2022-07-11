from dataclasses import dataclass
from datetime import datetime
from typing import Optional


@dataclass
class JobDto:
    id: int
    name: str
    position: str
    start: datetime
    finish: Optional[datetime]
    link: Optional[str]
