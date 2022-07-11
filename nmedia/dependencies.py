from imagekitio import ImageKit

from events.data.local_event_repository import LocalEventRepository
from events.domain.event_service import EventService
from jobs.data.local_job_repository import LocalJobRepository
from jobs.domain.job_service import JobService
from media.services.media_service import MediaService
from nmedia.secrets import Secrets
from posts.data.local_post_repository import LocalPostRepository
from posts.domain.post_service import PostService
from users.data.repositories import LocalUserRepository, LocalTokenRepository
from users.domain.repositories import UserRepository
from users.domain.services import UserService


class DependencyContainer:
    __instance = None

    _user_repository: UserRepository = LocalUserRepository()
    _image_kit = ImageKit(
        private_key=Secrets.IMAGE_KIT_PRIVATE_KEY,
        public_key=Secrets.IMAGE_KIT_PUBLIC_KEY,
        url_endpoint=Secrets.IMAGE_KIT_URL_ENDPOINT
    )
    user_service = UserService(user_repository=_user_repository, token_repository=LocalTokenRepository())
    media_service = MediaService(_image_kit)
    post_service = PostService(LocalPostRepository(), _user_repository)
    event_service = EventService(LocalEventRepository(), _user_repository)
    job_service = JobService(LocalJobRepository(), _user_repository)

    @classmethod
    def get_instance(cls):
        if not cls.__instance:
            cls.__instance = DependencyContainer()
        return cls.__instance
