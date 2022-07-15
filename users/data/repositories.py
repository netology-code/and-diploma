from typing import Union, Optional

from django.contrib.auth import authenticate

from nmedia.data.repositories import LocalRepository
from nmedia.domain.errors import TextError
from users.domain.models import UserDto, Token
from users.domain.repositories import UserRepository, TokenRepository
from users.models import UserDetails, TokenModel


class LocalUserRepository(UserRepository):
    local_repository = LocalRepository[UserDetails, int](UserDetails.objects)

    def get_by_token(self, token: Optional[str]) -> Optional[UserDto]:
        if token is None:
            return None
        token = TokenModel.objects.filter(token=token).first()
        if token is None:
            return None
        user = token.user.to_dto()
        return user

    def exists(self, login: str) -> bool:
        return UserDetails.objects.filter(username=login).exists()

    def login(self, login: str, password: str) -> Union[UserDto, TextError]:
        user = authenticate(username=login, password=password)
        if user is None:
            return TextError("Incorrect password")
        saved_user = self.get_by_id(user.id)
        if saved_user is None:
            return TextError("User not registered")
        return saved_user

    def create_user(self, login: str, password: str, name: str, avatar: Optional[str]) -> UserDto:
        user = UserDetails.objects.create_user(username=login, password=password)
        user.name = name
        if avatar is not None:
            user.avatar = avatar
        user.save()
        return user.to_dto()

    def get_by_id(self, id: int) -> Optional[UserDto]:
        user = self.local_repository.get_by_id(id)
        if user is None:
            return None
        return user.to_dto()

    def get_all(self) -> list[UserDto]:
        return list(map(lambda model: model.to_dto(), self.local_repository.get_all()))

    def delete_by_id(self, id: int) -> None:
        self.local_repository.delete_by_id(id)

    def save(self, item: UserDto) -> None:
        user = self.local_repository.get_by_id(item.id)
        user.name = item.name
        user.login = item.login
        user.save()


class LocalTokenRepository(TokenRepository):
    token_repository = LocalRepository[TokenModel, str](TokenModel.objects)
    user_repository = LocalRepository[UserDetails, int](UserDetails.objects)

    def create_token(self, token: str, user: UserDto) -> Token:
        model = TokenModel.objects.create(token=token, user=self.user_repository.get_by_id(user.id))
        return model.to_dto()

    def get_by_id(self, id: str) -> Optional[Token]:
        token = self.token_repository.get_by_id(id)
        if token is None:
            return None
        return self.token_repository.get_by_id(id).to_dto()

    def get_all(self) -> list[Token]:
        return list(map(lambda model: model.to_dto(), self.token_repository.get_all()))

    def delete_by_id(self, id: str) -> None:
        self.token_repository.delete_by_id(id)
