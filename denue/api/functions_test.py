from django.contrib.gis.geos import Point

from api.models import ComercialActivity, Stratum, ShopType, Shop
from fixtures.utilities import *


def create_comercial_activity(len_min=5, len_max=50):
    return ComercialActivity.objects.create(name=generate_string(len_min, len_max))


def create_stratum(len_min=5, len_max=50):
    return Stratum.objects.create(name=generate_string(len_min, len_max))


def create_shop_type(len_min=5, len_max=50):
    return ShopType.objects.create(name=generate_string(len_min, len_max))


def create_shop(len_min=5, len_max=100):
    name = generate_string(len_min, len_max)
    business_name = generate_string(len_min, len_max)
    address = generate_string(20, 150)
    email = 'user1@test.com'
    website = 'www.test.com'
    position = Point(5, 23)

    activity = create_comercial_activity()
    stratum = create_stratum()
    shop_type = create_shop_type()
    shop = Shop.objects.create(
        name=name,
        business_name=business_name,
        activity=activity,
        stratum=stratum,
        address=address,
        email=email,
        website=website,
        shop_type=shop_type,
        position=position,
        )

    return shop
