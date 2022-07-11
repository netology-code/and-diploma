from django.urls import path

from . import views

urlpatterns = [
    path('', views.EventsGetAllOrCreateView.as_view()),
    path('latest/', views.EventsGetLatestView.as_view()),
    path('<int:event_id>/', views.EventsGetByIdOrRemoveView.as_view()),
    path('<int:event_id>/after/', views.EventsGetAfterView.as_view()),
    path('<int:event_id>/before/', views.EventsGetBeforeView.as_view()),
    path('<int:event_id>/newer/', views.EventsGetNewerView.as_view()),
    path('<int:event_id>/likes/', views.EventsLikeOrDislikeView.as_view()),
    path('<int:event_id>/participants/', views.EventsParticipateOrUnparticipateView.as_view()),
]