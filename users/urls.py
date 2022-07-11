from django.urls import path

from .views import UsersRegistrationView, UsersAuthenticationView, UsersGetByIdView, UsersGetAllView

urlpatterns = [
    path('', UsersGetAllView.as_view()),
    path('registration', UsersRegistrationView.as_view()),
    path('authentication', UsersAuthenticationView.as_view()),
    path('<int:user_id>/', UsersGetByIdView.as_view()),
]
