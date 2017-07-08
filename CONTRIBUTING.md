# Contributing

We welcome any contributions! If you wish to contribute:

- Fork the `lockdown` repository
- Clone your fork to your development machine
- Run `./gradlew clean build` to confirm you are starting from a workign Setup
 - Please report any issues with this build step in GitHub project's issues
- Create a branch for your work
- Setup your deveopment environment (see below)
- Make changes
- Run `./gradlew clean build` to test your changes locally
- Push your branch to your Fork
- Make a Pull Request

## Development Environment Setup

Currently, Eclipse is the supported IDE for development of Lockdown. It is recommended to create an isolated workspace for StarChart Labs projects. You should also import the standard StarChart Labs formatting and save settings from the [eclipse-configuration repository](https://github.com/StarChart-Labs/eclipse-configuration)

## General Standards

In general, pull requests should:
- Be small and focused on a single improvement/bug
- Include tests for changed behavior/new features
- Match the formatting of the existing code
- Have documentation for added methods/classes and appropriate in-line comments
- Have additions to the CHANGE_LOG.md file recording changed behavior
