# Lockdown

[![Travis CI](https://img.shields.io/travis/Corona-IDE/lockdown.svg?branch=master)](https://travis-ci.org/Corona-IDE/lockdown) [![Code Coverage](https://img.shields.io/codecov/c/github/Corona-IDE/lockdown.svg)](https://codecov.io/github/Corona-IDE/lockdown) [![Black Duck Security Risk](https://copilot.blackducksoftware.com/github/groups/Corona-IDE/locations/lockdown/public/results/branches/master/badge-risk.svg)](https://copilot.blackducksoftware.com/github/groups/Corona-IDE/locations/lockdown/public/results/branches/master) [![Quality Gate](https://sonarqube.com/api/badges/gate?key=com.coronaide:lockdown)](https://sonarqube.com/dashboard/index/com.coronaide:lockdown) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


When you simply can't avoid storing basic credentials

Usually, when storing credentials, one-way encryption is used to protect sensitive information. However, some libraries and other systems require access to a set of clear-text credentials for systems such as remote applications or databases.

Lockdown is intended for those cases - where the need for clear-text credentials cannot be eliminated from an application. It applies a thin convenience layer on top of the established bouncycastle encryption libraries which allows simpler use the RSA public/private key encryption pattern.

## Contributing

Information for how to contribute to Lockdown can be found in [CONTRIBUTING.md](CONTRIBUTING.md)

## Projects

### lockdown-core

Lockdown Core is where the central key generation and encryption/decryption APIs are defined. This library is intended for use within applications consuming lockdown, and allows access to credential storage files created programmatically, with the CLI, or via the Gradle plug-in.

#### Usage

- lookupKey: A unique label for a set of credentials in a store. Determined when credentials are added to the store
- credentialStorePath: Path to the credential storage file
- privateKeyPath: Path to the private key used to decrypt credentials

```
private void loadCredentials(String lookupKey Path credentialStorePath, Path privateKeyPath){
  CredentialStore store = CredentialStore.loadOrCreate(credentialStorePath);

  store.accessCredentials(lookupKey, this::accessCredentials);
}

private void applyCredentials(String username, char[] password){
  //Use credentials - it is recommended to NOT store them in non-local variables
}
```

### lockdown-cli

Lockdown CLI is a command line tool for creating keys and adding credentials to credential store files.

#### Usage

Download the Lockdown CLI capsule from the GitHub Releases page. Entering no arguments will display usage information

```
java -jar lockdown-cli-<version>-capsule.jar (generate|addkey) [options] arguments....
```

**Note:** When adding keys, the password will not be displayed as it is typed for security reasons (similar to behavior when logging in via ssh)

### lockdown-gradle-plugin

The Lockdown Gradle plug-in is intended for use in Gradle builds, to allow teams to easily expose the ability to add/update credentials in credential storage files without requiring all team members to install the Lockdown CLI.

#### Usage

TODO romeara Setup documentation in progress
