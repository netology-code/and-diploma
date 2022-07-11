from abc import ABC, abstractmethod
from typing import Union, Optional

from jobs.domain.job_dto import JobDto
from nmedia.domain.errors import CodeTextError
from nmedia.domain.repository import Repository


class JobRepository(Repository[JobDto, int], ABC):

    @abstractmethod
    def save(self, item: JobDto, user_id: int) -> Union[JobDto, CodeTextError]:
        pass

    @abstractmethod
    def get_all_by_user_id(self, user_id: int) -> list[JobDto]:
        pass

    @abstractmethod
    def get_user_id(self, job_id: int) -> Optional[int]:
        pass
