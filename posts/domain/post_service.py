from typing import Union, Optional

from rest_framework.status import HTTP_404_NOT_FOUND, HTTP_401_UNAUTHORIZED, HTTP_400_BAD_REQUEST, HTTP_403_FORBIDDEN
from nmedia.domain.errors import CodeTextError, TextError
from posts.domain.models.post_create_request import PostCreateRequest
from posts.domain.models.post_dto import PostDto
from posts.domain.post_repository import PostRepository
from users.domain.models import UserDto
from users.domain.repositories import UserRepository


class PostService:
    _post_repository: PostRepository
    _user_repository: UserRepository

    def __init__(self, post_repository: PostRepository, user_repository: UserRepository):
        self._post_repository = post_repository
        self._user_repository = user_repository

    def get_by_id(self, post_id: int, token: Optional[str]) -> Union[CodeTextError, PostDto]:
        if token is None:
            post = self._post_repository.get_by_id(post_id)
        else:
            user: UserDto = self._user_repository.get_by_token(token)
            if user is None:
                return CodeTextError(HTTP_401_UNAUTHORIZED, "User not found")
            post = self._post_repository.get_by_id_with_user_id(post_id, user.id)
        if post is None:
            return CodeTextError(HTTP_404_NOT_FOUND, "Post not found")
        else:
            return post

    def get_latest(self, count: int, token: Optional[str]) -> list[PostDto]:
        user = self._user_repository.get_by_token(token)
        if user is None:
            user_id = None
        else:
            user_id = user.id
        return self._post_repository.get_latest(count, user_id)

    def get_latest_by_author(self, count: int, author_id: int, token: Optional[str]) -> list[PostDto]:
        user = self._user_repository.get_by_token(token)
        if user is None:
            user_id = None
        else:
            user_id = user.id
        return self._post_repository.get_latest_by_author_id(count=count, user_id=user_id, author_id=author_id)

    def get_after(self, id: int, count: int, token: Optional[str]) -> list[PostDto]:
        user = self._user_repository.get_by_token(token)
        if user is None:
            user_id = None
        else:
            user_id = user.id
        return self._post_repository.get_after(id=id, count=count, user_id=user_id)

    def get_after_by_author(self, id: int, author_id: int, count: int, token: Optional[str]) -> list[PostDto]:
        user = self._user_repository.get_by_token(token)
        if user is None:
            user_id = None
        else:
            user_id = user.id
        return self._post_repository.get_after_by_author_id(author_id=author_id, id=id, count=count, user_id=user_id)

    def get_before(self, id: int, count: int, token: Optional[str]) -> list[PostDto]:
        user = self._user_repository.get_by_token(token)
        if user is None:
            user_id = None
        else:
            user_id = user.id
        return self._post_repository.get_before(id=id, count=count, user_id=user_id)

    def get_before_by_author(self, id: int, author_id: int, count: int, token: Optional[str]) -> list[PostDto]:
        user = self._user_repository.get_by_token(token)
        if user is None:
            user_id = None
        else:
            user_id = user.id
        return self._post_repository.get_before_by_author_id(author_id=author_id, id=id, count=count, user_id=user_id)

    def get_newer(self, id: int, token: Optional[str]) -> list[PostDto]:
        user = self._user_repository.get_by_token(token)
        if user is None:
            user_id = None
        else:
            user_id = user.id
        return self._post_repository.get_newer(id=id, user_id=user_id)

    def get_newer_by_author(self, post_id: int, author_id: int, token: Optional[str]) -> list[PostDto]:
        user = self._user_repository.get_by_token(token)
        if user is None:
            user_id = None
        else:
            user_id = user.id
        return self._post_repository.get_newer_by_author_id(author_id=author_id, id=post_id, user_id=user_id)

    def delete_by_id(self, post_id, token) -> Optional[CodeTextError]:
        user: UserDto = self._user_repository.get_by_token(token)
        existing_post = self._post_repository.get_by_id(post_id)
        if existing_post is None:
            return None
        if user is None:
            return CodeTextError(text="Authorization required", code=HTTP_401_UNAUTHORIZED)
        if existing_post.authorId != user.id:
            return CodeTextError(text="You must be the owner of this post", code=HTTP_400_BAD_REQUEST)
        self._post_repository.delete_by_id(post_id)
        return None

    def get_all(self, token: Optional[str]) -> list[PostDto]:
        user = self._user_repository.get_by_token(token)
        if user is None:
            return self._post_repository.get_all()
        else:
            return self._post_repository.get_all_with_id(user_id=user.id)

    def get_all_by_author_id(self, author_id, token: Optional[str]) -> list[PostDto]:
        user = self._user_repository.get_by_token(token)
        if user is None:
            user_id = None
        else:
            user_id = user.id
        return self._post_repository.get_all_by_author_id(author_id=author_id, user_id=user_id)

    def save(self, request: PostCreateRequest, token: str) -> Union[PostDto, CodeTextError]:
        user: UserDto = self._user_repository.get_by_token(token)
        if user is None:
            return CodeTextError(text="Invalid token", code=HTTP_401_UNAUTHORIZED)
        existing_post = self._post_repository.get_by_id(request.id)
        if existing_post is None:
            existing_post = PostDto.from_request(request, user)
        else:
            existing_post.content = request.content
            existing_post.coords = request.coords
            existing_post.link = request.link
            existing_post.attachment = request.attachment
            existing_post.mentionIds = request.mentionIds
        if existing_post.authorId != user.id:
            return CodeTextError(text="You must be the owner of this post", code=HTTP_403_FORBIDDEN)
        save_result = self._post_repository.save(existing_post)
        if type(save_result) is TextError:
            return CodeTextError(HTTP_400_BAD_REQUEST, save_result.reason)
        else:
            return save_result

    def like_by_id(self, post_id, token) -> Union[CodeTextError, PostDto]:
        user: UserDto = self._user_repository.get_by_token(token)
        if user is None:
            return CodeTextError(HTTP_401_UNAUTHORIZED, "User not found")
        existing_post = self._post_repository.get_by_id(post_id)
        if existing_post is None:
            return CodeTextError(HTTP_404_NOT_FOUND, "Post not found")
        else:
            return self._post_repository.like(post_id=post_id, user_id=user.id)

    def unlike_by_id(self, post_id, token) -> Union[CodeTextError, PostDto]:
        user: UserDto = self._user_repository.get_by_token(token)
        if user is None:
            return CodeTextError(HTTP_401_UNAUTHORIZED, "User not found")
        existing_post = self._post_repository.get_by_id(post_id)
        if existing_post is None:
            return CodeTextError(HTTP_404_NOT_FOUND, "Post not found")
        else:
            return self._post_repository.unlike(post_id=post_id, user_id=user.id)
