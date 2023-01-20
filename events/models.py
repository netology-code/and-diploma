from typing import Optional

from django.db import models

from attachments.data.attachment_model import AttachmentModel
from coordinates.data.coordinates_model import CoordinatesModel
from events.domain.models.event_dto import EventDto
from events.domain.models.event_type import EventType
from jobs.models import JobModel
from users.domain.models import UserPreviewDto
from users.models import UserDetails


class EventModel(models.Model):
    id = models.BigAutoField(primary_key=True)
    author = models.ForeignKey(UserDetails, on_delete=models.CASCADE)
    content = models.TextField()
    datetime = models.DateTimeField()
    published = models.DateTimeField()
    coordinates = models.OneToOneField(
        to=CoordinatesModel,
        on_delete=models.CASCADE,
        null=True,
        default=None,
    )
    type = models.TextField()
    attachment = models.OneToOneField(
        to=AttachmentModel,
        on_delete=models.CASCADE,
        null=True,
        default=None,
    )
    link = models.TextField(default=None, null=True)

    def to_dto(self, user_id: Optional[int]) -> EventDto:
        likes_user_ids_db = EventLikes.objects.filter(event=self).values('user_id')
        likes_user_ids = set(map(lambda like: like['user_id'], likes_user_ids_db))
        if user_id is not None:
            liked_by_me = user_id in likes_user_ids
        else:
            liked_by_me = False
        participants_ids_db = EventParticipate.objects.filter(event=self).values('user_id')
        participants_ids = set(map(lambda like: like['user_id'], participants_ids_db))
        if user_id is not None:
            participated_by_me = user_id in participants_ids
        else:
            participated_by_me = False
        if self.coordinates is not None:
            coordinates = self.coordinates.to_dto()
        else:
            coordinates = None
        if self.attachment is not None:
            attachment = self.attachment.to_dto()
        else:
            attachment = None
        speaker_ids_db = EventSpeakers.objects.filter(event=self).values('user_id')
        speakers_ids = set(map(lambda speaker: speaker['user_id'], speaker_ids_db))

        all_users = likes_user_ids | speakers_ids | participants_ids
        users = UserDetails.objects.filter(pk__in=all_users).order_by('-id')
        users_infos = list(map(lambda user: (user.id, UserPreviewDto(user.name, user.avatar)), users))
        user_id_to_users = dict(users_infos)

        last_job = JobModel.objects.filter(user=self.author.id).order_by('-start').values('name').first()
        if last_job is None:
            last_job_name = None
        else:
            last_job_name = last_job['name']

        return EventDto(
            id=self.id,
            authorId=self.author.id,
            author=self.author.name,
            authorAvatar=self.author.avatar,
            content=self.content,
            datetime=self.datetime,
            published=self.published,
            coords=coordinates,
            type=EventType.from_str(self.type),
            likeOwnerIds=likes_user_ids,
            likedByMe=liked_by_me,
            speakerIds=speakers_ids,
            participantsIds=participants_ids,
            participatedByMe=participated_by_me,
            attachment=attachment,
            link=self.link,
            ownedByMe=self.author.id == user_id,
            users=user_id_to_users,
            authorJob=last_job_name,
        )


class EventLikes(models.Model):
    id = models.BigAutoField(primary_key=True)
    user = models.ForeignKey(UserDetails, to_field='id', on_delete=models.CASCADE)
    event = models.ForeignKey(EventModel, to_field='id', on_delete=models.CASCADE)


class EventSpeakers(models.Model):
    id = models.BigAutoField(primary_key=True)
    user = models.ForeignKey(UserDetails, to_field='id', on_delete=models.CASCADE)
    event = models.ForeignKey(EventModel, to_field='id', on_delete=models.CASCADE)


class EventParticipate(models.Model):
    id = models.BigAutoField(primary_key=True)
    user = models.ForeignKey(UserDetails, to_field='id', on_delete=models.CASCADE)
    event = models.ForeignKey(EventModel, to_field='id', on_delete=models.CASCADE)
