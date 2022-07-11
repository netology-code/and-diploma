from typing import Optional

from django.db import transaction

from nmedia.data.repositories import LocalRepository
from nmedia.domain.repository import ID, T
from posts.domain.models.post_dto import PostDto
from posts.domain.post_repository import PostRepository
from posts.models import Post, CoordinatesModel, AttachmentModel, PostLikes
from users.models import UserDetails


class LocalPostRepository(PostRepository):
    _local_repository = LocalRepository[Post, int](Post.objects)
    _user_repository = LocalRepository[UserDetails, int](UserDetails.objects)

    def get_newer(self, id: int, user_id: Optional[int]) -> list[PostDto]:
        models = Post.objects.filter(id__gte=id + 1).order_by('-id')
        return list(map(lambda model: model.to_dto(user_id), models))

    def get_newer_by_author_id(self, id: int, author_id: int, user_id: Optional[int]) -> list[PostDto]:
        author = UserDetails.objects.filter(id=author_id).first()
        if author is None:
            return list()
        models = Post.objects.filter(id__gte=id + 1, author=author).order_by('-id')
        return list(map(lambda model: model.to_dto(user_id), models))

    def get_latest(self, count: int, user_id: Optional[int]) -> list[PostDto]:
        models = Post.objects.all().order_by('-id')[:count]
        return list(map(lambda model: model.to_dto(user_id), models))

    def get_latest_by_author_id(self, count: int, author_id: int, user_id: Optional[int]) -> list[PostDto]:
        author = UserDetails.objects.filter(id=author_id).first()
        if author is None:
            return list()
        models = Post.objects.filter(author=author).order_by('-id')[:count]
        return list(map(lambda model: model.to_dto(user_id), models))

    @transaction.atomic
    def save(self, item: PostDto) -> PostDto:
        existing = self._local_repository.get_by_id(item.id)
        if existing is None:
            to_save = Post(
                author=self._user_repository.get_by_id(item.authorId),
                content=item.content,
                link=item.link,
                published=item.published,
            )
        else:
            existing.content = item.content
            existing.link = item.link
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
        return to_save.to_dto(item.authorId)

    def get_by_id(self, id: ID) -> Optional[T]:
        model = self._local_repository.get_by_id(id)
        if model is not None:
            return model.to_dto(None)
        else:
            return None

    def get_by_id_with_user_id(self, id: int, user_id: int) -> PostDto:
        result = self.get_by_id(id)
        if user_id in result.likeOwnerIds:
            liked_by_me = True
        else:
            liked_by_me = False
        result.liked_by_me = liked_by_me
        return result

    def get_all(self) -> list[T]:
        return list(map(lambda model: model.to_dto(None), self._local_repository.get_all()))

    def get_all_with_id(self, user_id: int) -> list[PostDto]:
        return list(map(lambda model: model.to_dto(user_id), self._local_repository.get_all()))

    def delete_by_id(self, id: ID) -> None:
        self._local_repository.delete_by_id(id)

    def get_after(self, id: int, count: int, user_id: Optional[int]) -> list[PostDto]:
        models = Post.objects.filter(id__gte=id + 1)[:count]
        result = list(map(lambda model: model.to_dto(user_id), models))
        return sorted(result, reverse=True, key=(lambda post: post.id))

    def get_after_by_author_id(self, id: int, author_id: int, count: int, user_id: Optional[int]) -> list[PostDto]:
        author = UserDetails.objects.filter(id=author_id).first()
        if author is None:
            return list()
        models = Post.objects.filter(id__gte=id + 1, author=author)[:count]
        result = list(map(lambda model: model.to_dto(user_id), models))
        return sorted(result, reverse=True, key=(lambda post: post.id))

    def get_before(self, id: int, count: int, user_id: Optional[int]) -> list[PostDto]:
        models = Post.objects.filter(id__lte=id - 1).order_by('-id')[:count]
        result = list(map(lambda model: model.to_dto(user_id), models))
        return result

    def get_before_by_author_id(self, id: int, author_id: int, count: int, user_id: Optional[int]) -> list[PostDto]:
        author = UserDetails.objects.filter(id=author_id).first()
        if author is None:
            return list()
        models = Post.objects.filter(id__lte=id - 1, author=author).order_by('-id')[:count]
        result = list(map(lambda model: model.to_dto(user_id), models))
        return result

    def like(self, post_id: int, user_id: int) -> PostDto:
        existing_like = PostLikes.objects.filter(post_id=post_id, user_id=user_id).first()
        if existing_like is None:
            PostLikes(post_id=Post.objects.get(id=post_id), user_id=UserDetails.objects.get(id=user_id)).save()
        return self.get_by_id_with_user_id(id=post_id, user_id=user_id)

    def unlike(self, post_id: int, user_id: int) -> PostDto:
        existing_like = PostLikes.objects.filter(post_id=post_id, user_id=user_id).first()
        if existing_like is not None:
            existing_like.delete()
        return self.get_by_id_with_user_id(id=post_id, user_id=user_id)

    def get_all_by_author_id(self, author_id: int, user_id: Optional[int]) -> list[PostDto]:
        author = UserDetails.objects.filter(id=author_id).first()
        if author is None:
            return list()
        posts = Post.objects.filter(author=author)
        return list(map(lambda model: model.to_dto(user_id), posts))
