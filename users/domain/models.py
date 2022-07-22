from dataclasses import dataclass
from typing import Optional


@dataclass
class UserDto:
    id: int
    login: str
    name: str
    avatar: Optional[str]


@dataclass
class UserPreviewDto:
    name: str
    avatar: Optional[str]


@dataclass
class Token:
    id: int
    token: str

    @staticmethod
    def create(token) -> 'Token':
        return Token(0, token)
