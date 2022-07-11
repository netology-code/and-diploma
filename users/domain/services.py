import uuid
from typing import Union, Optional

from nmedia.domain.errors import TextError
from users.domain.models import Token, UserDto
from users.domain.repositories import UserRepository, TokenRepository


class UserService:
    _user_repository: UserRepository
    _token_repository: TokenRepository

    def __init__(self, user_repository: UserRepository, token_repository: TokenRepository):
        self._user_repository = user_repository
        self._token_repository = token_repository

    def get_all(self) -> list[UserDto]:
        return self._user_repository.get_all()

    def register(self, login: str, password: str, name: str, avatar: str) -> Union[Token, TextError]:
        if self._user_repository.exists(login):
            return TextError("User already registered")
        user = self._user_repository.create_user(login=login, password=password, name=name, avatar=avatar)
        token = self._token_repository.create_token(token=str(uuid.uuid4()), user=user)
        return token

    def login(self, login: str, password: str) -> Union[Token, TextError]:
        user = self._user_repository.login(login=login, password=password)
        if type(user) is TextError:
            return user
        token = self._token_repository.create_token(token=str(uuid.uuid4()), user=user)
        return token

    def get_by_id(self, id: int) -> UserDto:
        return self._user_repository.get_by_id(id)

    def get_by_token(self, token: Optional[str]) -> Optional[UserDto]:
        return self._user_repository.get_by_token(token)
