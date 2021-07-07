
from django.test import TestCase

from api.models import ComercialActivity, Stratum, ShopType, Shop
from fixtures.utilities import * 
from .functions_test import *


# Create your tests here.
class ComercialActivityTestCase(TestCase):

    def setUp(self):

        self.comercial_activity = create_comercial_activity()

    def test_commercial_activity_get_by_name(self):

        comercial_activity = ComercialActivity.objects.get(
            name=self.comercial_activity.name
            )
        self.assertEqual(comercial_activity.name, self.comercial_activity.name)


class StratumTestCase(TestCase):

    def setUp(self):

        self.stratum = create_stratum()

    def test_commercial_activity_get_by_name(self):

        stratum = Stratum.objects.get(name=self.stratum.name)
        self.assertEqual(stratum.name, self.stratum.name)


class ShopTypeTestCase(TestCase):

    def setUp(self):

        self.shop_type = create_shop_type()

    def test_commercial_activity_get_by_name(self):

        shop_type = ShopType.objects.get(name=self.shop_type.name)
        self.assertEqual(shop_type.name, self.shop_type.name)


class ShopTestCase(TestCase):

    def setUp(self):
        self.shop = create_shop()

    def test_shop_get_by_name(self):
        shop = Shop.objects.get(name=self.shop.name)
        self.assertEqual(shop.name, self.shop.name)

    def test_shop_get_by_comercial_activity(self):
        shop = Shop.objects.get(activity__name=self.shop.activity.name)
        self.assertEqual(shop.name, self.shop.name)

    def test_shop_get_by_stratum(self):
        shop = Shop.objects.get(stratum__name=self.shop.stratum.name)
        self.assertEqual(shop.name, self.shop.name)

    def test_shop_get_by_shop_type(self):
        shop = Shop.objects.get(shop_type__name=self.shop.shop_type.name)
        self.assertEqual(shop.name, self.shop.name)
