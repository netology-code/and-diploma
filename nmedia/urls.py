"""nmedia URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.11/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  url(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  url(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.conf.urls import url, include
    2. Add a URL to urlpatterns:  url(r'^blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import include, path, re_path
from rest_framework import permissions
from drf_yasg.views import get_schema_view
from drf_yasg import openapi

schema_view = get_schema_view(
   openapi.Info(
      title="Netology Media API",
      default_version='v1',
      description="Сервер для выполнения дипломной работы по направлению Android",
      contact=openapi.Contact(email="https://t.me/Onotole1"),
   ),
   public=True,
   permission_classes=[permissions.AllowAny],
)

urlpatterns = [
    path('api/posts', include('posts.posts_urls')),
    path('api/<int:author_id>/wall', include('posts.wall_urls')),
    path('api/my/wall', include('posts.my_wall_urls')),
    path('api/<int:user_id>/jobs', include('jobs.job_urls')),
    path('api/my/jobs', include('jobs.my_job_urls')),
    path('api/users', include('users.urls')),
    re_path(r'^api/media/?$', include('media.urls')),
    path('api/events', include('events.urls')),
    path('admin/', admin.site.urls),
    re_path(r'^swagger(?P<format>\.json|\.yaml)$', schema_view.without_ui(cache_timeout=0), name='schema-json'),
    re_path(r'^swagger/$', schema_view.with_ui('swagger', cache_timeout=0), name='schema-swagger-ui'),
    re_path(r'^redoc/$', schema_view.with_ui('redoc', cache_timeout=0), name='schema-redoc'),
]
