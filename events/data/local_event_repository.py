from typing import Optional, Union

from django.db import transaction

from attachments.data.attachment_model import AttachmentModel
from coordinates.data.coordinates_model import CoordinatesModel
from events.domain.event_repository import EventRepository
from events.domain.models.event_dto import EventDto
from events.models import EventModel, EventLikes, EventParticipate, EventSpeakers
from nmedia.data.repositories import LocalRepository
from nmedia.domain.errors import TextError
from nmedia.domain.repository import ID, T
from users.models import UserDetails


class LocalEventRepository(EventRepository):
    _local_repository = LocalRepository[EventModel, int](EventModel.objects)
    _user_repository = LocalRepository[UserDetails, int](UserDetails.objects)

    @transaction.atomic
    def save(self, item: EventDto) -> Union[EventDto, TextError]:
        existing = self._local_repository.get_by_id(item.id)
        if existing is None:
            to_save = EventModel(
                author=self._user_repository.get_by_id(item.authorId),
                content=item.content,
                link=item.link,
                published=item.published,
                datetime=item.datetime,
                type=item.type,
            )
        else:
            existing.content = item.content
            existing.link = item.link
            if item.datetime is not None:
                existing.datetime = item.datetime
            to_save = existing
        coordinates = item.coords
        if coordinates is not None:
            to_save.coordinates = CoordinatesModel(
                lat=coordinates.lat,
                long=coordinates.long,
            )
            to_save.coordinates.save()
        attachment = item.attachment
        if attachment is not None:
            to_save.attachment = AttachmentModel(
                url=attachment.url,
                type=attachment.type.value,
            )
            to_save.attachment.save()
        self._local_repository.save(to_save)
        speaker_ids = item.speakerIds
        if speaker_ids is not None:
            if speaker_ids == set():
                EventSpeakers.objects.filter(event=to_save.id).delete()
            for speaker_id in speaker_ids:
                existing_speaker_id = EventSpeakers.objects.filter(user=speaker_id, event=to_save.id).first()
                if existing_speaker_id is None:
                    user = self._user_repository.get_by_id(speaker_id)
                    if user is None:
                        return TextError("Speaker with id " + str(speaker_id) + " not found")
                    EventSpeakers(user=user, event=to_save).save()
        return to_save.to_dto(item.authorId)

    def get_latest(self, count: int, user_id: Optional[int]) -> list[EventDto]:
        models = EventModel.objects.all().order_by('-id')[:count]
        return list(map(lambda model: model.to_dto(user_id), models))

    def get_all_with_id(self, user_id: int) -> list[EventDto]:
        return list(map(lambda model: model.to_dto(user_id), self._local_repository.get_all()))

    def like(self, event_id: int, user_id: int) -> EventDto:
        existing_like = EventLikes.objects.filter(event=event_id, user=user_id).first()
        if existing_like is None:
            EventLikes(event=EventModel.objects.get(id=event_id), user=UserDetails.objects.get(id=user_id)).save()
        return self.get_by_id_with_user_id(id=event_id, user_id=user_id)

    def unlike(self, event_id: int, user_id: int) -> EventDto:
        existing_like = EventLikes.objects.filter(event=event_id, user=user_id).first()
        if existing_like is not None:
            existing_like.delete()
        return self.get_by_id_with_user_id(id=event_id, user_id=user_id)

    def participate(self, event_id: int, user_id: int) -> EventDto:
        existing_participate = EventParticipate.objects.filter(event=event_id, user=user_id).first()
        if existing_participate is None:
            EventParticipate(event=EventModel.objects.get(id=event_id),
                             user=UserDetails.objects.get(id=user_id)).save()
        return self.get_by_id_with_user_id(id=event_id, user_id=user_id)

    def unparticipate(self, event_id: int, user_id: int) -> EventDto:
        existing_participate = EventParticipate.objects.filter(event=event_id, user=user_id).first()
        if existing_participate is not None:
            existing_participate.delete()
        return self.get_by_id_with_user_id(id=event_id, user_id=user_id)

    def get_after(self, id: int, count: int, user_id: Optional[int]) -> list[EventDto]:
        models = EventModel.objects.filter(id__gte=id + 1)[:count]
        result = list(map(lambda model: model.to_dto(user_id), models))
        return sorted(result, reverse=True, key=(lambda post: post.id))

    def get_before(self, id: int, count: int, user_id: Optional[int]) -> list[EventDto]:
        models = EventModel.objects.filter(id__lte=id - 1).order_by('-id')[:count]
        result = list(map(lambda model: model.to_dto(user_id), models))
        return result

    def get_by_id_with_user_id(self, id: int, user_id: int) -> EventDto:
        result = self._local_repository.get_by_id(id)
        return result.to_dto(user_id)

    def get_newer(self, id: int, user_id: Optional[int]) -> list[EventDto]:
        models = EventModel.objects.filter(id__gte=id + 1).order_by('-id')
        return list(map(lambda model: model.to_dto(user_id), models))

    def get_by_id(self, id: ID) -> Optional[T]:
        model = self._local_repository.get_by_id(id)
        if model is not None:
            return model.to_dto(None)
        else:
            return None

    def get_all(self) -> list[T]:
        return list(map(lambda model: model.to_dto(None), self._local_repository.get_all()))

    def delete_by_id(self, id: ID) -> None:
        self._local_repository.delete_by_id(id)
