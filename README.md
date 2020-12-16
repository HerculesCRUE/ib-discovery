![](./images/logos_feder.png)



| Entregable     | Procesador de datos                                          |
| -------------- | ------------------------------------------------------------ |
| Fecha          | 17/12/2020                                                   |
| Proyecto       | [ASIO](https://www.um.es/web/hercules/proyectos/asio) (Arquitectura Semántica e Infraestructura Ontológica) en el marco de la iniciativa [Hércules](https://www.um.es/web/hercules/) para la Semántica de Datos de Investigación de Universidades que forma parte de [CRUE-TIC](http://www.crue.org/SitePages/ProyectoHercules.aspx) |
| Módulo         | Discovery                                                    |
| Tipo           | Software                                                     |
| Objetivo       | Librería de descubrimiento para el proyecto Backend SGI (ASIO). |
| Estado         | Todas los objetivos (excepto el de búsqueda de instancias relacionadas en la nube LOD), enumeradas en el punto anterior, están implementadas al **100%**. Dado que para implementar el objetivo restante, se reutilizara la mayor parte de la algoritmia implementada, las partes necesarias a desarrollar se concentraran básicamente en los conectores y mapeos descritos en el documento por lo que se estima que el nivel de completitud de la librería seria aproximadamente de un **75%** |
| Próximos pasos | La integración con componentes desarrollados en una fase de madurez no final, o otros por desarrollar (tales como la gestión de eventos de acciones propagados por la librería o la parte del frontal web que permita aceptar o desechar sugerencias de similitud por parte de usuarios finales), quizás requieran la modificación o creación de algún EndPoint adicional, aunque según lo descrito en el apartado anterior, la implementación de estos desde el punto de vista de la librería debería de ser trivial. |
| Documentación  | [Librería de descubrimiento](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/24-Librer%C3%ADa_de_descubrimiento/ASIO_Libreria_de_descubrimiento.md)<br/>[Manual de usuario](./docs/manual_de_usuario.md) (documentación de alto nivel)<br />[Documentación técnica](./docs/documentacion-tecnica.md) (documentación de bajo nivel)<br/>[Documentación API REST de la librería de descubrimiento](./docs/documentacion_api_rest_de_la_libreria_de_descubrimiento.md) (documentación de bajo nivel)<br/>[docker](./docs/docker.md) |

# ASIO - Discovery

|     | Master |
| --- | ------ |
| Quality Gate | [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=HerculesCRUE_ib-discovery&metric=alert_status)](https://sonarcloud.io/dashboard?id=HerculesCRUE_ib-discovery) |
| Coverage | [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=HerculesCRUE_ib-discovery&metric=coverage)](https://sonarcloud.io/dashboard?id=HerculesCRUE_ib-discovery) |

Librería de descubrimiento para el proyecto Backend SGI (ASIO).

## OnBoarding

Para iniciar el entorno de desarrollo se necesita cumplir los siguientes requisitos:

* OpenJDK 11 (en caso de querer JDK8: Oracle JDK 8)
* Eclipse JEE 2019-09 con plugins:
  * Spring Tools 4
  * m2e-apt
  * Lombok
* Docker

## Módulos disponibles

* **Módulo back**: módulo que añade una capa de servicios REST a la funcionalidad de la aplicación. Genera un artefacto JAR bootable
* **Módulo service**: módulo que contiene la lógica de la aplicación. Puede ser utilizado como librería independiente para ser integrado en otras aplicaciones
* **Módulo swagger**: módulo que contiene la funcionalidad necesaria para añadir Swagger para la interacción con el API Rest

## Metodología de desarrollo

La metodología de desarrollo es Git Flow.

## Entorno de desarrollo Docker

La inicialización de los elementos adicionales al entorno de desarrollo se realiza con docker. 

En el directorio docker-devenv se ha configurado un fichero docker-compose.yml para poder arrancar el entorno de desarrollo.

Para arrancar el entorno:

```bash
docker-compose up -d
```

Para pararlo:

```bash
docker-compose down
```

## Swagger

Se ha añadido la posibilidad de utilizar Swagger. Para acceder a Swagger, se utilizará la siguiente URL:

* http://localhost:8080/swagger-ui.html

Para activar swagger se utilizará la variable `app.swagger.enabled`

## Instalación en entorno real

La aplicación se puede configurar por medio del fichero de configuración **application.yml** o mediante variables de entorno.

El fichero de configuración será la configuración usada para cualquier variable de entorno que no este configurada

Será preciso configurar las siguientes variables de entorno cuando se instale en un entorno real (el resto pueden ser cambiadas si se desea en el fichero de configuración y en caso contrarío se usara el valor por defecto):

* Relativas a la Base de datos Relacional

|Variable|Descripción|Valor por defecto|
|---|---|---|
|`APP_PERSISTENCE_DATASOURCE_DRIVER-CLASS-NAME`| Driver usado para la conexión a BBDD Relacional       |org.mariadb.jdbc.Driver|
|`APP_PERSISTENCE_DATASOURCE_USERNAME`|Usuario para la conexión a BBDD Relacional| app                                                          |
| `APP_PERSISTENCE_DATASOURCE_PASSWORD`          | Password para la conexión a BBDD Relacional           | sqlpass                                                      |
| `APP_PERSISTENCE_DATASOURCE_URL`               | Cadena de conexión para la conexión a BBDD Relacional | jdbc:mariadb://127.0.0.1:3307/discovery?ssl=false&createDatabaseIfNotExist=true |

* Relativas a la integración con otras herramientas usadas por la librería de descubrimiento

| Variable                                | Descripción                                                  | Valor por defecto  |
| --------------------------------------- | ------------------------------------------------------------ | ------------------ |
| `DATA_READCACHEFROMFIREBASE`            | Determina si en caso de estar la cache de REDIS vacía (tras el primer despliegue en un entorno), se quieren usar los datos cacheados estáticamente almacenados en Firebase, para permitir a la aplicación estar desplegada en un tiempo sustancialmente menor | false              |
| `DATA_ELASTICSEARCH_HOST`               | Host de conexión a Elasticsearch                             | localhost          |
| `DATA_ELASTICSEARCH_PORT`               | Puerto de conexión a Elasticsearch                           | 9200               |
| `DATA_REDIS_HOST`                       | Host de conexión a REDIS                                     | localhost          |
| `DATA_REDIS_PORT`                       | Puerto de conexión a REDIS                                   | 16379              |
| `DATA_REDIS_PASSWORD`                   | Pasword de conexión a REDIS                                  | redispass          |
| `DATA_KAFKA_HOST`                       | Host de conexión a broker Kafka                              | localhost          |
| `DATA_KAFKA_PORT`                       | Puerto de conexión a broker Kafka                            | 9092               |
| `DATA_KAFKA_TOPICENTITYCHANGE_TOPIC`    | Topic para que la librería de descubrimiento sea notificada en cambios de entidades almacenadas en el Triple Store | entity_change      |
| `DATA_KAFKA_TOPICENTITYCHANGE_GROUPID` | Grupo para que los clientes de la librería de descubrimiento se subscriban a las notificaciones de cambios de entidades almacenadas en el Triple Store | entity_change_1    |
| `DATA_KAFKA_TOPICDISCOVERYACTION_TOPIC` | Topic para donde la librería de descubrimiento sea publicara las acciones necesarias en las entidades relativas la eliminación de duplicados, tras encontrar similitudes | discovery_action   |
| `DATA_KAFKA_TOPICDISCOVERYACTION_GROUPID` | Grupo para que los clientes de la librería de descubrimiento se subscriban a las notificaciones de acciones | discovery_action_1 |

* Relativas a la configuración de la aplicación

| Variable                                                     | Descripción                                                  | Valor por defecto                                            |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `DATA_SOURCES_USECACHEDDATA`                                 | Determina si esta permitido el uso de datos cacheados, esto aumenta sustancialmente el rendimiento de la aplicación | true                                                         |
| `DATA_SOURCES_THRESHOLDS_elasticSearchAttributesThresholdSimple` | Threshold de importancia de atributos que se requiere superar para composición de queries en Elasticsearch para objetos sin varios niveles de anidación (objetos simples) | 0.8                                                          |
| `DATA_SOURCES_THRESHOLDS_elasticSearchAttributesNumberRatioSimple` | Threshold de numero mínimo de atributos que se requiere superar para composición de queries en Elasticsearch para objetos sin varios niveles de anidación (objetos simples) | 0.4                                                          |
| `DATA_SOURCES_THRESHOLDS_elasticSearchAttributesThresholdComplex` | Threshold de importancia de atributos que se requiere superar para composición de queries en Elasticsearch para objetos con varios niveles de anidación (objetos complejos) | 0.5                                                          |
| `DATA_SOURCES_THRESHOLDS_elasticSearchAttributesNumberRatioComplex` | Threshold de numero mínimo de atributos que se requiere superar para composición de queries en Elasticsearch para objetos con varios niveles de anidación (objetos complejos) | 0.2                                                          |
| `DATA_SOURCES_THRESHOLDS_elasticSearchMaxDesirableNumbersOfResults` | Número máximo de resultados que se desean obtener como respuesta a las Queries de elasticsearch. Si esta configurado, cortara las respuesta como máximo a el numero indicado ordenando los resultados por similitud | 50                                                           |
| `DATA_SOURCES_THRESHOLDS_elasticSearchCutOffAccordPercentile` | Los resultados retornados por Elasticsearch, serán truncados una vez que se alcance el percentil indicado para el atributo de similitud | 0.5                                                          |
| `DATA_SOURCES_THRESHOLDS_manualThreshold`                    | Threshold mínimo, para que la similitud encontrada, pueda ser considerada suficiente para que se requiera la intervención de un usuario para determinar si esta es correcta o no, y desencadenar acciones | 0.7                                                          |
| `DATA_SOURCES_THRESHOLDS_automaticThreshold`                 | Threshold mínimo, para que la similitud encontrada, pueda ser considerada suficiente para que se ejecuten las acciones de eliminación del duplicado sin intervención humana | 0.9                                                          |
| `DATA_SOURCES_THRESHOLDS_nodes`                              | Lista de nodos para los que se obtendrán datos de los triple stores. En esta lista debe de aparecer el nodo propio done se despliega la librería, y pueden aparecer también nodos externos con los que se buscaran enlaces. Los objetos tienen que tener los siguientes atributos **type** (trellis, wikibase u otros representa el tipo de Triple Store donde se encuentra la informacion), **baseURL** es la URL base sobre la cual se realizaran las peticiones, **user** usuario que realiza la petición , y **password** especialmente importante para la autorización. | - type: trellis  baseURL: http://herc-iz-front-desa.atica.um.es/ user: admin password: admin |



### Ejecución

Al generarse un JAR bootable la ejecución se realizará mediante el siguiente comando:

```bash
java -jar {jar-name}.jar
```

Sustituyendo `{jar-name}` por el nombre del fichero JAR generado.

No es necesario especificar la clase de inicio de la aplicación, ya que el fichero MANIFEST.MF generado ya contiene la información necesaria. Solamente se especificarán los parametros necesarios.

## Testing y cobertura

Se incluyen los resultados del testing y cobertura en los siguientes enlaces:

* [Testing](http://herc-iz-front-desa.atica.um.es:8070/discovery/surefire/surefire-report.html)
* [Cobertura](https://sonarcloud.io/component_measures?id=HerculesCRUE_ib-discovery&metric=coverage&view=list)

##  Documentación adicional

* [Librería de descubrimiento](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/24-Librer%C3%ADa_de_descubrimiento/ASIO_Libreria_de_descubrimiento.md)
* [Manual de usuario](./docs/manual_de_usuario.md) (documentación de alto nivel)
* [Documentación técnica](./docs/documentacion-tecnica.md) (documentación de bajo nivel)
* [Documentación API REST de la librería de descubrimiento](./docs/documentacion_api_rest_de_la_libreria_de_descubrimiento.md) (documentación de bajo nivel)
* [docker](./docs/docker.md)
