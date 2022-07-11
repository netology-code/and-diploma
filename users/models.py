from django.db import models
from django.contrib.auth.models import AbstractUser
from django.db.models import Model, UUIDField

from users.domain.models import UserDto, Token


class UserDetails(AbstractUser):
    id = models.BigAutoField(primary_key=True)
    avatar = models.TextField(default=None, null=True)
    name = models.TextField()

    def to_dto(self) -> UserDto:
        return UserDto(int(str(self.id)), self.username, str(self.name), str(self.avatar))


class TokenModel(Model):
    token: UUIDField = models.UUIDField(primary_key=True, editable=False)
    user: UserDetails = models.ForeignKey(on_delete=models.CASCADE, to=UserDetails)

    def to_dto(self) -> Token:
        return Token(int(str(self.user.id)), str(self.token))
