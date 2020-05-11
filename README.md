# ASIO - Triples Storage Adapter

Arquetipo Java para API Rest con base de datos relacional

## OnBoarding

Para iniciar el entorno de desarrollo se necesita cumplir los siguientes requisitos:

* OpenJDK 11 (en caso de querer JDK8: Oracle JDK 8)
* Eclipse JEE 2019-09 con plugins:
** Spring Tools 4
** m2e-apt
** Lombok
* Docker


### Instalar Lombok

Para la instalación de Lombok, es preciso descargar la última versión desde [https://projectlombok.org/download](https://projectlombok.org/download). Se descargará un jar que precisa ser ejecutado:

	java -jar lombok.jar

Se seleccionará la ubicación en la que se encuentra instalado Eclipse.

En caso que de problemas a la hora de generar las clases de Mapstruct, es preciso utilizar una versión parcheada de lombok. Para ello, se ha dejado en \\rackstation\Desarrollo\fuentes\Entorno de desarrollo\Eclipses el fichero lombok-patched-1.18.6.jar. Se deberá configurar en el fichero eclipse.ini, sustituyendo el jar que tiene configurado actualmente por el parcheado

```
-javaagent:C:\desarrollo\java\install\eclipse-jee-2018-12-R-win32-x86_64\lombok-patched-1.18.6.jar
```

## Metodología de desarrollo

La metodología de desarrollo es Git Flow.

## Java 11

La aplicación está preparada para funcionar con JDK 11. En caso de necesitar trabajar con un JDK anterior, es preciso especificar una propiedad en el POM:

```xml
<properties>
	<java.version>1.8</java.version>
</properties>
```

Para descargar JDK 11, se precisa utilizar openjdk, la cual se puede obtener de https://jdk.java.net/11/

### Swagger

Se ha añadido la posibilidad de utilizar Swagger, el cual se ha configurado como Starter:

Para acceder a Swagger, se utilizará la siguiente URL:

http://localhost:8080/swagger-ui.html
