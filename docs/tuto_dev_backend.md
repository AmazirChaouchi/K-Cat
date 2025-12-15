# Tuto pour le backend

## Développement du backend
Langage : Kotlin

IDE : IntelliJ

Setup les variables d'environnement :

1. Dans le menu du haut : Run > Edit Configurations
2. Environnement variables :
```
DB_URL=jdbc:postgresql://dpg-d502uueuk2gs739ofb0g-a.virginia-postgres.render.com/bd_kcat;PG_USER=bd_kcat_user;PG_PASSWORD=...
```

(Attention l'adresse IP doit être ajoutée dans Render pour que les requêtes soit autorisées)

Tester l'application :
1. Run l'application
2. Tester l'url :
    - Dans un navigateur. Exemple : http://localhost:8080/test/hello
    - Dans postman (plus pratique pour les requêtes avec paramètre).


## Administration de la BD

Langage : postgresql

En ligne de commande, utiliser "psql" :
```
psql -h dpg-d502uueuk2gs739ofb0g-a.virginia-postgres.render.com -U bd_kcat_user bd_kcat
```

Indiquer ensuite le mdp.




