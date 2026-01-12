# Routes API

## GET Litter Measurements

GET http://localhost:8080/api/litter-measurements?litiereId=12345

Récupère les données d'une litière.
Exemple de résultat :



## POST Litter Measurements

POST https://k-cat.onrender.com/api/litter-measurements

Permet de créer de nouvelles données pour une litière.
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
```

Status code :
201 = created
