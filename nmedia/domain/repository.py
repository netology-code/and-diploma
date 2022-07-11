from abc import abstractmethod
from typing import TypeVar, Generic, Optional

T = TypeVar('T')
ID = TypeVar('ID')


class Repository(Generic[T, ID]):
    @abstractmethod
    def get_by_id(self, id: ID) -> Optional[T]:
        pass

    @abstractmethod
    def get_all(self) -> list[T]:
        pass

    @abstractmethod
    def delete_by_id(self, id: ID) -> None:
        pass
