from django.urls import path

from . import views

urlpatterns = [
    path('', views.JobGetAllOrCreateView.as_view()),
    path('<int:job_id>', views.JobDeleteByIdView.as_view()),
]
