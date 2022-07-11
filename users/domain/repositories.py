from abc import ABC, abstractmethod
from typing import Union, Optional

from nmedia.domain.errors import TextError
from nmedia.domain.repository import Repository
from users.domain.models import UserDto, Token


class UserRepository(Repository[UserDto, int], ABC):
    @abstractmethod
    def create_user(self, login: str, password: str, name: str, avatar: str) -> UserDto:
        pass

    @abstractmethod
    def get_by_token(self, token: Optional[str]) -> Optional[UserDto]:
        pass

    @abstractmethod
    def login(self, login: str, password: str) -> Union[UserDto, TextError]:
        pass

    @abstractmethod
    def save(self, item: UserDto) -> None:
        pass

    @abstractmethod
    def exists(self, login: str) -> bool:
        pass


class TokenRepository(Repository[Token, str], ABC):
    @abstractmethod
    def create_token(self, token: str, user: UserDto) -> Token:
        pass
