from django.urls import path

from . import views

urlpatterns = [
    path('', views.PostsGetAllMyWallView.as_view()),
    path('latest/', views.PostsGetLatestMyView.as_view()),
    path('<int:post_id>/after/', views.PostsGetAfterMyWallView.as_view()),
    path('<int:post_id>/before/', views.PostsGetBeforeMyWallView.as_view()),
    path('<int:post_id>/newer/', views.PostsGetNewerMyWallView.as_view()),
]
