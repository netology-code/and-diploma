from datetime import datetime
from dataclasses import dataclass
from typing import Optional

from attachments.domain.attachment import Attachment
from coordinates.domain.coordinates_dto import CoordinatesDto
from posts.domain.models.post_create_request import PostCreateRequest
from users.domain.models import UserDto


@dataclass
class PostDto:
    id: int
    authorId: int
    author: str
    authorAvatar: Optional[str]
    content: str
    published: datetime
    coords: Optional[CoordinatesDto]
    link: Optional[str]
    likeOwnerIds: set[int]
    mentionIds: set[int]
    mentionedMe: bool
    likeOwnerIds: set[int]
    likedByMe: bool
    attachment: Optional[Attachment]

    @staticmethod
    def from_request(request: PostCreateRequest, author: UserDto) -> 'PostDto':
        return PostDto(
            id=request.id,
            authorId=author.id,
            author=author.name,
            authorAvatar=author.avatar,
            content=request.content,
            published=datetime.now(),
            coords=request.coords,
            link=request.link,
            likeOwnerIds=set(),
            likedByMe=False,
            attachment=request.attachment,
            mentionIds=request.mentionIds,
            mentionedMe=author.id in request.mentionIds
        )
