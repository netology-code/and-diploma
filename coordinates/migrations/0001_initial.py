# Generated by Django 4.0.4 on 2022-06-20 07:00

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='CoordinatesModel',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('lat', models.DecimalField(decimal_places=6, max_digits=8)),
                ('long', models.DecimalField(decimal_places=6, max_digits=8)),
            ],
        ),
    ]
