# Routes API

## GET Litter Measurements

GET http://localhost:8080/api/litter-measurements?litiereId=12345

Récupère les données d'une litière.
Exemple de résultat :
```
[
    {
        "id": 2,
        "litiereId": "12345",
        "poids": 5.0,
        "timestamp": "2025-12-15T17:06:00"
    },
    {
        "id": 3,
        "litiereId": "12345",
        "poids": 5.0,
        "timestamp": "2025-12-15T20:06:00"
    }
]
```


## POST Litter Measurements

POST https://k-cat.onrender.com/api/litter-measurements

Permet de créer de nouvelles données pour une litière.
Retourne un booleen indiquant si la litière doit être nettoyée ou non.
Exemple de body :
```
{
    "litiereId": "12345",
    "poids": 5.2,
    "timestamp": "2025-12-15T17:06:00"
}
```

Utilisation en python :
```
payload = {
    "litiereId": "12345",
    "poids": 5.2,
    "timestamp": "2025-12-15T17:06:00"
}
response = requests.post("https://k-cat.onrender.com/api/litter-measurements", json=payload)
print(response.status_code) 
must_be_cleaned = response.json()   # True ou False
print("Doit être nettoyée ?", must_be_cleaned)
```

Status code :
201 = created


## GET Litter Cleanup

GET http://localhost:8080/api/litter-cleanup?litiereId=12345

Récupère les informations de nettoyage de la litière.
Exemple de résultat :
```
{
    "litiereId": "12345",
    "lastCleanUpDate": "2026-01-14T10:50:20.898529",
    "shouldBeCleanedUp": false
}
```