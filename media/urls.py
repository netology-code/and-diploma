from django.urls import path

from . import views

urlpatterns = [
    path('', views.MediaCreateView.as_view()),
]