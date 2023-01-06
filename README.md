# Microsoft Graph Deploy Maven Plugin

This maven plugins can deploy artifacts to any drive reachable using the
[Graph API](https://developer.microsoft.com/en-us/graph/).

This plugins can (for now) only use the **Client Credentials flow** with a **Client Secret** as it was initially
intended to be used in a pipeline for automatic deployment.

:warning: This plugin overrides existing data on the Drive without notice!

## Usage

This plugin is not deployed to central, maybe it will one day if it's mature enough and has users.

For now on you'll have to add the follwing repository to get the plugin:

```xml
  <pluginRepositories>
    <pluginRepository>
      <id>jobayle-maven-plugins</id>
      <url>https://pkgs.dev.azure.com/jobayle/jobayle-maven-repo/_packaging/jobayle-maven-plugins/maven/v1</url>
    </pluginRepository>
  </pluginRepositories>
```

## Configuration

* `tenantId` Azure AD credentials: Tenant ID (usually a UUID).
* `clientId` Azure AD credentials: Client ID (usually a UUID).
* `clientSecret` Azure AD credentials: Client Secret (an arbitrary string).
* `artifact` path to artifact to deploy (defaults to `${project.build.directory}/${artifactId}-${version}.jar`).
* `drive` Path to drive to deploy to (eg: `sites/{site-id}/drives/{drive-id}`)

## Example configuration

I recommend you put this configuration in a special profile so the deploy is only triggered on a pipeline.

In your **pom.xml**:

```xml
  <build>
    <plugins>
      <plugin>
        <groupId>eu.jobayle.maven.plugins</groupId>
        <artifactId>msgraph-deploy-maven-plugin</artifactId>
        <version>0.1.0</version>
        <configuration>
          <tenantId>${TENANT_ID}</tenantId>
          <clientId>${CLIENT_ID}</clientId>
          <clientSecret>${CLIENT_SECRET}</clientSecret>
          <drive>sites/${SITE_ID}/drive</drive>
        </configuration>
      </plugin>
    </plugins>
  </build>
```
