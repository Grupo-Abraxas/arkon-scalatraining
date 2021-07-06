# from django.db import models
from django.contrib.gis.db import models
from django.db.models.fields import related

# Create your models here.

"""
DROP TABLE IF EXISTS comercial_activity;

CREATE TABLE comercial_activity (
    id INT PRIMARY KEY
    , name TEXT NOT NULL
);

DROP TABLE IF EXISTS stratum;

CREATE TABLE stratum (
    id INT PRIMARY KEY
    , name TEXT NOT NULL
);

DROP TABLE IF EXISTS shop_type;

CREATE TABLE shop_type (
    id INT PRIMARY KEY
    , name TEXT NOT NULL
);

DROP TABLE IF EXISTS shop;

CREATE TABLE shop (
    id INT PRIMARY KEY
    , name TEXT NOT NULL
    , business_name TEXT
    , activity_id INT REFERENCES comercial_activity (id)
    , stratum_id INT REFERENCES stratum (id)
    , address TEXT NOT NULL
    , phone_number TEXT
    , email TEXT
    , website TEXT
    , shop_type_id INT REFERENCES shop_type (id)
    , position GEOGRAPHY (POINT) NOT NULL
);


"""


class ComercialActivity(models.Model):
    name = models.CharField(max_length=100)


class Stratum(models.Model):
    name = models.CharField(max_length=100)


class ShopType(models.Model):
    name = models.CharField(max_length=100)


class Shop(models.Model):
    name = models.CharField(max_length=100)
    business_name = models.CharField(max_length=100)
    activity_id = models.ForeignKey(
        ComercialActivity,
        on_delete=models.CASCADE,
        related_name='shops'
        )
    stratum_id = models.ForeignKey(
        Stratum,
        on_delete=models.CASCADE,
        related_name='shops'),
    address = models.CharField(max_length=100)
