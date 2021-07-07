import random
from string import ascii_letters
from django.contrib.auth import get_user_model


def generate_string(min_length=5, max_length=50,
                    string_generator=ascii_letters):

    return ''.join(random.choice(string_generator) for i in range(
        random.randint(min_length, max_length))
        )


def generate_int(min_length=0, max_length=50):
    return random.randint(min_length, max_length)


def authenticate_test(client, username='test'):
    user = get_user_model().objects.create(username=username)
    client.authenticate(user)
    return user
