from django.urls import re_path

from . import views

urlpatterns = [
    re_path(r'^/(?P<job_id>\d+)/?$', views.JobDeleteByIdView.as_view()),
    re_path(r'^/?$', views.JobGetAllOrCreateView.as_view()),
]
