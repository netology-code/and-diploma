from django.urls import re_path

from . import views

urlpatterns = [
    re_path(r'/?$', views.PostsGetAllWallView.as_view()),
    re_path(r'/latest/?$', views.PostsGetLatestByAuthorView.as_view()),
    re_path(r'/(?P<post_id>\d+)/after/?$', views.PostsGetAllWallView.as_view()),
    re_path(r'/(?P<post_id>\d+)/before/?$', views.PostsGetBeforeWallView.as_view()),
    re_path(r'/(?P<post_id>\d+)/newer/?$', views.PostsGetNewerWallView.as_view()),
]
