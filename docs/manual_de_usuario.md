![](./images/logos_feder.png)

| Entregable     | Librería factoria de URIs                                    |
| -------------- | ------------------------------------------------------------ |
| Fecha          | 25/05/2020                                                   |
| Proyecto       | [ASIO](https://www.um.es/web/hercules/proyectos/asio) (Arquitectura Semántica e Infraestructura Ontológica) en el marco de la iniciativa [Hércules](https://www.um.es/web/hercules/) para la Semántica de Datos de Investigación de Universidades que forma parte de [CRUE-TIC](http://www.crue.org/SitePages/ProyectoHercules.aspx) |
| Módulo         | Arquitectura Semántica                                       |
| Tipo           | Documentación                                                |
| Objetivo       | El presente documento describe la librería Factoría de URIs a alto nivel, por lo tanto, se complementa con el documento de [Documentación tecnica](./documentacion-tecnica.md) que la describe a bajo nivel.<br/>Únicamente pretende mejorar la comprensión del usuario, de las funcionalidades implementadas y el cumplimiento de lo expresado en los documentos de  [Esquema de URIs](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/entregables_hito_1/08-Esquema_de_URIs_Hércules/ASIO_Izertis_ArquitecturaDeURIs.md) y [Buenas practicas para URIs Hércules](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/entregables_hito_1/09-Buenas_prácticas_para_URIs_Hércules/ASIO_Izertis_BuenasPracticasParaURIsHercules.md) |
| Estado         | Implementada al **100%**, según la funcionalidad prevista para cubrir lo expresado en los documentos de [esquema de URIs](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/entregables_hito_1/08-Esquema_de_URIs_Hércules/ASIO_Izertis_ArquitecturaDeURIs.md)  , y  [Buenas practicas para URIs Hércules](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/entregables_hito_1/09-Buenas_prácticas_para_URIs_Hércules/ASIO_Izertis_BuenasPracticasParaURIsHercules.md).  Por otro lado la exposición de los EndPoint relativos al [CRUD](https://es.wikipedia.org/wiki/CRUD) sobre modelo de datos completo, hace posible realizar cualquier operación, aunque esta en principio no estuviese prevista. |
| Próximos pasos | La integración con componentes desarrollados en una fase de madurez no final, o otros por desarrollar (tales como el servicio de publicación web), quizás requieran la modificación o creación de algún EndPoint adicional, aunque según lo descrito en el apartado anterior, dado que existe un CRUD completo sobre todas las entidades, la implementación, debería de ser trivial. |
| Documentación  | [Esquema de URIs](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/entregables_hito_1/08-Esquema_de_URIs_Hércules/ASIO_Izertis_ArquitecturaDeURIs.md)<br/>[Buenas practicas para URIs Hércules](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/entregables_hito_1/09-Buenas_prácticas_para_URIs_Hércules/ASIO_Izertis_BuenasPracticasParaURIsHercules.md)<br/>[Documentación técnica](./documentacion-tecnica.md) (documentación de bajo nivel)<br/>[Documentación API REST de la Factoria de URIs](./documentacion_api_rest_de_la_factoria_de_uris.md) (documentación de bajo nivel)<br/>[build](./build.md)<br/>[docker](./docker.md) |



# Manual de usuario

El presente documento realiza una descripción de la Factoría de URIs a alto nivel, por lo tanto, se complementa con el documento de [Documentación técnica](./documentacion-tecnica.md) que la describe a bajo nivel.

Únicamente pretende mejorar la comprensión del usuario, de las funcionalidades implementadas y el cumplimiento de lo expresado en los documentos de  [Esquema de URIs](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/entregables_hito_1/08-Esquema_de_URIs_Hércules/ASIO_Izertis_ArquitecturaDeURIs.md) y [Buenas practicas para URIs Hércules](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/entregables_hito_1/09-Buenas_prácticas_para_URIs_Hércules/ASIO_Izertis_BuenasPracticasParaURIsHercules.md).

## DESPLIEGUE

Se puede encontrar documentación exhaustiva de desplieguen en el documento [build](./build.md) tanto para usuarios finales como para desarrolladores.

## URIS

### Esquema de URIS

El esquema de URIs implementado, esta descrito en el documento de [Esquema de URIs](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/entregables_hito_1/08-Esquema_de_URIs_Hércules/ASIO_Izertis_ArquitecturaDeURIs.md) 

Básicamente tiene como finalidad principal mapear URIS publicas  o canónicas con URIs privadas o internas.

 ![mapper_url](./images/multi_languege_map_language.png)

Para ello es conveniente una definición algo mas formal

#### URIS publicas o externas (URIS Canónicas)

Podemos definir una **URI canónica**, como aquella que siguiendo los criterios expresados en el documento [esquema de URIs](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/entregables_hito_1/08-Esquema_de_URIs_Hércules/ASIO_Izertis_ArquitecturaDeURIs.md), ofrece un identificador persistente para un recurso semántico, siguiendo para ello el siguiente patrón de construcción 

**http://{dominio}/[{subdominio}]/{tipo}/{concepto}[/{referencia}]**

Cada elemento del esquema de URIs a partir del elemento idioma (tipo, concepto y referencia) debe mostrarse en el idioma indicado en la URI, si procede:

- **dominio:** Representa el nivel mayor del espacio de nombres para la resolución del URI, y para aportar información relevante sobre el propietario de la información. (ejemplo http://**hercules.org**)

- **subdominio (si procede):** Aporta información sobre la entidad o departamento dentro de la entidad a la cual pertenece el recurso de información. Representa el nivel menor del espacio nombres para la resolución del URI, y para aportar información relevante sobre el propietario de la información. (ejemplo http://**hercules.org/um**)

- **tipo:** Establece el tipo de información que contiene el recurso. Podrá ser uno de los enumerados:

  - catálogo:

     

    Un catálogo de datos es una colección de metadatos sobre conjuntos de datos o datasets. Habitualmente estos documentos y recursos de información contendrían datos comunes como condiciones de uso, origen, vocabularios utilizados, etc. Se usaran a priori los vocabularios:

    - [DCAT](https://www.w3.org/TR/vocab-dcat/) (vocabularios para describir catálogos, datasets, distribuciones)
    - [VoID](https://www.w3.org/TR/void/) (vocabulario para describir conjuntos de datos RDF)
    - [PROV](https://www.w3.org/TR/prov-o/) (vocabulario para describir licencias y derechos de uso)

  - **def:** Para vocabulario u ontología utilizada como modelo semántico. Habitualmente esquemas RDF-S u ontologías representadas mediante OWL.

  - **kos:** Sistema de organización del conocimiento sobre un dominio concreto. Habitualmente taxonomías, diccionarios o tesauros, representados mediante SKOS
  - **recurso:** Identificación abstracta única y unívoca de un recurso u objeto físico o conceptual. Estos recursos son las representaciones atómicas de los documentos y recursos de información y suelen ser instancias de los conceptos que se definen en los vocabularios.

- **concepto:** Los conceptos son representaciones abstractas que se corresponden con las clases o propiedades de los vocabularios u ontologías utilizados para representar semánticamente los recursos. Además del concepto, se podrá representar una referencia unívoca a instancias concretas.

- **referencia:** Es una instancia, concepto o termino especifico. Se creara como un identificador opaco, que no contenga información del recurso.

Las URIS canónicas a su vez tienen como misión ofrecer una capa de abstracción, que permita no condicionar el diseño de la URI, a el impuesto por un determinado sistema de almacenamiento.

A su vez, para dar soporte al multilingüismo, se crea el concepto de **URI Canónica por idioma**, que básicamente es la asociación de una URI Canónica, con los distintos idiomas donde el recurso esta disponible. Como se puede apreciar en el esquema, existe una relación de 1:n entre las URIS Canónicas y las URIS Canónicas por idioma donde n, en este caso, sera le numero de idiomas en los que esta disponible el recurso.

Por otro lado, dando cumplimiento a la buena practica de ofrecer contenido semántico, la URI Canónicas por idioma, ofrecerán traducción de los componentes que conforman la URI mencionados anteriormente (por ejemplo el tipo recurso, será rec [recurso] en español y res [resource] en ingles).

#### URIS privadas o internas

Por otro lado las **URIs privadas o internas** proporcionan la ubicación real del recurso, en un determinado sistema de almacenamiento, de forma que siempre será posible la obtención del recurso en un idioma y sistema de almacenamiento concreto, mediante una invocación a su URI Canónica, con los parámetros de idioma y tipo de almacenamiento.

Este mapeo es bidireccional, es decir también se podrá obtener la URI Canónica o Canónica por idioma, dado una URI de almacenamiento local.

 ### Cambio de esquema

En cumplimiento del requisito de cambio de esquema de URIs, expresado en el pliego, se establece el siguiente mecanismo para su implementación.

Tanto en el fichero de configuración application.yaml como mediante la variable de entorno APP_URI_CANONICALURISCHEMA o APP_URI_CANONICALURILANGUAGESCHEMA es posible cambiar el patrón tanto de URIs Canónicas como de dichas URIs Canónicas por idioma (incluso de forma distinta para cada caso).

Los componentes de la URI, son los descritos en el documento de  [esquema de URIs](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/entregables_hito_1/08-Esquema_de_URIs_Hércules/ASIO_Izertis_ArquitecturaDeURIs.md), siguiendo el patrón para URIs Canónicas:

**http://{dominio}/[{subdominio}]/{tipo}/{concepto}[/{referencia}]**

Y  para URIs Canónicas para un determinado idioma:

**http://{dominio}/[{subdominio}]/{idioma}/{tipo}/{concepto}[/{referencia}]**

El esquema se define de la forma

```bash
# Canonical Schema
APP_URI_CANONICALURISCHEMA  = http://$domain$/$sub-domain$/$type$/$concept$/$reference$ 
# Canonical Langauge Schema
APP_URI_CANONICALURILANGUAGESCHEMA = http://$domain$/$sub-domain$/$type$/$concept$/$reference$
```

Donde podremos eliminar (algunos opcionales, tales como el sub-domain) o modificar el orden de los componentes (dichos componentes se definen en el documento de [esquema de URIs](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/entregables_hito_1/08-Esquema_de_URIs_Hércules/ASIO_Izertis_ArquitecturaDeURIs.md))

 ### Normalización

La Factoría de URIs implementa la normalización descrita en el documento [esquema de URIs](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/entregables_hito_1/08-Esquema_de_URIs_Hércules/ASIO_Izertis_ArquitecturaDeURIs.md), concretamente todos los puntos detallados a continuación:

- Han de ser únicos (al menos en su dominio). 
- Usar siempre minúsculas, salvo para los conceptos que podrán tener la primera letra en mayúscula.
- Eliminar caracteres propios de el idioma, tales como acentos, o signos de puntuación.
- Usar el guión medio (-) como separador de palabras.

## Modelo de datos

Es conveniente para mejorar la comprensión, conocer el modelo de datos, que soporta la funcionalidad de la librería de URIs.

### ![modelo datos](./images/model_data.png)

Entre las entidades presentes en el esquema podemos destacar:

### Entidad TYPE

Representa el componente tipo, descrito en el  [Esquema de URIs](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/entregables_hito_1/08-Esquema_de_URIs_Hércules/ASIO_Izertis_ArquitecturaDeURIs.md). El atributo code actúa como calve primaria y representa el código (de 3 caracteres) que será usado en el esquema de URIs, para las URIs canónicas y el atributo name, su descripción larga.

![Type](./images/type_entity.png)

### Entidad LANGUAGE

Modela los atributos necesarios para el modelado de un lenguaje aplicable al proyecto de Factoría de URIS. El atributo ISO, representa el idioma en codigo ISO 639-1, otros atributos tales como LANGUAGE, NAME, REGION, SCRIPT y VARIANT, representan metadatos extraídos de ese mismo código, y los atributos DOMAIN_NAME, SUB_DOMAIN_NAME, TYPE_NAME, CONCEPT_NAME y REFERENCE_NAME, representan la traducción al idioma indicado de los componentes del esquema de URIS  dominio, sub-dominio, tipo, concepto y referencia respectivamente. Por otro lado cabe destacar el atributo IS_DEFAULT, que determina si este, es el idioma por defecto. Solo un idioma podrá ser  definido como idioma por defecto y es obligado que este definido uno.

![Type](./images/language_entity.png)

### Entidad LANGUAGE_TYPE

Modela la unión de un lenguaje y un tipo, y por lo tanto ofrece la traducción de dicho tipo al lenguaje indicado, incluso en la construcción de la URI Canónica por idioma.

![Type](./images/language_type_entity.png)

### Entidad STORAGE_TYPE:

La entidad **STORAGE_TYPE** almacena información relativa al un determinado tipo de almacenamiento, por ejemplo en el estado actual del proyecto, Trellis y Wikibase. Mantiene asimismo la capacidad de guardar metadatos relativos a dichos sistemas tales como la URL del EndPoint SPARQL o de su API .

![Type](./images/storage_type_entity.png)



### Entidad CANONICAL_URI:

La entidad **CANONICAL_URI** representa un URI canónica para un determinado recurso, ya sea una clase, una propiedad o una instancia. 

![Type](./images/canonical_uri_entity.png)



### Entidad CANONICAL_URI_LANGUAGE:

 La entidad **CANONICAL_URI** representa un URI canónica para un determinado recurso, ya sea una clase, una propiedad o una instancia. 

![language](./images/canonical_uri_language_entity.png)

### Entidad LOCAL_URI: 

La entidad **LOCAL_URI** representa un URI local asociada con una URI Canónica por idioma y un tipo de almacenamiento. 

![language](./images/local_uri_entity.png)

## API REST de Factoría de URIS

El API Rest en su totalidad se encuentra documentado, mediante Swagger.

Swagger se desplegara de forma automática,  al ejecutar el proyecto, en el mismo host y el mismo puerto configurado en el application.yml.

Por lo tanto se podrá acceder a Swagger mediante una URL de es siguiente formato:  

[http://{HOST_FACTORIA_URIS}:[SWAGGER_PORT]/swagger-ui.html](http://localhost:9326/swagger-ui.html)

En caso de ejecutar en la maquina local, con la configuración presente actualmente en el fichero application.yml, la URI resultante será

http://localhost:9326/swagger-ui.html

Obteniendo el siguiente resultado

![swagger](./images/swagger.png)

Por otro lado, puede encontrarse una documentación exahustiva de los Endpoint desplegados por el API, en el documento

[Documentación API REST de la Factoría de URIs](documentacion_api_rest_de_la_factoria_de_uris.md)