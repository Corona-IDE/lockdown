# lockdown-gradle-plugin
[![Maven Central](https://img.shields.io/maven-central/v/com.coronaide.lockdown/lockdown-gradle-plugin.svg)](https://mvnrepository.com/artifact/com.coronaide.lockdown/lockdown-gradle-plugin)

The Lockdown Gradle plug-in is intended for use in Gradle builds, to allow teams to easily expose the ability to add/update credentials in credential storage files without requiring all team members to install the Lockdown CLI.

## Usage

Add the Lockdown Gradle plug-in to your buildscript classpath, and then use the AddCredentialsTask to allow developers to add encrypted values

### Add Task to build.gradle

```
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath group: 'com.coronaide.lockdown', name: 'lockdown-gradle-plugin', version: '0.1.2'
    }
}

task addCredentials(type: org.starchartlabs.lockdown.gradle.task.AddCredentialsTask){
    publicKey "${projectDir}/id_rsa.pub"
    credentialFile "${projectDir}/credentials.properties"
}
```

### Call Task From the Command line

```
./gradlew addCredentials -PlookupKey=<lookupKey>
```

A dialog will appear to accept the credentials - once entered, they are encrypted and stored.

The -P argument for the `lookupKey` determines which set of credentials is updated or created
