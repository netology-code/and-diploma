from typing import Optional

from django.db import models

from attachments.data.attachment_model import AttachmentModel
from coordinates.data.coordinates_model import CoordinatesModel
from events.domain.models.event_dto import EventDto
from events.domain.models.event_type import EventType
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
        like_owner_ids = EventLikes.objects.filter(event=self).values('user_id')
        like_owners = set(map(lambda like: like['user_id'], like_owner_ids))
        if user_id is not None:
            liked_by_me = user_id in like_owners
        else:
            liked_by_me = False
        participants_ids = EventParticipate.objects.filter(event=self).values('user_id')
        participants = set(map(lambda like: like['user_id'], participants_ids))
        if user_id is not None:
            participated_by_me = user_id in participants
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
        speaker_ids = EventSpeakers.objects.filter(event=self).values('user_id')
        speakers = set(map(lambda like: like['user_id'], speaker_ids))
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
            likeOwnerIds=like_owners,
            likedByMe=liked_by_me,
            speakerIds=speakers,
            participantsIds=participants,
            participatedByMe=participated_by_me,
            attachment=attachment,
            link=self.link,
            ownedByMe=self.author.id == user_id
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
