from typing import Optional, Union

from rest_framework.status import HTTP_403_FORBIDDEN

from jobs.domain.job_dto import JobDto
from jobs.domain.job_repository import JobRepository
from jobs.models import JobModel
from nmedia.data.repositories import LocalRepository
from nmedia.domain.errors import CodeTextError
from nmedia.domain.repository import ID, T
from users.models import UserDetails


class LocalJobRepository(JobRepository):
    _local_repository = LocalRepository[JobModel, int](JobModel.objects)

    def get_user_id(self, job_id: int) -> Optional[int]:
        existing_job: Optional[JobModel] = JobModel.objects.filter(id=job_id).first()
        if existing_job is None:
            return None
        else:
            return int(str(existing_job.user.id))

    def save(self, item: JobDto, user_id: int) -> Union[JobDto, CodeTextError]:
        existing_job = JobModel.objects.filter(id=item.id).first()
        if existing_job is None:
            existing_job = JobModel(
                name=item.name,
                position=item.position,
                start=item.start,
                finish=item.finish,
                link=item.link,
                user=UserDetails.objects.get(id=user_id)
            )
        else:
            existing_job.name = item.name
            existing_job.position = item.position
            existing_job.start = item.start
            existing_job.finish = item.finish
            existing_job.link = item.link
        if existing_job.user.id != user_id:
            return CodeTextError(HTTP_403_FORBIDDEN, "You must be the owner of this job")
        existing_job.save()
        return existing_job.to_dto()

    def get_all_by_user_id(self, user_id: int) -> list[JobDto]:
        user = UserDetails.objects.filter(id=user_id).first()
        if user is None:
            return list()
        jobs = JobModel.objects.filter(user=user)
        return list(map(lambda model: model.to_dto(), jobs))

    def get_by_id(self, id: ID) -> Optional[T]:
        model = self._local_repository.get_by_id(id)
        if model is not None:
            return model.to_dto()
        else:
            return None

    def get_all(self) -> list[T]:
        return list(map(lambda model: model.to_dto(), self._local_repository.get_all()))

    def delete_by_id(self, id: ID) -> None:
        self._local_repository.delete_by_id(id)
