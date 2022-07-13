from typing import Union, Optional

from rest_framework.status import HTTP_401_UNAUTHORIZED, HTTP_403_FORBIDDEN

from jobs.domain.job_dto import JobDto
from jobs.domain.job_repository import JobRepository
from nmedia.domain.errors import CodeTextError
from users.domain.models import UserDto
from users.domain.repositories import UserRepository


class JobService:
    _job_repository: JobRepository
    _user_repository: UserRepository

    def __init__(self, job_repository: JobRepository, user_repository: UserRepository):
        self._job_repository = job_repository
        self._user_repository = user_repository

    def get_by_id(self, job_id: int) -> Union[CodeTextError, JobDto]:
        job = self._job_repository.get_by_id(job_id)
        if job is None:
            return CodeTextError(404, "Job not found")
        else:
            return job

    def get_all_token(self, token: str) -> Union[CodeTextError, list[JobDto]]:
        user: UserDto = self._user_repository.get_by_token(token)
        if user is None:
            return CodeTextError(HTTP_401_UNAUTHORIZED, "Authorization required")
        else:
            user_id = user.id
        return self.get_all_by_user_id(user_id)

    def get_all_by_user_id(self, user_id: int) -> list[JobDto]:
        return self._job_repository.get_all_by_user_id(user_id=user_id)

    def save(self, request: JobDto, token: str) -> Union[JobDto, CodeTextError]:
        user: UserDto = self._user_repository.get_by_token(token)
        if user is None:
            return CodeTextError(text="Authorization required", code=HTTP_401_UNAUTHORIZED)
        return self._job_repository.save(item=request, user_id=user.id)

    def delete_by_id(self, job_id, token) -> Optional[CodeTextError]:
        user: UserDto = self._user_repository.get_by_token(token)
        job_user_id = self._job_repository.get_user_id(job_id)
        if job_user_id is None:
            return None
        if user is None:
            return CodeTextError(text="Authorization required", code=HTTP_401_UNAUTHORIZED)
        if job_user_id != user.id:
            return CodeTextError(text="You must be the owner of this job", code=HTTP_403_FORBIDDEN)
        self._job_repository.delete_by_id(job_id)
        return None
