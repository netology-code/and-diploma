from abc import ABC, abstractmethod
from typing import Optional, Union

from nmedia.domain.errors import TextError
from nmedia.domain.repository import Repository
from posts.domain.models.post_dto import PostDto


class PostRepository(Repository[PostDto, int], ABC):

    @abstractmethod
    def save(self, item: PostDto) -> Union[PostDto, TextError]:
        pass

    @abstractmethod
    def get_latest(self, count: int, user_id: Optional[int]) -> list[PostDto]:
        pass

    @abstractmethod
    def get_latest_by_author_id(self, count: int, author_id: int, user_id: Optional[int]) -> list[PostDto]:
        pass

    @abstractmethod
    def get_all_with_id(self, user_id: int) -> list[PostDto]:
        pass

    @abstractmethod
    def like(self, post_id: int, user_id: int) -> PostDto:
        pass

    @abstractmethod
    def unlike(self, post_id: int, user_id: int) -> PostDto:
        pass

    @abstractmethod
    def get_after(self, id: int, count: int, user_id: Optional[int]) -> list[PostDto]:
        pass

    @abstractmethod
    def get_after_by_author_id(self, id: int, author_id: int, count: int, user_id: Optional[int]) -> list[PostDto]:
        pass

    @abstractmethod
    def get_before(self, id: int, count: int, user_id: Optional[int]) -> list[PostDto]:
        pass

    @abstractmethod
    def get_before_by_author_id(self, id: int, author_id: int, count: int, user_id: Optional[int]) -> list[PostDto]:
        pass

    @abstractmethod
    def get_by_id_with_user_id(self, id: int, user_id: int) -> Optional[PostDto]:
        pass

    @abstractmethod
    def get_newer(self, id: int, user_id: Optional[int]) -> list[PostDto]:
        pass

    @abstractmethod
    def get_newer_by_author_id(self, id: int, author_id: int, user_id: Optional[int]) -> list[PostDto]:
        pass

    @abstractmethod
    def get_all_by_author_id(self, author_id: int, user_id: Optional[int]) -> list[PostDto]:
        pass
