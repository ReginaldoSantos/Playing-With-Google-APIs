# Google Directory API Client

Testing Directory API as admin in Google Cloud Plataform (e. g. _Google For Work_ e _Google for Education_).

This client has both an abstraction to use Directory API services via command line as well as a client implementation with scope "ADMIN_DIRECTORY_USER" which allows viewing and managing the provisioning of users on your domain.


## Use

This client lib has a _DirectoryUserService_ class with static calls to User *CRUD* services from Directory API.


### Add To Your Project

```
    <dependency>
      <groupId>br.com.webnize</groupId>
      <artifactId>googleapis-admin-sdk-client</artifactId>
      <version>0.0.1-SNAPSHOT</version>
    </dependency>
```


### Packing...

```
$ mvn clean install
```


### First Steps

[Directory API: Getting Started](https://developers.google.com/admin-sdk/directory/v1/get-start/getting-started)

Access the [_Admin Console_](https://console.developers.google.com/?authuser=0) and create credentials to _Admin SDK_ API.

Once you have your own credentials file (_client_secret.json_) it could be add to:

1. Your jar file;
2. The current Directory;
3. The directory target with system variable *CLIENT_SECRET_JSON_PATH*;


#### Sample

Try [googleapis-admin-sdk-samples](../googleapis-admin-sdk-samples) to see the big picture.
It comes with 'maven-assembly-plugin' to generate a self-contained executable jar.


### Authentication

The _Google Directory API_ allows only OAuth2, so you must have a browser in place in order to authorize your app.

Implemented flow: [OAuth 2.0 for Installed Applications](https://developers.google.com/identity/protocols/OAuth2InstalledApp)

<img id="googleapis-oauth2-flow" alt="Google Installed Apps OAuth2 Flow" src="https://developers.google.com/accounts/images/webflow.png">

Besides that, after first authentication the credentials are stored at _.credentials/googleapis-admin-sdk-v1.json_ for further use.


#### Headless Authentication

Not possible at all, this is OAuth2.
But credentials could be copied and pasted between machines.

