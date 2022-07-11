from typing import Optional, Union

from rest_framework.status import HTTP_401_UNAUTHORIZED, HTTP_403_FORBIDDEN, HTTP_400_BAD_REQUEST, HTTP_404_NOT_FOUND

from events.domain.event_repository import EventRepository
from events.domain.models.event_create_request import EventCreateRequest
from events.domain.models.event_dto import EventDto
from nmedia.domain.errors import CodeTextError, TextError
from users.domain.models import UserDto
from users.domain.repositories import UserRepository


class EventService:
    _event_repository: EventRepository
    _user_repository: UserRepository

    def __init__(self, event_repository: EventRepository, user_repository: UserRepository):
        self._event_repository = event_repository
        self._user_repository = user_repository

    def get_all(self, token: Optional[str]) -> list[EventDto]:
        user = self._user_repository.get_by_token(token)
        if user is None:
            return self._event_repository.get_all()
        else:
            return self._event_repository.get_all_with_id(user_id=user.id)

    def delete_by_id(self, event_id, token) -> Optional[CodeTextError]:
        user: UserDto = self._user_repository.get_by_token(token)
        existing_event = self._event_repository.get_by_id(event_id)
        if existing_event is None:
            return None
        if user is None:
            return CodeTextError(text="Authorization required", code=HTTP_401_UNAUTHORIZED)
        if existing_event.authorId != user.id:
            return CodeTextError(text="You must be the owner of this event", code=HTTP_403_FORBIDDEN)
        self._event_repository.delete_by_id(event_id)
        return None

    def get_newer(self, id: int, token: Optional[str]) -> list[EventDto]:
        user = self._user_repository.get_by_token(token)
        if user is None:
            user_id = None
        else:
            user_id = user.id
        return self._event_repository.get_newer(id=id, user_id=user_id)

    def get_before(self, id: int, count: int, token: Optional[str]) -> list[EventDto]:
        user = self._user_repository.get_by_token(token)
        if user is None:
            user_id = None
        else:
            user_id = user.id
        return self._event_repository.get_before(id=id, count=count, user_id=user_id)

    def get_after(self, id: int, count: int, token: Optional[str]) -> list[EventDto]:
        user = self._user_repository.get_by_token(token)
        if user is None:
            user_id = None
        else:
            user_id = user.id
        return self._event_repository.get_after(id=id, count=count, user_id=user_id)

    def get_latest(self, count: int, token: Optional[str]) -> list[EventDto]:
        user = self._user_repository.get_by_token(token)
        if user is None:
            user_id = None
        else:
            user_id = user.id
        return self._event_repository.get_latest(count, user_id)

    def get_by_id(self, event_id: int, token: Optional[str]) -> Union[CodeTextError, EventDto]:
        if token is None:
            event = self._event_repository.get_by_id(event_id)
        else:
            user: UserDto = self._user_repository.get_by_token(token)
            if user is None:
                return CodeTextError(HTTP_404_NOT_FOUND, "User not found")
            event = self._event_repository.get_by_id_with_user_id(event_id, user.id)
        if event is None:
            return CodeTextError(HTTP_404_NOT_FOUND, "event not found")
        else:
            return event

    def like_by_id(self, event_id, token) -> Union[CodeTextError, EventDto]:
        user: UserDto = self._user_repository.get_by_token(token)
        if user is None:
            return CodeTextError(HTTP_401_UNAUTHORIZED, "User not found")
        existing_event = self._event_repository.get_by_id(event_id)
        if existing_event is None:
            return CodeTextError(HTTP_404_NOT_FOUND, "event not found")
        else:
            return self._event_repository.like(event_id=event_id, user_id=user.id)

    def unlike_by_id(self, event_id, token) -> Union[CodeTextError, EventDto]:
        user: UserDto = self._user_repository.get_by_token(token)
        if user is None:
            return CodeTextError(404, "User not found")
        existing_event = self._event_repository.get_by_id(event_id)
        if existing_event is None:
            return CodeTextError(404, "event not found")
        else:
            return self._event_repository.unlike(event_id=event_id, user_id=user.id)

    def save(self, request: EventCreateRequest, token: str) -> Union[EventDto, CodeTextError]:
        user: UserDto = self._user_repository.get_by_token(token)
        if user is None:
            return CodeTextError(HTTP_401_UNAUTHORIZED, "User not found")
        existing_event = self._event_repository.get_by_id(request.id)
        if existing_event is None:
            if request.type is None:
                return CodeTextError(HTTP_400_BAD_REQUEST, "Type is required for creation")
            existing_event = EventDto.from_request(request, user)
        else:
            existing_event.content = request.content
            existing_event.coords = request.coords
            existing_event.link = request.link
            existing_event.attachment = request.attachment
            existing_event.speaker_ids = request.speaker_ids
        if existing_event.authorId != user.id:
            return CodeTextError(HTTP_403_FORBIDDEN, "You must be the owner of this event")
        result = self._event_repository.save(existing_event)
        if type(result) is TextError:
            return CodeTextError(HTTP_400_BAD_REQUEST, result.reason)
        else:
            return result

    def participate_by_id(self, event_id, token) -> Union[CodeTextError, EventDto]:
        user: UserDto = self._user_repository.get_by_token(token)
        if user is None:
            return CodeTextError(HTTP_401_UNAUTHORIZED, "User not found")
        existing_event = self._event_repository.get_by_id(event_id)
        if existing_event is None:
            return CodeTextError(HTTP_404_NOT_FOUND, "event not found")
        else:
            return self._event_repository.participate(event_id=event_id, user_id=user.id)

    def unparticipate_by_id(self, event_id, token) -> Union[CodeTextError, EventDto]:
        user: UserDto = self._user_repository.get_by_token(token)
        if user is None:
            return CodeTextError(HTTP_401_UNAUTHORIZED, "User not found")
        existing_event = self._event_repository.get_by_id(event_id)
        if existing_event is None:
            return CodeTextError(HTTP_404_NOT_FOUND, "event not found")
        else:
            return self._event_repository.unparticipate(event_id=event_id, user_id=user.id)
