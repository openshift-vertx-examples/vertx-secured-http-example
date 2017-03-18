# Introduction

This project exposes a simple REST endpoint where the service `greeting` is available, but properly secured, at this
address `http://hostname:port/greeting` and returns a json Greeting message after the application issuing the call to
the REST endpoint has been granted to access the service.

```
{
    "content": "Hello, World!",
    "id": 1
}

```

The id of the message is incremented for each request. To customize the message, you can pass as parameter the name of
the person that you want to send your greeting.

To manage the security, roles & permissions to access the service, a 
[Red Hat SSO](https://access.redhat.com/documentation/en/red-hat-single-sign-on/7.0/securing-applications-and-services-guide/securing-applications-and-services-guide)
backend will be installed and configured for this project.

It relies on the Keycloak project which implements the `OpenId` connect specification which is an extension of the
`Oauth2` protocol.

After a successful login, the application will receive an `identity token` and an `access token`. The identity token
contains information about the user such as username, email, and other profile information.

The access token is digitally signed by the realm and contains access information (like user role mappings)

This is typically this `access token` formatted as a JSON Token that the Vert.x application will use to allow access to
the application.

The configuration of the adapter is defined within the `app/src/main/java/org/obsidiantoaster/quickstart/RestApplication.java`
file using these environment properties:

```
$REALM
$REALM_PUBLIC_KEY
$SSO_HOST
$CLIENT_APP
$CLIENT_SECRET
```

Note that the config object is what you would download from the Keycloak admin console. Just for simplicity we encode it
in the source code but you could just load the json at runtime.

One can request a token manually using the `OpenID-Connect` protocol. A typical request would be: 

```
https://<SSO_HOST>/auth/realms/<REALM>/protocol/openid-connect/token?client_secret=<SECRET>&grant_type=password&client_id=CLIENT_APP
```

And the HTTP requests accessing the endpoint/Service will include the Bearer Token

```
http://<Vert.x_App>/greeting -H "Authorization:Bearer <ACCESS_TOKEN>"
```

Alternatively one can use helper libraries has for example in the html demo `app/src/main/resources/webroot/index.html`
where the official `keycloak.js` is used to simplify the interaction with the server.

The project is split into two Apache Maven modules - `app` and `sso`.

The `App` module exposes the REST Service using as technology Vert.x while the `sso` module contains the OpenShift
objects required to deploy the Red Hat SSO Server 7.0 along with the "app" module.

The goal of this project is to deploy the quickstart against an OpenShift environment (online, dedicated, ...).

# Prerequisites

To get started with this quickstart you'll need the following prerequisites:

Name | Description | Version
--- | --- | ---
[java][1] | Java JDK | 8
[maven][2] | Apache Maven | 3.3.x
[oc][3] | OpenShift Client | v3.3.x
[git][4] | Git version management | 2.x

[1]: http://www.oracle.com/technetwork/java/javase/downloads/
[2]: http://maven.apache.org/download.cgi 
[3]: https://docs.openshift.com/enterprise/3.2/cli_reference/get_started_cli.html
[4]: https://git-scm.com/book/en/v2/Getting-Started-Installing-Git

In order to build and deploy this project, you must have an account on an OpenShift Online (OSO): https://console.dev-preview-int.openshift.com/ instance.

# Build

In order to build and deploy this project, it is required to have an account on an OpenShift Online (OSO): 
`https://console.dev-preview-stg.openshift.com/` instance or you're welcome to setup your own OpenShift env; via
`minishift`.

Once you have this, along with the
[OpenShift CLI tool](https://docs.openshift.com/online/cli_reference/get_started_cli.html), you're ready to go.

Open a terminal, log on to the OpenShift Server `oc login https://<OPENSHIFT_ADDRESS> --token=MYTOLEN` when you use 
OpenShift Online or Dedicated.

Create a new project on OpenShift `oc new-project <some_project_name>` and next build the quickstart

```
mvn clean install -Popenshift
```

# Launch / deploy

To deploy the whole secured app, first move to sso/ dir, and then simply use the `Fabric8` Maven Plugin with the goals
`deploy` and `start`:

```
cd sso
mvn fabric8:deploy -Popenshift
```

Open OpenShift console in the browser to see the status of the app, and the exact routes, to be used to access the app's
greeting endpoint or to access the Red Hat SSO's admin console.

Note: until https://issues.jboss.org/browse/CLOUD-1166 is fixed, we need to fix the redirect-uri in RH-SSO admin
console, to point to our app's route.

To specify the Red Hat SSO URL to be used by the Vert.x Application, it is required to change the SSO_URL env variable
assigned to the DeploymentConfig object. You can change this value using the following oc command where the https server
to be defined corresponds to the location of the Red Hat SSO Server running in OpenShift.

```
oc env dc/secured-vertx-rest SSO_URL=https://secure-sso-myproject.192.168.178.12.xip.io/auth
```

Finally in order to allow both web and API access to keycloak 3 changes need to be done in the default configuration.
Open the SSO admin console `https://secure-sso-myproject.192.168.178.12.xip.io/auth/admin/master/console`, goto clients
and select `demoapp`. In this view we need to change:

* `Access Type`: to `public`
* `Valid Redirect URIs`: to `http://vertx-rest-sso.e8ca.engint.openshiftapps.com/*` (your application URL slash start)
* `Web Origins`: to `*`

These are just indications for better security you should restrict the wildcards.

# Access the service

You can experiment at an high level with the service by using the web client demo. Just open the root directory of the
server. Of course we are demonstrating APIs so you can interact with then using plain HTTP clients such as curl.

If the pod of the Secured Vert.x Application is running like also the Red Hat SSO Server, you
can use the bash scripts proposed within the root of the project to access the service.

use the following bash script and pass as parameters the address of the Red Hat Secured SSO Server and the Secured
Vert.x Application.

```
./scripts/token_req.sh https://secure-sso-ssovertx.e8ca.engint.openshiftapps.com http://vertx-rest-sso.e8ca.engint.openshiftapps.com
```

The URLs of the Red Hat SSO & Vert.x Application are created according to this convention:

* Red Hat Secured SSO : <secured_sso_route>.<namespace>.<host_machine>
* Secured Vert.x Application : <secured_vertx_route>.<namespace>.<host_machine>

You can find such routes using this oc client command `oc get routes` or the Openshift Console.

# Access the service using a user without admin role

The patterns property defines as pattern, the `/greeting` endpoint which means that this endpoint is protected by
Keycloak. Every other endpoint that is not explicitly listed is NOT secured by Keycloak and is publicly available.

To verify that a user without the `admin` role can't access the service, you will create a new user using the following
bash script

```
./scripts/add_user.sh <SSO_HOST> <Vert.x_HOST>
```

Next, you can call again the greeting endpoint by issuing a HTTP request where the username is `bburke` and the password
`password`. In response, you will be notified that yoou can't access to the service

```
./scripts/token_user_req.sh <SSO_HOST> <Vert.x_HOST>
```
