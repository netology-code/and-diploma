# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from typing import Optional

from django.db import models

from attachments.data.attachment_model import AttachmentModel
from coordinates.data.coordinates_model import CoordinatesModel
from posts.domain.models.post_dto import PostDto


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
        likes_user_ids = PostLikes.objects.filter(post_id=self.id).values('user_id')
        likes = set(map(lambda like: like['user_id'], likes_user_ids))
        mentions_user_ids = PostMentions.objects.filter(post_id=self.id).values('user_id')
        mentions = set(map(lambda mention: mention['user_id'], mentions_user_ids))
        if user_id is not None:
            liked_by_me = user_id in likes
            mentioned_me = user_id in mentions
        else:
            liked_by_me = False
            mentioned_me = False
        return PostDto(
            id=self.id,
            authorId=self.author.id,
            authorAvatar=self.author.avatar,
            content=self.content,
            published=self.published,
            coords=coordinates,
            link=self.link,
            likeOwnerIds=likes,
            likedByMe=liked_by_me,
            author=self.author.name,
            attachment=attachment,
            mentionIds=mentions,
            mentionedMe=mentioned_me,
        )


class PostLikes(models.Model):
    id = models.BigAutoField(primary_key=True)
    user_id = models.ForeignKey('users.UserDetails', to_field='id', on_delete=models.CASCADE)
    post_id = models.ForeignKey(Post, to_field='id', on_delete=models.CASCADE)


class PostMentions(models.Model):
    id = models.BigAutoField(primary_key=True)
    user_id = models.ForeignKey('users.UserDetails', to_field='id', on_delete=models.CASCADE)
    post_id = models.ForeignKey(Post, to_field='id', on_delete=models.CASCADE)
