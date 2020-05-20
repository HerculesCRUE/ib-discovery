# Generación de imagen Docker

Los artefactos bootables están diseñados para poder ser distribuidos como imagen Docker. Se indicarán a continuación las intrucciones.

## Compilación

En primer lugar es preciso [compilar el artefacto](build.md) y copiar el JAR generado en el directorio `docker-build/java` con el nombre `app.jar`

## Generación de la imagen

Para la generación de la imagen se precisa ejecutar el siguiente comando desde el directorio `docker-build` que es donde se encuentra el fichero `Dockerfile`.

```bash
docker build . -t {artifact-name}:{tag}
```

Sustituyendo `{artifact-name}` y `{tag}` por el nombre del artefacto y la versión respectivamente.

En caso que se desee distribuir la imagen a través de un Registry de Docker, se deberá hacer un `pull` mediante la ejecución el comando:

```bash
docker push {artifact-name}:{tag}
```

Es posible que algunos Registries requieran de autenticación previa, debiendo para ello ejecutar previamente un `docker login`.
