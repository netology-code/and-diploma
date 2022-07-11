from typing import TypeVar, Optional

from django.db.models import Model, Manager

from nmedia.domain.repository import Repository

M = TypeVar('M', bound=Model)
ID = TypeVar('ID')


class LocalRepository(Repository[M, ID]):
    _manager: Manager

    def __init__(self, manager: Manager):
        self._manager = manager

    def get_by_id(self, id: ID) -> Optional[M]:
        return self._manager.filter(id=id).first()

    def get_all(self) -> list[M]:
        return self._manager.all().order_by('-id')

    def delete_by_id(self, id: ID) -> None:
        model = self.get_by_id(id=id)
        if model is not None:
            model.delete()

    @staticmethod
    def save(item: M) -> ID:
        item.save()
        return item.pk
