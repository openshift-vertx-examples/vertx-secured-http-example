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
$SSO_AUTH_SERVER_URL
$CLIENT_APP
$CLIENT_SECRET
```

Alternatively one can use helper libraries has for example in the html demo `app/src/main/resources/webroot/index.html`
where the official `keycloak.js` is used to simplify the interaction with the server.

The project is split into two Apache Maven modules - `app` and `sso`.
The `App` module exposes the REST Service using WildflySwarm.
The `sso` module is a submodule link to the [redhat-sso](https://github.com/obsidian-toaster-quickstarts/redhat-sso) project
 that contains the OpenShift objects required to deploy the Red Hat SSO Server 7.0 as well as a Java command line client
 driver to access this secured endpoint.

The goal of this project is to deploy the quickstart in an OpenShift environment (online, dedicated, minishift, ...).


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

The first time you clone this secured-swarm-rest project, you need to initialize the sso submodule. You can do this by
either:
  1. using `git clone --recursive https://github.com/obsidian-toaster-quickstarts/secured_rest-vertx`

or

  1. using `git clone https://github.com/obsidian-toaster-quickstarts/secured_rest-vertx`
  1. `cd sso`
  1. `git submodule init`
  1. `git submodule update`

# Setting up OpenShift and the RH SSO Server

If you have not done so already, open up the sso/README.adoc or view it online [here](https://github.com/obsidian-toaster-quickstarts/redhat-sso/blob/master/README.adoc)
and follow the OpenShift Online section to setup your OpenShift environment and deploy the RH SSO server.

Make note of the SSO_AUTH_SERVER_URL value you received after deploying the RH SSO server. If you missed that step, return
to [Determine the SSO_AUTH_SERVER_URL value](https://github.com/obsidian-toaster-quickstarts/redhat-sso/blob/master/README.adoc#determine-the-sso_auth_server_url-value)
section and follow the instruction to obtain it.

# Build and deploy the Application

The WildFly Swarm application needs to be packaged and deployed. This process will generate the uber jar file, the OpenShift resources
and deploy them within the namespace of the OpenShift Server. Make sure you pass in the SSO_AUTH_SERVER_URL you
obtained during the deployment of the RH SSO server.

    ```
    cd app
    mvn fabric8:deploy -DSSO_AUTH_SERVER_URL=<SSO_AUTH_SERVER_URL from above...> -Popenshift -DskipTests=true
    ```
# Access the Secured Endpoints

Return to the sso/README.adoc or view it online [here](https://github.com/obsidian-toaster-quickstarts/redhat-sso/blob/master/README.adoc)
and continue at the "Access the Secured Endpoints" section.

## Example output

```bash
[sso 765]$ java -jar target/sso-client.jar --app secured-vertx-rest
Successful oc get routes: Yes
Using auth server URL: https://secure-sso-sso.e8ca.engint.openshiftapps.com/auth
Available application endpoint names: [secured-vertx-rest, secured-springboot-rest]

Requesting greeting...
{
  "id" : 3,
  "content" : "Hello, World!"
}
```
