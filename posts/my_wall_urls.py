from django.urls import re_path

from . import views

urlpatterns = [
    re_path(r'/latest/?$', views.PostsGetLatestMyView.as_view()),
    re_path(r'/(?P<post_id>\d+)/after/?$', views.PostsGetAfterMyWallView.as_view()),
    re_path(r'/(?P<post_id>\d+)/before/?$', views.PostsGetBeforeMyWallView.as_view()),
    re_path(r'/(?P<post_id>\d+)/newer/?$', views.PostsGetNewerMyWallView.as_view()),
    re_path(r'/?$', views.PostsGetAllMyWallView.as_view()),
]
