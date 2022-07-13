from django.urls import re_path

from . import views

urlpatterns = [
    re_path(r'/latest/?$', views.EventsGetLatestView.as_view()),
    re_path(r'/(?P<event_id>\d+)/after/?$', views.EventsGetAfterView.as_view()),
    re_path(r'/(?P<event_id>\d+)/before/?$', views.EventsGetBeforeView.as_view()),
    re_path(r'/(?P<event_id>\d+)/newer/?$', views.EventsGetNewerView.as_view()),
    re_path(r'/(?P<event_id>\d+)/likes/?$', views.EventsLikeOrDislikeView.as_view()),
    re_path(r'/(?P<event_id>\d+)/participants/?$', views.EventsParticipateOrUnparticipateView.as_view()),
    re_path(r'/(?P<event_id>\d+)/?$', views.EventsGetByIdOrRemoveView.as_view()),
    re_path(r'/?$', views.EventsGetAllOrCreateView.as_view()),
]
