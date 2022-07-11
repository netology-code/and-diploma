from django.urls import path

from . import views

urlpatterns = [
    path('', views.JobGetAllByUserIdView.as_view()),
]
