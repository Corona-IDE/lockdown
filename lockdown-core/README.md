# lockdown-core
[![Maven Central](https://img.shields.io/maven-central/v/com.coronaide.lockdown/lockdown-core.svg)](https://mvnrepository.com/artifact/com.coronaide.lockdown/lockdown-core)

Lockdown Core is where the central key generation and encryption/decryption APIs are defined. This library is intended for use within applications consuming lockdown, and allows access to credential storage files.

## Usage

- **lookupKey:** A unique label for a set of credentials in a store. Determined when credentials are added to the store
- **credentialStorePath:** Path to the credential storage file
- **publicKeyPath:** Path to the public key used to encrypt credentials
- **privateKeyPath:** Path to the private key used to decrypt credentials

### Storing Credentials

```
private void loadCredentials(String lookupKey Path credentialStorePath, Path publicKeyPath){
  CredentialStore store = CredentialStore.loadOrCreate(credentialStorePath);

  String username = getUsername();
  char[] password = getPassword();

  store.addOrUpdateCredentials(lookupKey, username, password, publicKeyPath);
}

private String getUsername(){
   //Read username from the user
}

private char[] getPassword(){
   //Read password from the user
}
```

### Accessing Credentials

```
private void loadCredentials(String lookupKey Path credentialStorePath, Path privateKeyPath){
  CredentialStore store = CredentialStore.loadOrCreate(credentialStorePath);

  store.accessCredentials(lookupKey, this::accessCredentials);
}

private void accessCredentials(String username, char[] password){
  //Use credentials - it is recommended to NOT store them in non-local variables
}
```

## Credential Store Format

The credential store is a simple properties file. The "key" for each property is what is referred to as the "lookupKey" in the APIs. The value of each property is the username and password Base64 encoded and separated by a ':', then encrypted with a public key
