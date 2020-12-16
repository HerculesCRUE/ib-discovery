![](./img/logos_feder.png)

| Fecha         | 17/12/2020                                                   |
| ------------- | ------------------------------------------------------------ |
| Proyecto      | [ASIO](https://www.um.es/web/hercules/proyectos/asio) (Arquitectura Semántica e Infraestructura Ontológica) en el marco de la iniciativa [Hércules](https://www.um.es/web/hercules/) para la Semántica de Datos de Investigación de Universidades que forma parte de [CRUE-TIC](http://www.crue.org/SitePages/ProyectoHercules.aspx) |
| Módulo        | Arquitectura Semántica                                       |
| Tipo          | Documentación                                                |
| Objetivo      | El presente documento describe los EndPoint expuestos por la librería de descubrimiento |
| Documentación | [Librería de descubrimiento](https://github.com/HerculesCRUE/ib-asio-docs-/blob/master/24-Librer%C3%ADa_de_descubrimiento/ASIO_Libreria_de_descubrimiento.md)<br/>[Manual de usuario](./manual_de_usuario.md) (documentación de alto nivel)<br />[Documentación técnica](./documentacion-tecnica.md) (documentación de bajo nivel)<br/>[Documentación API REST de la librería de descubrimiento](./documentacion_api_rest_de_la_libreria_de_descubrimiento.md) (documentación de bajo nivel)<br/>[README](../README.md)<br/>[docker](./docker.md) |

# API REST de librería de descubrimiento

La documentación de esta sección hará referencia a cada uno de los EndPoints desplegados por la librería de descubrimiento, apoyándose en la documentación proporcionada por Swagger, por lo tanto los enlaces que se facilitan para los EndPoint descritos en Swagger, solo estarán disponibles si se ha realizado el despliegue, y dicho despliegue se ha realizado en la misma máquina donde se encuentra la presente documentación. En otro caso es necesario cambiar el host y el puerto por aquellos donde la librería de URIs ha sido desplegada.

La librería de descubrimiento despliega los siguientes EndPoints:

### API REST Librería de descubrimiento

##### Implementación

Es implementado por el controlador [DiscoveryController] descrito en la [seccion controladores, de la documentacion tecnica](./documentacion-tecnica.md#Controladores) 

##### EndPoints

![swagger](./img/swagger.png)

###### GET /discovery/

Disponible en Swagger el siguiente [enlace](http://localhost:9327/swagger-ui.html#/discovery-controller/statusUsingGET)

**Semántica**

- Permite obtener el estado de la aplicación

**PETICIÓN:**

```
curl -X GET "http://localhost:9327/discovery/" -H "accept: */*"
```

**RESPUESTA**

```
{
  "name": null,
  "appState": "INITIALIZED",
  "states": {
    "REDIS": {
      "state": "UPLOAD_DATA",
      "lastDate": "2020-12-13T18:58:28.437+0000"
    },
    "ELASTICSEARCH": {
      "state": "UPLOAD_DATA",
      "lastDate": "2020-12-13T18:58:28.747+0000"
    },
    "CACHE": {
      "state": "UPLOAD_DATA",
      "lastDate": "2020-12-13T18:58:28.437+0000"
    }
  },
  "lastFilterDate": {},
  "stateCode": 200
}
```

###### POST /discovery/cahce/force-reload

Disponible en Swagger el siguiente [enlace](http://localhost:9327/swagger-ui.html#/discovery-controller/doForceReloadCacheUsingPOST)

**Semántica**

Permite solicitar la recarga de la cache.

**PETICIÓN:**

```
curl -X POST "http://localhost:9327/discovery/cache/force-reload" -H "accept: */*"
```

**RESPUESTA**

```
DONE
```

###### POST /discovery/entity-link

Disponible en Swagger el siguiente [enlace](http://localhost:9327/swagger-ui.html#/discovery-controller/findEntityLinkByNodeTripleStoreAndClassUsingPOST)

**Semántica**

Permite realizar una **búsqueda de similitudes para todas las entidades**, dentro del propio Backend SGI, aquellas almacenadas en el mismo Triple Store, y que pertenecen a la misma clase. En caso de encontrar similitudes con suficiente grado de similitud (por encima de el umbral definido por configuración para acciones automáticas), se desencadenaran peticiones al Backend SGI, para que realize la fusión de las entidades, es decir un UPDATE con los atributos mas actualizados de ambas entidades, y un o varias operaciones DELETE, para las entidades menos actualizadas.

Para entidades almacenadas en otros Backend o Triplestores (si lo indica el parámetro linkEntities), se generaran links, entre la entidad de el propio Backend SGI, y las entidades externas de otros Backend SGIs.

**Parámetros**

- **node:** (Requerido). Cadena de Texto.  Indica el nodo (Backend SGI) a partir del cual se quiere hacer la búsqueda de similitudes.

- **tripleStore:** (Requerido). Cadena de Texto.  Indica el Triple Store a partir del cual se quiere hacer la búsqueda de similitudes.

- **className:** (Requerido). Cadena de Texto.  Indica el nombre de la clase a partir del cual se quiere hacer la búsqueda de similitudes, para esa misma clase.

- **userId:** (Requerido). Cadena de Texto.  Identificador del usuario, para auditoria, y control de Jobs.

- **requestCode:** (Requerido). Cadena de Texto.  Código de respuesta que tiene una doble función. Por un lado identifica si una misma respuesta ha sido enviada multiples veces por un cliente, y en ese caso, se todas las demás, se ignoraran. Por otro lado en caso de procesarse como una respuesta asíncrona, y de propagarse la respuesta por Kafka, esta pueda ser filtrada por el usuario (aparecerá como key en el mensaje de respuesta).

- **applyDelta:** (Opcional). Booleano. Por defecto **true** (recomendado).  Si verdadero indica que solo se quiere realizar la búsqueda, para las entidades de la clase indicada, que hayan sufrido modificaciones en el triple store, desde la última petición del mismo tipo (mismo Backend, Triple Store y clase). En caso de **false**, se realizara la búsqueda en todas las entidades.

-  **doSynchronous:** (Opcional). Booleano. Por defecto **false** (recomendado).  Si falso indica que se desea hacer la petición de forma asíncrona. De esa forma la petición se encolara en una cola FIFO, y será procesada en su turno. Dado que el tiempo necesario para el proceso, es indeterminado, y el cliente quedaría bloqueado, se recomienda hacer siempre la petición de forma asíncrona. Los resultados de la petición, se propagaran por medio de un webhook o de cola Kafka, según la preferencia del usuario. En caso de verdadero, la petición se procesara inmediatamente, y el cliente quedara a la espera de la  respuesta.

-  **propague_in_kafka:** (Opcional). Booleano. Por defecto **true** (recomendado).  Si es verdadero las acciones a llevar a cabo por el Event Proccessor se propagara en kafka en la cola, en la el topic definido en la configuración del proyecto para tal efecto (kafka.topicDiscoveryAction.topic). En caso contrario, las acciones no serán propagadas por kafka.

- **webHook:** Endpoint del cliente, para enviar callback con la respuesta, cuando esta sea procesada.

  

**PETICIÓN:**

```
curl -X POST "http://localhost:9327/discovery/entity-link?applyDelta=true&className=GrupoInvestigacion&doSynchronous=true&linkEntities=false&node=um&propague_in_kafka=true&requestCode=12345&tripleStore=trellis&userId=1" -H "accept: */*"
```

**RESPUESTA**

```
{
  "state": {
    "appState": "INITIALIZED",
    "cacheState": "UPLOAD_DATA",
    "dataState": "UPLOAD_DATA",
    "elasticState": "UPLOAD_DATA"
  },
  "response": {
    "node": "um",
    "tripleStore": "trellis",
    "className": "GrupoInvestigacion",
    "startDate": "2020-12-13 20:25:21",
    "endDate": "2020-12-13 20:25:44",
    "status": "COMPLETED",
    "results": [],
    "userId": "1",
    "requestCode": "12345"
  }
}
```

###### POST /discovery/entity-link/instance

Disponible en Swagger el siguiente [enlace](http://localhost:9327/swagger-ui.html#/discovery-controller/findEntityLinkByEntityAndNodeTripleStoreAndClassUsingPOST)

**Semántica**

Permite realizar una búsqueda de similitudes entre la entidad enviada en el Body, y el resto de entidades dentro del propio Backend SGI, aquellas almacenadas en el mismo Triple Store, y que pertenecen a la misma clase. En caso de encontrar similitudes con suficiente grado de similitud (por encima de el umbral definido por configuración para acciones automáticas), se desencadenaran peticiones al Backend SGI, para que realize la fusión de las entidades, es decir un UPDATE con los atributos mas actualizados de ambas entidades, y un o varias operaciones DELETE, para las entidades menos actualizadas.

Para entidades almacenadas en otros Backend o Triplestores (si lo indica el parámetro linkEntities), se generaran links, entre la entidad de el propio Backend SGI, y las entidades externas de otros Backend SGIs.

Es especialmente útil para evitar insertar duplicados, si estos ya existen en el Triple Store, por ejemplo en el caso de la Factoría de Uris, que se integra con la librería de descubrimiento de modo que , mediante una llamada a este método, puede saber si la entidad ya existe, y en caso afirmativo, retorna la URI del objeto equivalente encontrado, en vez de generar una nueva, y por lo tanto, evitar asi generar un duplicado.

**Parámetros**

- **node:** (Requerido). Cadena de Texto.  Indica el nodo (Backend SGI) a partir del cual se quiere hacer la búsqueda de similitudes.
- **tripleStore:** (Requerido). Cadena de Texto.  Indica el Triple Store a partir del cual se quiere hacer la búsqueda de similitudes.
- **className:** (Requerido). Cadena de Texto.  Indica el nombre de la clase a partir del cual se quiere hacer la búsqueda de similitudes, para esa misma clase.
- **userId:** (Requerido). Cadena de Texto.  Identificador del usuario, para auditoria, y control de Jobs.
- **requestCode:** (Requerido). Cadena de Texto.  Código de respuesta que tiene una doble función. Por un lado identifica si una misma respuesta ha sido enviada multiples veces por un cliente, y en ese caso, se todas las demás, se ignoraran. Por otro lado en caso de procesarse como una respuesta asíncrona, y de propagarse la respuesta por Kafka, esta pueda ser filtrada por el usuario (aparecerá como key en el mensaje de respuesta).
- **applyDelta:** (Opcional). Booleano. Por defecto **true** (recomendado).  Si verdadero indica que solo se quiere realizar la búsqueda, para las entidades de la clase indicada, que hayan sufrido modificaciones en el triple store, desde la última petición del mismo tipo (mismo Backend, Triple Store y clase). En caso de **false**, se realizara la búsqueda en todas las entidades.
-  **doSynchronous:** (Opcional). Booleano. Por defecto **false** (recomendado).  Si falso indica que se desea hacer la petición de forma asíncrona. De esa forma la petición se encolara en una cola FIFO, y será procesada en su turno. Dado que el tiempo necesario para el proceso, es indeterminado, y el cliente quedaría bloqueado, se recomienda hacer siempre la petición de forma asíncrona. Los resultados de la petición, se propagaran por medio de un webhook o de cola Kafka, según la preferencia del usuario. En caso de verdadero, la petición se procesara inmediatamente, y el cliente quedara a la espera de la  respuesta.
-  **propague_in_kafka:** (Opcional). Booleano. Por defecto **true** (recomendado).  Si es verdadero las acciones a llevar a cabo por el Event Proccessor se propagara en kafka en la cola, en la el topic definido en la configuración del proyecto para tal efecto (kafka.topicDiscoveryAction.topic). En caso contrario, las acciones no serán propagadas por kafka.
- **webHook:** Endpoint del cliente, para enviar callback con la respuesta, cuando esta sea procesada.

**Body**: El objeto con el cual se quiere comparar si existe o no en el Triple Store

```
{
	"description": "PEDIATRÍA"
}
```



**PETICIÓN:**

```
curl -X POST "http://localhost:9327/discovery/entity-link/instance?className=GrupoInvestigacion&doSynchronous=true&entityId=12345678890&linkEntities=false&node=um&propague_in_kafka=true&requestCode=12345&tripleStore=trellis&userId=1" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"description\" : \"PEDIATRÍA\"}"
```

**RESPUESTA**

```
{
  "state": {
    "appState": "INITIALIZED",
    "cacheState": "UPLOAD_DATA",
    "dataState": "UPLOAD_DATA",
    "elasticState": "UPLOAD_DATA"
  },
  "response": {
    "node": "um",
    "tripleStore": "trellis",
    "className": "GrupoInvestigacion",
    "startDate": "2020-12-13 20:25:21",
    "endDate": "2020-12-13 20:25:44",
    "status": "COMPLETED",
    "results": [],
    "userId": "1",
    "requestCode": "12345"
  }
}
```

###### POST /discovery/entity/change

Disponible en Swagger el siguiente [enlace](http://localhost:9327/swagger-ui.html#/discovery-controller/entityChangeUsingPOST)

**Semántica**

Permite que la librería de descubrimiento sea notificada cada por el Event Proccesor cada vez que se produce un cambio en el triple store, de forma que la cache pueda ser actualizada.

**Parámetros**

- **node:** (Requerido). Cadena de Texto.  Indica el nodo (Backend SGI) a partir del cual se quiere hacer la búsqueda de similitudes.
- **tripleStore:** (Requerido). Cadena de Texto.  Indica el Triple Store a partir del cual se quiere hacer la búsqueda de similitudes.
- **className:** (Requerido). Cadena de Texto.  Indica el nombre de la clase a partir del cual se quiere hacer la búsqueda de similitudes, para esa misma clase.
- **action:** (Requerido). Cadena de Texto.  Acción realizada en el triple store. Puede ser UPDATE, INSERT o DELETE
- **entityLocalURI:** (Requerido). Cadena de Texto.  URI local en el triple store. Sirve para recuperar los datos de la entidad que cambio, en caso de un UPDATE o INSERT, y para borrarla de la cache en caso de un delete

**Body**: El objeto con el cual se quiere comparar si existe o no en el Triple Store

```
{
	"description": "PEDIATRÍA"
}
```



**PETICIÓN:**

```
curl -X POST "http://localhost:9327/discovery/entity/change?action=UPDATE&className=GrupoInvestidacion&entityLocalURI=http%3A%2F%2Fherc-iz-front-desa.atica.um.es%2FGrupoInvestigacion%2Fe6df58f9-b414-3624-84d5-2ae41fa0bd98&node=um&tripleStore=trellis" -H "accept: */*"
```

**RESPUESTA**

```
DONE
```

###### GET /discovery/entity/stats

Disponible en Swagger el siguiente [enlace](http://localhost:9327/swagger-ui.html#/discovery-controller/getEntityStatsUsingGET)

**Semántica**

Permite obtener las estadísticas asociadas a una determinada clase, dentro de un determinado Backend y triplestore.

**Parámetros**

- **node:** (Requerido). Cadena de Texto.  Indica el nodo (Backend SGI) a partir del cual se quiere hacer la búsqueda de similitudes.
- **tripleStore:** (Requerido). Cadena de Texto.  Indica el Triple Store a partir del cual se quiere hacer la búsqueda de similitudes.
- **className:** (Requerido). Cadena de Texto.  Indica el nombre de la clase a partir del cual se quiere hacer la búsqueda de similitudes, para esa misma clase.

**PETICIÓN:**

```
curl -X GET "http://localhost:9327/discovery/entity/stats?className=GrupoInvestigacion&node=um&tripleStore=trellis" -H "accept: */*"
```

**RESPUESTA**

```
{
  "stats": {
    "maxRelativeRatio": 1,
    "isEmpty": false,
    "attributesSize": 3,
    "attributes": {
      "uni": 0.06666667,
      "description": 1,
      "id": 1
    },
    "maxAttributesRelativeRatio": 1,
    "maxEntitiesRelativeRatio": 0,
    "entitiesSize": 0
  },
  "status": {
    "name": null,
    "appState": "INITIALIZED",
    "states": {
      "REDIS": {
        "state": "UPLOAD_DATA",
        "lastDate": "2020-12-13T19:43:48.012+0000"
      },
      "ELASTICSEARCH": {
        "state": "UPLOAD_DATA",
        "lastDate": "2020-12-13T19:43:53.425+0000"
      },
      "CACHE": {
        "state": "UPLOAD_DATA",
        "lastDate": "2020-12-13T19:43:48.012+0000"
      }
    },
    "lastFilterDate": {},
    "stateCode": 200
  }
}
```

