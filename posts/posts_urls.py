from django.urls import path

from . import views

urlpatterns = [
    path('', views.PostsGetAllOrCreateView.as_view()),
    path('latest/', views.PostsGetLatestView.as_view()),
    path('<int:post_id>/', views.PostsGetByIdOrRemoveView.as_view()),
    path('<int:post_id>/after/', views.PostsGetAfterView.as_view()),
    path('<int:post_id>/before/', views.PostsGetBeforeView.as_view()),
    path('<int:post_id>/newer/', views.PostsGetNewerView.as_view()),
    path('<int:post_id>/likes/', views.PostsLikeOrDislikeView.as_view()),
]
