![](./img/logos_feder.png)

| Fecha         | 17/12/2020                                                   |
| ------------- | ------------------------------------------------------------ |
| Proyecto      | [ASIO](https://www.um.es/web/hercules/proyectos/asio) (Arquitectura Semántica e Infraestructura Ontológica) en el marco de la iniciativa [Hércules](https://www.um.es/web/hercules/) para la Semántica de Datos de Investigación de Universidades que forma parte de [CRUE-TIC](https://www.crue.org/proyecto/hercules/) |
| Módulo        | Arquitectura Semántica                                       |
| Tipo          | Documentación                                                |
| Objetivo      | El presente documento describe el proceso de generación y ejecución de una imagen docker a partir de el código del proyecto |
| Documentación | [Librería de descubrimiento](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/24-Librer%C3%ADa_de_descubrimiento/ASIO_Libreria_de_descubrimiento.md)<br/>[Manual de usuario](./manual_de_usuario.md) (documentación de alto nivel)<br />[Documentación técnica](./documentacion-tecnica.md) (documentación de bajo nivel)<br/>[Documentación API REST de la librería de descubrimiento](./documentacion_api_rest_de_la_libreria_de_descubrimiento.md) (documentación de bajo nivel)<br/>[README](../README.md)<br/>[docker](./docker.md)<br/>[Federación](https://github.com/HerculesCRUE/ib-discovery<br/>https://github.com/HerculesCRUE/ib-federation<br/>https://github.com/HerculesCRUE/ib-service-discovery)<br/>[Service Discovery](https://github.com/HerculesCRUE/ib-service-discovery) |

# Generación de imagen Docker

Los artefactos bootables están diseñados para poder ser distribuidos como imagen Docker. Se indicarán a continuación las instrucciones.

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
docker pull {artifact-name}:{tag}
```

Es posible que algunos Registros requieran de autenticación previa, debiendo para ello ejecutar previamente un `docker login`.
