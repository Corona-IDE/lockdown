# Lockdown

[![Travis CI](https://img.shields.io/travis/Corona-IDE/lockdown.svg?branch=master)](https://travis-ci.org/Corona-IDE/lockdown) [![Code Coverage](https://img.shields.io/codecov/c/github/Corona-IDE/lockdown.svg)](https://codecov.io/github/Corona-IDE/lockdown) [![Black Duck Security Risk](https://copilot.blackducksoftware.com/github/groups/Corona-IDE/locations/lockdown/public/results/branches/master/badge-risk.svg)](https://copilot.blackducksoftware.com/github/groups/Corona-IDE/locations/lockdown/public/results/branches/master) [![Quality Gate](https://sonarqube.com/api/badges/gate?key=com.coronaide:lockdown)](https://sonarqube.com/dashboard/index/com.coronaide:lockdown) [![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT) [![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/997/badge)](https://bestpractices.coreinfrastructure.org/projects/997)

* [Contributing](#contributing)
* [Legal](#legal)
* [Reporting Vulnerabilities](#reporting-vulnerabilities)
* [Projects](#projects)
    * [lockdown-core](#lockdown-core)
    * [lockdown-cli](#lockdown-cli)
    * [lockdown-gradle-plugin](#lockdown-gradle-plugin)
* [Handling Key Files](#handling-key-files)

When you simply can't avoid storing basic credentials

Usually, when storing credentials, one-way encryption is used to protect sensitive information. However, some systems require access to a set of clear-text credentials for authentication.

Lockdown is intended for those cases - where the need for clear-text credentials cannot be eliminated from an application. It applies a thin convenience layer on top of the established bouncycastle encryption libraries which allows simpler use the RSA public/private key encryption pattern.

## Contributing

Information for how to contribute to Lockdown can be found in [the contribution guidelines](CONTRIBUTING.md)

## Legal

Lockdown is distributed under the [MIT License](https://opensource.org/licenses/MIT). There are no requirements for using it in your own project (a line in a NOTICES file is appreciated but not necessary for use)

The requirement for a copy of the license being included in distributions is fulfilled by a copy of the [LICENSE](./LICENSE) file being included in constructed JAR archives

## Reporting Vulnerabilities

If you discover a security vulnerability, contact the development team by e-mail at corona.ide.dev@gmail.com

## Projects

### lockdown-core
[![Maven Central](https://img.shields.io/maven-central/v/com.coronaide.lockdown/lockdown-core.svg)](https://mvnrepository.com/artifact/com.coronaide.lockdown/lockdown-core)

Lockdown Core is where the central key generation and encryption/decryption APIs are defined. This library is intended for use within applications consuming lockdown, and allows access to credential storage files.

Usage information can be found in lockdown-core's [README](./lockdown-core/README.md)

### lockdown-cli
[![Maven Central](https://img.shields.io/maven-central/v/com.coronaide.lockdown/lockdown-cli.svg)](https://mvnrepository.com/artifact/com.coronaide.lockdown/lockdown-cli)

Lockdown CLI is a command line tool for creating keys and adding credentials to credential store files.

Usage information can be found in lockdown-cli's [README](./lockdown-cli/README.md)

### lockdown-gradle-plugin
[![Maven Central](https://img.shields.io/maven-central/v/com.coronaide.lockdown/lockdown-gradle-plugin.svg)](https://mvnrepository.com/artifact/com.coronaide.lockdown/lockdown-gradle-plugin)

The Lockdown Gradle plug-in is intended for use in Gradle builds, to allow teams to easily expose the ability to add/update credentials in credential storage files without requiring all team members to install the Lockdown CLI.

Usage information can be found in lockdown-gradle-plugin's [README](./lockdown-gradle-plugin/README.md)

## Generating Keys

Lockdown includes a key generator, which is mostly easily used via [lockdown-cli](./lockdown-cli/README.md). All generated keys are of PEM format (PKCS#1), which is currently the only format accepted by the library.

## Handling Key Files

Using public/private keys, there are two main things to keep in mind. Public keys are meant for encrypting data - they can be shared without significant risk to allow anyone to encrypt data the holder of the private key can understand. The private key is the "secret" in this pattern, and should be protected the same way a password would be

## Collaborators

Information for collaborators, including the release process, can be found in the [collaborator documention](./COLLABORATORS.md)
