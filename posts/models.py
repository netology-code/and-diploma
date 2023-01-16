# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from typing import Optional

from django.db import models

from attachments.data.attachment_model import AttachmentModel
from coordinates.data.coordinates_model import CoordinatesModel
from jobs.models import JobModel
from posts.domain.models.post_dto import PostDto
from users.domain.models import UserPreviewDto
from users.models import UserDetails


class Post(models.Model):
    id = models.BigAutoField(primary_key=True)
    author = models.ForeignKey('users.UserDetails', on_delete=models.CASCADE)
    content = models.TextField()
    published = models.DateTimeField()
    coordinates = models.OneToOneField(
        to=CoordinatesModel,
        on_delete=models.CASCADE,
        null=True,
        default=None,
    )
    attachment = models.OneToOneField(
        to=AttachmentModel,
        on_delete=models.CASCADE,
        null=True,
        default=None,
    )
    link = models.TextField(default=None, null=True)

    def to_dto(self, user_id: Optional[int]) -> PostDto:
        if self.coordinates is not None:
            coordinates = self.coordinates.to_dto()
        else:
            coordinates = None
        if self.attachment is not None:
            attachment = self.attachment.to_dto()
        else:
            attachment = None
        likes_user_ids_db = PostLikes.objects.filter(post_id=self.id).values('user_id')
        likes_user_ids = set(map(lambda like: like['user_id'], likes_user_ids_db))

        mentions_user_ids_db = PostMentions.objects.filter(post_id=self.id).values('user_id')
        mentions_user_ids = set(map(lambda mention: mention['user_id'], mentions_user_ids_db))

        all_users = likes_user_ids | mentions_user_ids
        users = UserDetails.objects.filter(pk__in=all_users).order_by('-id')
        users_infos = list(map(lambda user: UserPreviewDto(user.name, user.avatar), users))
        user_id_to_users = dict(zip(all_users, users_infos))

        if user_id is not None:
            liked_by_me = user_id in likes_user_ids
            mentioned_me = user_id in mentions_user_ids
        else:
            liked_by_me = False
            mentioned_me = False

        last_job = JobModel.objects.filter(user=self.author.id).order_by('-start').values('name').first()
        if last_job is None:
            last_job_name = None
        else:
            last_job_name = last_job['name']

        return PostDto(
            id=self.id,
            authorId=self.author.id,
            authorAvatar=self.author.avatar,
            content=self.content,
            published=self.published,
            coords=coordinates,
            link=self.link,
            likeOwnerIds=likes_user_ids,
            likedByMe=liked_by_me,
            author=self.author.name,
            attachment=attachment,
            mentionIds=mentions_user_ids,
            mentionedMe=mentioned_me,
            ownedByMe=self.author.id == user_id,
            users=user_id_to_users,
            authorJob=last_job_name,
        )


class PostLikes(models.Model):
    id = models.BigAutoField(primary_key=True)
    user_id = models.ForeignKey('users.UserDetails', to_field='id', on_delete=models.CASCADE)
    post_id = models.ForeignKey(Post, to_field='id', on_delete=models.CASCADE)


class PostMentions(models.Model):
    id = models.BigAutoField(primary_key=True)
    user_id = models.ForeignKey('users.UserDetails', to_field='id', on_delete=models.CASCADE)
    post_id = models.ForeignKey(Post, to_field='id', on_delete=models.CASCADE)
