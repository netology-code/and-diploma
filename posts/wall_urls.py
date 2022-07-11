from django.urls import path

from . import views

urlpatterns = [
    path('', views.PostsGetAllWallView.as_view()),
    path('latest/', views.PostsGetLatestByAuthorView.as_view()),
    path('<int:post_id>/after/', views.PostsGetAllWallView.as_view()),
    path('<int:post_id>/before/', views.PostsGetBeforeWallView.as_view()),
    path('<int:post_id>/newer/', views.PostsGetNewerWallView.as_view()),
]
