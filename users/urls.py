from django.urls import re_path

from .views import UsersRegistrationView, UsersAuthenticationView, UsersGetByIdView, UsersGetAllView

urlpatterns = [
    re_path(r'/registration/?$', UsersRegistrationView.as_view()),
    re_path(r'/authentication/?$', UsersAuthenticationView.as_view()),
    re_path(r'/(?P<user_id>\d+)/?$', UsersGetByIdView.as_view()),
    re_path(r'/?$', UsersGetAllView.as_view()),
]
