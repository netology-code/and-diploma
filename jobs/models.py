from django.db import models

from jobs.domain.job_dto import JobDto
from users.models import UserDetails


class JobModel(models.Model):
    id = models.BigAutoField(primary_key=True)
    user = models.ForeignKey(UserDetails, to_field='id', on_delete=models.CASCADE)
    name = models.TextField()
    position = models.TextField()
    start = models.DateTimeField()
    finish = models.DateTimeField(default=None, null=True)
    link = models.TextField(default=None, null=True)

    def to_dto(self) -> JobDto:
        return JobDto(
            id=self.id,
            name=self.name,
            position=self.position,
            start=self.start,
            finish=self.finish,
            link=self.link
        )
