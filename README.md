<a href="https://www.arkondata.com/">
    <img src="./img/logo.jpg" align="right" height="80">
</a>

# Arkon's Python Training

## Description
Python hands on training project. Looking to introduce new team members or anyone interested to the python 
programming language and the way it's used within the ArkonData team. 

You'll implement a web server exposing a GraphQL API to expose business retrieved from the INEGI's API and 
query them based on their location.

# https://docs.djangoproject.com/en/3.2/ref/contrib/gis/install/geolibs/#gdalbuild
# https://docs.djangoproject.com/en/3.2/ref/contrib/gis/model-api/

* [Concepts](https://github.com/Grupo-Abraxas/arkon-scalatraining#concepts)
* [Tools](https://github.com/Grupo-Abraxas/arkon-scalatraining#tools)
* [Libraries](https://github.com/Grupo-Abraxas/arkon-scalatraining#libraries)
* [Exercises](https://github.com/Grupo-Abraxas/arkon-scalatraining#exercises)
* [Basic commands](https://github.com/Grupo-Abraxas/arkon-scalatraining#basic-commands)
* [Requirements](https://github.com/Grupo-Abraxas/arkon-scalatraining#requirements)

## Concepts
- FP
    - [Functional Programming HOWTO](https://docs.python.org/es/3/howto/functional.html#functional-programming-howto)

## Books
- Coming soon

## Videos
- Coming soon

## Tools
- [Python]https://www.python.org/)
- [Code](https://code.visualstudio.com/)

## Libraries
- [Django](https://docs.djangoproject.com): Library for FP.
- [Geospatial libraries](https://docs.djangoproject.com/en/3.2/ref/contrib/gis/install/geolibs/)
- [GeoDjango Model API](https://docs.djangoproject.com/en/3.2/ref/contrib/gis/model-api/)
- [Testing in Django](https://docs.djangoproject.com/en/3.2/topics/testing/): Python testing library.

## Exercises


## Basic commands
Django Shell
```
$ python manage.py shell
```

Running test
```
// running all tests
$ python manage.py test

// running api test
$ python manage.py test api.test

// Creates new migration(s) for api app
$ python manage.py makemigrations api

// Updates database schema
$ python manage.py migrate api

// Create virtual environment
$ python -m venv env

// Activate Virtual Environment
$ source mypython/bin/activate

// Deactivate the virtual environment
$ deactivate

// Install requiretements 
$ pip install -r requirements.txt



```

## Requirements 
Implement a GraphQL API based on the given [schema](./schema.graphql) to expose the saved business and 
query them based on their location. The database to be used should be [PostgreSQL](www.postgresql.org) with the 
[PostGIS](http://postgis.net/) extension to power the georeferenced queries. To fill the database you'll have 
to implement a web scrapper to retrieve data from the INEGI's DENUE [API](https://www.inegi.org.mx/servicios/api_denue.html) 
and execute the `createShop` mutation defined on the implemented API.

The implemented API should comply the following rules: 
- On the `createShop` mutation 
    - The `activity`, `stratum` and `shopType` fields should search for existing records on the `Activity`, 
      `Stratum` and `ShopType` tables and insert only if there is no previous record.
