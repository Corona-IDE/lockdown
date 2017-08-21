# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]
- Remove com.coronaide.* classes/APIs which were deprecated in 1.0.0

## [1.0.0]
- Change group ID to org.starchartlabs.lockdown
- Create APIs in new group ID package structure
- Deprecate APIs in old package naming structure

## [0.1.2]
- Made invalid key error message clearer about requirement of PKCS1
- Allow password entry in CLI to be hidden when Console is unavailable

## [0.1.1]
- Added required meta-data toPOM for Maven Central
- Made title of credential login dialog configurable

## [0.1.0]
### Added
- Add APIs for generating RSA public/private key pairs
- Added APIs for adding and accessing encrypted credentials
- Add a command-line tool for key generation and credential addition by clients
- Add a Gradle plug-in for developer addition of credentials without downloading CLI
