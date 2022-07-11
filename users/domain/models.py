from dataclasses import dataclass


@dataclass
class UserDto:
    id: int
    login: str
    name: str
    avatar: str


@dataclass
class Token:
    id: int
    token: str

    @staticmethod
    def create(token) -> 'Token':
        return Token(0, token)
