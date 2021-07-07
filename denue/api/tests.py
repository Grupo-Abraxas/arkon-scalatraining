from django.contrib.gis.geos import Point
from django.test import TestCase

from api.models import ComercialActivity, Stratum, ShopType, Shop
from fixtures.utilities import * 


# Create your tests here.
class ComercialActivityTestCase(TestCase):
    def setUp(self):
        self.name = generate_string(5, 50)
        ComercialActivity.objects.create(name=self.name)

    def test_commercial_activity_get_by_name(self):

        comercial_activity = ComercialActivity.objects.get(name=self.name)
        self.assertEqual(comercial_activity.name, self.name)


class StratumTestCase(TestCase):
    def setUp(self):
        self.name = generate_string(5, 50)
        Stratum.objects.create(name=self.name)

    def test_commercial_activity_get_by_name(self):

        stratum = Stratum.objects.get(name=self.name)
        self.assertEqual(stratum.name, self.name)


class ShopTypeTestCase(TestCase):
    def setUp(self):
        self.name = generate_string(5, 50)
        ShopType.objects.create(name=self.name)

    def test_commercial_activity_get_by_name(self):

        shop_type = ShopType.objects.get(name=self.name)
        self.assertEqual(shop_type.name, self.name)


class ShopTestCase(TestCase):
    def setUp(self):
        self.name = generate_string(5, 100)
        self.business_name = generate_string(5, 100)
        self.activity_name = generate_string(5, 100)
        self.stratum_name = generate_string(5, 100)
        self.address = generate_string(50, 150)
        self.email = 'user1@test.com'
        self.website = 'www.test.com'
        self.shop_type_name = generate_string(5, 50)
        self.position = Point(5, 23)

        activity = ComercialActivity.objects.create(name=self.activity_name)
        stratum = Stratum.objects.create(name=self.stratum_name)
        shop_type = ShopType.objects.create(name=self.shop_type_name)
        shop = Shop.objects.create(
            name=self.name,
            business_name=self.business_name,
            activity=activity,
            stratum=stratum,
            address=self.address,
            email=self.email,
            website=self.website,
            shop_type=shop_type,
            position=self.position,
            )

    def test_shop_get_by_name(self):

        shop = Shop.objects.get(name=self.name)
        self.assertEqual(shop.name, self.name)
