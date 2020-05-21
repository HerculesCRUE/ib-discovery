# Compilación

Se indicará a continuación los pasos que hay que seguir para llevar a cabo la generación del artefacto.

## Prerrequisitos

Se precisa disponer los siguientes elementos configurados:

* OpenJDK 11
* Maven 3.6.x

## Compilación

Para realizar la compilación se ejecutará el siguiente comando:

```bash
mvn clean package
```

También sería posible instalar o desplegar los artefactos sustituyendo `package` por `install` o `deploy` respectivamente.

Los artefactos se generarán dentro del directorio `target` de cada uno de los módulos:

### Back

Los artefactos se encuentran dentro de discovery-back/target

* Artefacto: discovery-back-{version}.jar

### Service

Los artefactos se encuentran dentro de discovery-service/target

* Artefacto: discovery-service-{version}.jar

### Swagger

Los artefactos se encuentran dentro de discovery-swagger/target

* Artefacto: discovery-swagger-{version}.jar
