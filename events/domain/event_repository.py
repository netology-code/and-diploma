from abc import ABC, abstractmethod
from typing import Optional, Union

from events.domain.models.event_dto import EventDto
from nmedia.domain.errors import TextError
from nmedia.domain.repository import Repository


class EventRepository(Repository[EventDto, int], ABC):

    @abstractmethod
    def save(self, item: EventDto) -> Union[EventDto, TextError]:
        pass

    @abstractmethod
    def get_latest(self, count: int, user_id: Optional[int]) -> list[EventDto]:
        pass

    @abstractmethod
    def get_all_with_id(self, user_id: int) -> list[EventDto]:
        pass

    @abstractmethod
    def like(self, event_id: int, user_id: int) -> EventDto:
        pass

    @abstractmethod
    def unlike(self, event_id: int, user_id: int) -> EventDto:
        pass

    @abstractmethod
    def participate(self, event_id: int, user_id: int) -> EventDto:
        pass

    @abstractmethod
    def unparticipate(self, event_id: int, user_id: int) -> EventDto:
        pass

    @abstractmethod
    def get_after(self, id: int, count: int, user_id: Optional[int]) -> list[EventDto]:
        pass

    @abstractmethod
    def get_before(self, id: int, count: int, user_id: Optional[int]) -> list[EventDto]:
        pass

    @abstractmethod
    def get_by_id_with_user_id(self, id: int, user_id: int) -> EventDto:
        pass

    @abstractmethod
    def get_newer(self, id: int, user_id: Optional[int]) -> list[EventDto]:
        pass
