from django.urls import re_path

from . import views

urlpatterns = [
    re_path(r'/latest/?$', views.PostsGetLatestView.as_view()),
    re_path(r'/(?P<post_id>\d+)/after/?$', views.PostsGetAfterView.as_view()),
    re_path(r'/(?P<post_id>\d+)/before/?$', views.PostsGetBeforeView.as_view()),
    re_path(r'/(?P<post_id>\d+)/newer/?$', views.PostsGetNewerView.as_view()),
    re_path(r'/(?P<post_id>\d+)/likes/?$', views.PostsLikeOrDislikeView.as_view()),
    re_path(r'/(?P<post_id>\d+)/?$', views.PostsGetByIdOrRemoveView.as_view()),
    re_path(r'^/?$', views.PostsGetAllOrCreateView.as_view()),
]
