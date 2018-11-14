from github import Github
from github import GithubException
from github import UnknownObjectException
from github import RateLimitExceededException
import requests
import re
import logging
g = Github(base_url="https://api.github.com")
flag = "y"
logging.basicConfig(level=logging.ERROR,
                    filename='Exception.log',
                    filemode='a',
                    format=
                    '%(asctime)s - %(pathname)s[line:%(lineno)d] - %(levelname)s: %(message)s'
                    )
while flag is "y":
    username = input("Please enter a username on GitHub:")
    while not re.match("[a-zA-Z0-9-]+",username):
        username = input("Username is illegal. Please enter again:")
    try:
        n = g.get_user(username).get_repos().totalCount
        print("There are %d public repositories."%n)
        print("-----------------------------------")
        for repo in g.get_user(username).get_repos():
            print(repo.name)
            #repo.edit(has_wiki=False)
            # to see all the available attributes and methods
            #print(dir(repo))
    except UnknownObjectException as e:
        print("Username does not exist! Please check your username.")
        logging.error('UnknownObjectException')
    except requests.exceptions.ConnectionError as e:
        print("Please check your network connection.")
        logging.error('Connectionerror')
    except RateLimitExceededException as e:
        print("API rate limit exceeded.")
        logging.error('RateLimitExceededException')
    finally:
        flag = input("\nDo you want to query another username? y: Yes or any key: No")