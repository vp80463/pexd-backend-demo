Blank project v1.0
===
![][bg-jdk] ![][bg-springboot] ![][bg-gradle]

## Introduction
 Base on YNA-G3-Solid framework code.

## Commands

Prerequisites:

- JDK 17.+ - check with command "`java -version`"

### Show the version of gradlew

```shell 
gradlew -v
```
Output is the full version of gradle:

```text
------------------------------------------------------------
Gradle 8.5
------------------------------------------------------------

Build time:   2023-11-29 14:08:57 UTC
Revision:     28aca86a7180baa17117e0e5ba01d8ea9feca598

Kotlin:       1.9.20
Groovy:       3.0.17
Ant:          Apache Ant(TM) version 1.10.13 compiled on January 4 2023
JVM:          17.0.9 (Eclipse Adoptium 17.0.9+9)
```

### Set environment
This project depends on the Solid packages stored in Github packages.    
So personal access token is required for downloading packages.  
Add ```PACKAGE_USERNAME``` and ```PACKAGE_REPO_TOKEN``` as environment variables.  
> PACKAGE_USERNAME: Github username (belongs to YMSL Enterprise)  
> PACKAGE_REPO_TOKEN: Personal access token generated by Github (requires ```read:packages``` permission)

### Build an eclipse project

```shell
gradlew eclipse --refresh-dependencies
```
The source jars or dependencies can be downloaded by this command.  

### Run application in command line

- Run springboot application with default 'development' active profile

   ```shell
     gradlew appRun
   ```
- Run springboot application with other active profiles

   ```shell
     gradlew appRun -PappProfiles=production
   ```

### Build a jar/war file 

```shell
gradlew clean build
``` 

the target distributable war file will be found in `blank-web\build\libs\blank-web-1.0.0-boot.jar`.　　

### Run the jar file
Execute in jar run mode:

```bash
java -jar blank-web\build\libs\blank-web-1.0.0-boot.jar \ 
--spring.profiles.active=production \
--DB_USERNAME=postgres \
--DB_PASSWORD=postgres \
--DB_URL=jdbc:postgresql://localhost:15432/postgres
```
or execute the jar directly
``` bash
./blank-web/build/libs/blank-web-1.0.0-boot.jar
```
For the executable jar, the parameters can be set via environment variables.  

### Run the jar file with docker
After build the jar file, the docker-compose file can be used to run the jar file.  
```shell
docker-compose up -d
```
If folder name changed, `Dockerfile` should be modified too.  
``` Dockerfile
# USER $USERNAME
ARG BOOT_JAR_DIR=./blank-web/build/libs
```

## Modules 

### Blank Domain

Default, it includes following tools:

- **Embedded H2**
    
    An embedded memory database. It can be replaced by other jdbc driver.

- **HikariCP**

    The default database connection pooling tool. A default local datasource transaction manager
    was created. 
    In scenarios with multiple datasources and JTA distributed transaction manager, use 'Atomikos' instead of 'HikariCP'.

### Blank Web

- The embedded `Undertow` is the default web application server. You can
replace it with the other type server named '`Tomcat`'. 

- The JSP parsing feature is disabled by default.

## Login  
- Login url: [http://localhost:8080/blank](http://localhost:8080/blank)
- Login user/password: `user`/`user`

## Swagger
Swagger spring doc is default included in this project.  
Access [http://localhost:8080/blank/swagger-ui/index.html] to view the API document.  
Swagger is not active in production environment(by setting profile to `production`), but the packages would be included.  
If want to exclude swagger packages in production environment, build the project with command:  
```shell
./gradlew clean build -Pprofile=production
```
Then most of the swagger dependencies would be excluded, only annotation would be included for compile.  

[bg-jdk]: https://img.shields.io/badge/jdk-17-brightgreen.svg?style=flat&logo=java&color=information&labelColor=important
[bg-gradle]: https://img.shields.io/badge/gradle-8.4-information.svg?labelColor=blue
[bg-springboot]: https://img.shields.io/badge/springboot-3.2.0-information.svg?labelColor=9cf
