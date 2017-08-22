# lockdown-cli
[![Maven Central](https://img.shields.io/maven-central/v/org.starchartlabs.lockdown/lockdown-cli.svg)](https://mvnrepository.com/artifact/org.starchartlabs.lockdown/lockdown-cli)

Lockdown CLI is a command line tool for creating keys and adding credentials to credential store files.

## Usage

Download the Lockdown CLI capsule from the [GitHub Releases page](https://github.com/Corona-IDE/lockdown/releases). Entering no arguments will display usage information

```
java -jar lockdown-cli-<version>-capsule.jar (generate|addkey) [options] arguments....
```

### generate

`generate -o <output directory> [-n <name>] [-f]`

The generate command creates a public/private key pair for encryption and decryption of credentials

- `-o <output directory>`
    - Specifies the directory to output keys to. Required
- `-n <name>`
    - Specifies the base name for keys. Default is 'lockdown_rsa', resulting in a public key 'lockdown_rsa.pub', and a private key 'lockdown_rsa'
- `-f`
    - Overwrite any existing keys at the output locations. Default is to halt if keys are present

### addkey

`addkey <lookupKey> -o <credential store> -k <public key>`

- `<lookupKey>`
    - Key used to reference the set of credentials in the credential store
- `-o <credential store>`
    - Specifies the credential store to create/add to. Required
- `-k <public key>`
    - Specifies the public key to use to encrypt credentials. Required

**Note:** When adding keys, the password will not be displayed as it is typed for security reasons (similar to behavior when logging in via ssh)
