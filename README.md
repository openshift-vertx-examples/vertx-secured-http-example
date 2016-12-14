# Introduction

This project exposes a simple OAuth2 secured REST endpoint where the service greeting is available at this address http://hostname:port/greeting and returns a json Greeting message

{
    "content": "Hello, World!",
    "id": 1
}

The id of the message is incremented for each request. To customize the message, you can pass as parameter the name of the person that you want to send your greeting.

# Build

To build the project, open a terminal and execute this apache maven command

```
mvn clean package
```

# Launch and Test

To start the Eclipse Vert.x application, execute this Java command within a terminal

```
java -jar target/vertx-rest-1.0-SNAPSHOT-fat.jar run org.jboss.obsidian.quickstart.RestApplication -conf config.json
```

Where `config.json` is the application configuration. This file should contain a verbatin copy of RedHat SSO exported
JSON plus some optional configuration such as http port, oauth callback path and public facing url, e.g.:

```
{
  "realm": "master",
  "realm-public-key": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhWwqt5bCOA2eSjZ7vib3/f7CC6ZQR+UJF/2rhQSA3FUaHjUhdUv/G6wB9WCU7U1n/vh0mSPI1A0aygkqNrvDh7uoKx+5zXXEG+FOgG34PrGePVbF3R5R7goBiPpdusFu5c7fJWoux9wZgS5qkEjwGxoby3nwrdeFzYdNu2A5G32jy9Xhyqj1lwiFq3tZHUTYQCBEgYB7n7c+1xXP1dc5TC3eLeKa64/WDGFDGdKsOqFO/869XAAdJgaup8gR/mDL2uP+eZAcZ4KmzFLjrF5Sb4xjDctvnh20B9MIzGQTAhygGkHmsnTTZ42/RX2ibjcBN4P+8PZvnUy8kUiAobTB9QIDAQAB",
  "auth-server-url": "http://keycloak:8080/auth",
  "ssl-required": "external",
  "resource": "Obsidian",
  "credentials": {
    "secret": "2cd928dd-0331-459a-9699-8a8e9772782c"
  },
  
  "http.port": 8080,
  "oauth2.public.url": "http://localhost:8080",
  "oauth2.callback": "/callback"
}
```

and execute some HTTP Get requests to get a response from the Rest endpoint.

```
http http://localhost:8080/greeting
http http://localhost:8080/greeting name==Charles
```

If the pod of the Secured Vert.x Application is running like also the Red Hat SSO Server, you 
can use one of the bash scripts proposed within the root of the project to access the service.

Depending which tool you prefer to use (curl or httpie), use one of bash files available and pass as parameters
the address of the Red Hat Secured SSO Server and the Secured Spring Boot Application. 

```
./httpie_token_req.sh https://secure-sso-sso.e8ca.engint.openshiftapps.com http://secure-vertx-rest-sso.e8ca.engint.openshiftapps.com
./curl_token_req.sh https://secure-sso-sso.e8ca.engint.openshiftapps.com http://secure-vertx-rest-sso.e8ca.engint.openshiftapps.com
```

The URLs of the Red Hat SSO & Vert.x Application are created according to this convention:

* Red Hat Secured SSO : <secured_sso_route>.<namespace>.<host_machine>
* Secured Vert.x Application : <secured_vertx_route>.<namespace>.<host_machine>

You can find such routes using this oc client command `oc get routes` or the Openshift Console.


# Launch using Vert.x maven plugin

```
mvn clean package vertx:run
```

# OpenShift

- To build & deploy

```
mvn clean package fabric8:deploy -Popenshift
```
- And to run/launch the pod

```
mvn fabric8:start -Popenshift
```

# OpenShift Online

- Connect to the OpenShift Online machine (e.g. https://console.dev-preview-int.openshift.com/console/command-line) to get the token to be used by the oc client to be authenticated and access the project
- Open a terminal and execute this command using the oc client where you will replace the MYTOKEN with the one that you can get from the Web Console
```
oc login https://api.dev-preview-int.openshift.com --token=MYTOKEN
```
- Use the Fabric8 Maven Plugin to launch the S2I process on the OpenShift Online machine
```
mvn clean package fabric8:deploy -Popenshift
```
- And to run/launch the pod
```
mvn fabric8:start -Popenshift
```
- Create the route to access the service 
```
oc expose service/vertx-rest --port=8080 
```
- Get the route url
```
oc get route/vertx-rest
NAME         HOST/PORT                                                    PATH      SERVICE           TERMINATION   LABELS
vertx-rest   vertx-rest-obsidian.1ec1.dev-preview-int.openshiftapps.com             vertx-rest:8080                 expose=true,group=org.jboss.obsidian.quickstart,project=vertx-rest,provider=fabric8,version=1.0-SNAPSHOT
```
- Use the Host/Port address to access the REST endpoint
```
http http://vertx-rest-obsidian.1ec1.dev-preview-int.openshiftapps.com/greeting
http http://vertx-rest-obsidian.1ec1.dev-preview-int.openshiftapps.com/greeting?name=Charles
```
