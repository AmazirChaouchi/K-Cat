import requests
import time

API_URL = "https://k-cat.onrender.com/api/litter-cleanup"
LITIERE_ID = "23456"

def must_be_cleaned():
    r = requests.get(API_URL, params={"litiereId": LITIERE_ID}, timeout=5)

    if r.status_code != 200:
        print("Erreur API :", r.status_code)
        return False

    data = r.json()
    return data.get("shouldBeCleanedUp", False)


while True:
    if not must_be_cleaned():
        print("Eteinte de la LED")
        break
    time.sleep(10)

