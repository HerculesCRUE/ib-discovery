-- asio_jobs
CREATE DATABASE IF NOT EXISTS `asio_jobs` ;
CREATE USER IF NOT EXISTS 'asio_jobs'@'%' IDENTIFIED BY 'uC^XZv1ltaer';
GRANT ALL PRIVILEGES ON asio_jobs.* TO 'asio_jobs'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

-- input
CREATE DATABASE IF NOT EXISTS `input` ;
CREATE USER IF NOT EXISTS 'input'@'%' IDENTIFIED BY 'Y%ahhr&IlHB&';
GRANT ALL PRIVILEGES ON input.* TO 'input'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

-- uris
CREATE DATABASE IF NOT EXISTS `uris` ;
CREATE USER IF NOT EXISTS 'uris'@'%' IDENTIFIED BY 'aGTA7C!$gluY';
GRANT ALL PRIVILEGES ON uris.* TO 'uris'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;


-- discovery_services
CREATE DATABASE IF NOT EXISTS `services` ;
CREATE USER IF NOT EXISTS 'services'@'%' IDENTIFIED BY 'DVGrBLGPUsXt';
GRANT ALL PRIVILEGES ON services.* TO 'services'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

-- federation
CREATE DATABASE IF NOT EXISTS `federation` ;
CREATE USER IF NOT EXISTS 'federation'@'%' IDENTIFIED BY 'Tdzy3wSM66kg';
GRANT ALL PRIVILEGES ON federation.* TO 'federation'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

-- discovery
CREATE DATABASE IF NOT EXISTS `discovery` ;
CREATE USER IF NOT EXISTS 'discovery'@'%' IDENTIFIED BY 'QEY8UgzXKqq2';
GRANT ALL PRIVILEGES ON discovery.* TO 'discovery'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

-- keycloak
CREATE DATABASE IF NOT EXISTS `keycloak` ;
CREATE USER IF NOT EXISTS 'keycloak'@'%' IDENTIFIED BY 'uHkSyrvUsQD&';
GRANT ALL PRIVILEGES ON keycloak.* TO 'keycloak'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

-- etl
CREATE DATABASE IF NOT EXISTS `etl` ;
CREATE USER IF NOT EXISTS 'etl'@'%' IDENTIFIED BY 'OgnaWL*C2Wit';
GRANT ALL PRIVILEGES ON etl.* TO 'etl'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

-- wiki_as
CREATE DATABASE IF NOT EXISTS `wiki_as` ;
CREATE USER IF NOT EXISTS 'wiki_as'@'%' IDENTIFIED BY 'pkvIStIe!Yw1';
GRANT ALL PRIVILEGES ON wiki_as.* TO 'wiki_as'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

-- wiki_io
CREATE DATABASE IF NOT EXISTS `wiki_io` ;
CREATE USER IF NOT EXISTS 'wiki_io'@'%' IDENTIFIED BY 'ZVRJUlA03#wZ';
GRANT ALL PRIVILEGES ON wiki_io.* TO 'wiki_io'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

-- umasio
CREATE DATABASE IF NOT EXISTS `umasio` ;
CREATE USER IF NOT EXISTS 'umasio'@'%' IDENTIFIED BY 'R5HCUMtYwa7W';
GRANT ALL PRIVILEGES ON umasio.* TO 'umasio'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

