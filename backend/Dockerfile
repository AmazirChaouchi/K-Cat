# ÉTAPE 1: Construction (utilise une image avec Gradle et le JDK pour compiler)
FROM gradle:8.5.0-jdk21 AS builder
WORKDIR /app

# 1. Copier les fichiers de configuration et les scripts essentiels (pour le cache)
# Cela inclut le script d'exécution gradlew et les fichiers de build.
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle gradle

# 2. Accorder les permissions d'exécution
RUN chmod +x gradlew

# 3. Copier TOUTES les sources et ressources nécessaires à la compilation (y compris le dossier 'src')
# Cette étape remplace les COPY et RUN dépendances précédents pour une approche plus simple
COPY src src

# 4. Construire l'exécutable (clean garantit une reconstruction complète)
RUN ./gradlew clean bootJar

# ÉTAPE 2: Production (utilise une image très légère avec seulement le JRE pour l'exécution)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copie le JAR construit de l'étape "builder"
# Le fichier JAR est trouvé dans le chemin standard de Spring Boot
COPY --from=builder /app/build/libs/*.jar app.jar

# Rend l'application compatible avec les variables d'environnement du Cloud (Render utilise la variable PORT)
ENV SERVER_PORT=8080

# Exposer le port par défaut (même s'il est ignoré par Render, c'est une bonne pratique Docker)
EXPOSE 8080

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]