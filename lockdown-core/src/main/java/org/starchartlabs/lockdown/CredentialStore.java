/*
 * Copyright (C) 2017 The Corona-IDE@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.lockdown;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.Security;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an encrypted store of credential information. Clients are intended to use this representation to operate
 * on stored credentials and access them from within an application which needs access to the stored data
 *
 * @author romeara
 * @since 1.0.0
 */
public class CredentialStore {

    /** Logger reference to output information to the application log files */
    private static final Logger logger = LoggerFactory.getLogger(CredentialStore.class);

    private static final String PKCS_1_PUBLIC_TYPE = "RSA PUBLIC KEY";

    private static final String PKCS_1_PRIVATE_TYPE = "RSA PRIVATE KEY";

    private final Path credentialFile;

    /**
     * Creates a credential store operator for a given file
     *
     * @param credentialFile
     *            Path to a file storing credential information
     */
    private CredentialStore(Path credentialFile) {
        Objects.requireNonNull(credentialFile);

        Security.addProvider(new BouncyCastleProvider());
        this.credentialFile = credentialFile;
    }

    /**
     * Adds or updates a set of credentials accessible via a lookup key. Credentials will be encrypted, lookup key will
     * remain in plain text
     *
     * @param lookupKey
     *            Key to use for later access to the credentials
     * @param username
     *            The user name to encrypt and store
     * @param password
     *            The password to encrypt and store
     * @param publicKey
     *            The public RSA key to use to encrypt the credentials
     * @throws IOException
     *             If an error occurs reading or writing the credential store
     * @throws InvalidCipherTextException
     *             If there is an error encrypting the credentials
     * @since 0.1.0
     */
    public void addOrUpdateCredentials(String lookupKey, String username, char[] password, Path publicKey)
            throws IOException, InvalidCipherTextException {
        Objects.requireNonNull(lookupKey);
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        Objects.requireNonNull(publicKey);

        byte[] publicKeyBytes = readAndDecodeKey(publicKey, PKCS_1_PUBLIC_TYPE);
        String clearText = getCombinedCredentials(username, password);
        byte[] encryptedBytes = encrypt(clearText, publicKeyBytes);

        String entryValue = new String(Base64.encode(encryptedBytes));

        Properties properties = new Properties();

        try (InputStream inputStream = Files.newInputStream(credentialFile, StandardOpenOption.READ)) {
            properties.load(inputStream);
        }

        properties.setProperty(lookupKey, entryValue);

        try (OutputStream outputStream = Files.newOutputStream(credentialFile)) {
            properties.store(outputStream, null);
        }

        logger.info("Credentials added for lookup key {}", lookupKey);
    }

    /**
     * Lists lookup keys already present/available in the credential store. Each lookup key corresponds to a a single
     * set of credentials that may be accessed or manipulated
     *
     * @return A set of the unique lookup keys which each map to a single set of access credentials
     * @throws IOException
     *             If there is an error reading the credential store
     * @since 2.0.0
     */
    public Set<String> getLookupKeys() throws IOException {
        Properties properties = new Properties();

        try (InputStream inputStream = Files.newInputStream(credentialFile, StandardOpenOption.READ)) {
            properties.load(inputStream);
        }

        return properties.keySet().stream()
                .map(Object::toString)
                .collect(Collectors.toSet());

    }

    /**
     * Deletes a set of credentials from the credential store
     *
     * @param lookupKey
     *            The key to remove
     * @throws IOException
     *             If there is an error reading or updating the credential store
     * @since 0.1.0
     */
    public void deleteCredentials(String lookupKey) throws IOException {
        Objects.requireNonNull(lookupKey);

        Properties properties = new Properties();

        try (InputStream inputStream = Files.newInputStream(credentialFile, StandardOpenOption.READ)) {
            properties.load(inputStream);
        }

        properties.remove(lookupKey);

        try (OutputStream outputStream = Files.newOutputStream(credentialFile)) {
            properties.store(outputStream, null);
        }
    }

    /**
     * Accesses a stored set of credentials for use in a client application
     *
     * <p>
     * Clients are encouraged to use this at any time credentials are needed, and to not store credentials via the
     * consumer
     *
     * @param lookupKey
     *            The key designating the particular stored credentials to access
     * @param privateKey
     *            The private RSA key to use to decrypt the stored credentials
     * @param credentialConsumer
     *            The consumer to provide decrypted credentials to for use
     * @throws IOException
     *             If there is an error reading from the credential store or public key
     * @throws InvalidCipherTextException
     *             If there is an error decrypting credentials, such as an incorrect key
     * @since 0.1.0
     */
    public void accessCredentials(String lookupKey, Path privateKey, BiConsumer<String, char[]> credentialConsumer)
            throws IOException, InvalidCipherTextException {
        Objects.requireNonNull(lookupKey);
        Objects.requireNonNull(privateKey);
        Objects.requireNonNull(credentialConsumer);

        Properties properties = new Properties();

        try (InputStream inputStream = Files.newInputStream(credentialFile, StandardOpenOption.READ)) {
            properties.load(inputStream);
        }

        String entryValue = properties.getProperty(lookupKey);

        if (entryValue == null) {
            throw new IllegalArgumentException("No credentials stored with lookupKey " + lookupKey);
        }

        byte[] privateKeyBytes = readAndDecodeKey(privateKey, PKCS_1_PRIVATE_TYPE);
        byte[] encryptedBytes = Base64.decode(entryValue.getBytes());
        String clearText = decrypt(encryptedBytes, privateKeyBytes);

        processExtractedCredentials(clearText, credentialConsumer);
    }

    /**
     * Loads or creates a new credential store at the specified location
     *
     * <p>
     * Meant for creation operations - If a file should be validated as existing, use {@link #load(Path)} instead
     *
     * @param credentialFile
     *            Path to the file storing credentials
     * @return A {@link CredentialStore} instance for reading and manipulating stored credentials
     * @throws IOException
     *             If there is an error creating or loading the file
     * @since 0.1.0
     */
    public static CredentialStore loadOrCreate(Path credentialFile) throws IOException {
        if (!credentialFile.toFile().exists()) {
            boolean existed = credentialFile.toFile().createNewFile();

            if (!existed) {
                logger.warn("File.exists() check did not match upon creation at location {}", credentialFile.toUri());
            }
        }

        return new CredentialStore(credentialFile);
    }

    /**
     * Loads an existing credential store file
     *
     * <p>
     * File must exist - use {@link #loadOrCreate(Path)} if it is possible the file isn't created yet
     *
     * @param credentialFile
     *            Path to the file storing credentials
     * @return A {@link CredentialStore} instance for reading and manipulating stored credentials
     * @throws FileNotFoundException
     *             If the provided path does not represent an existing file
     * @since 0.1.0
     */
    public static CredentialStore load(Path credentialFile) throws FileNotFoundException {
        if (!credentialFile.toFile().exists()) {
            throw new FileNotFoundException("Credential file does not exist at " + credentialFile.toUri().toString());
        }

        return new CredentialStore(credentialFile);
    }

    /**
     * Combines a set of credentials into a single, encyrptable string
     *
     * @param username
     *            The user name to combine
     * @param password
     *            The matching password to combine
     * @return A single string which can be encrypted to store the credentials
     */
    private String getCombinedCredentials(String username, char[] password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        String encodedUsername = new String(Base64.encode(username.getBytes()));
        String encodedPassword = new String(Base64.encode(new String(password).getBytes()));

        return new StringBuilder().append(encodedUsername)
                .append(':')
                .append(encodedPassword)
                .toString();
    }

    /**
     * Extracts credential information from encoded data and provides it to the consumer
     *
     * @param clearText
     *            The clear text information saved with basic encoding
     * @param credentialConsumer
     *            A consumer to provide credential information to
     */
    private void processExtractedCredentials(String clearText, BiConsumer<String, char[]> credentialConsumer) {
        Objects.requireNonNull(clearText);
        Objects.requireNonNull(credentialConsumer);

        String[] elements = clearText.split(":");

        if (elements.length != 2) {
            throw new IllegalArgumentException("Encrypted credentials not of expected form");
        }

        String encodedUsername = elements[0];
        String encodedPassword = elements[1];

        String username = new String(Base64.decode(encodedUsername.getBytes()));
        char[] password = new String(Base64.decode(encodedPassword.getBytes())).toCharArray();

        try {
            credentialConsumer.accept(username, password);
        } finally {
            Arrays.fill(password, '\0');
            username = null;
        }
    }

    /**
     * Encrypts a pain text string into a byte array
     *
     * @param clearText
     *            The string to encrypt
     * @param publicKeyBytes
     *            The public RSA key to use to perform the encryption
     * @return The encrypted data
     * @throws IOException
     *             If there is an error creating the public key operator
     * @throws InvalidCipherTextException
     *             If the encryption attempt fails
     */
    private byte[] encrypt(String clearText, byte[] publicKeyBytes) throws IOException, InvalidCipherTextException {
        Objects.requireNonNull(clearText);
        Objects.requireNonNull(publicKeyBytes);

        AsymmetricKeyParameter publicKey = PublicKeyFactory.createKey(publicKeyBytes);
        AsymmetricBlockCipher cipher = new RSAEngine();
        cipher = new PKCS1Encoding(cipher);
        cipher.init(true, publicKey);

        byte[] messageBytes = clearText.getBytes();
        return cipher.processBlock(messageBytes, 0, messageBytes.length);
    }

    /**
     * Decrypts a byte array into a plain text string
     *
     * @param encrypted
     *            The encrypted bytes to convert
     * @param privateKeyBytes
     *            Bytes forming the private key used to decrypt the encrypted data
     * @return Decrypted data
     * @throws IOException
     *             If there is an error creating the private key operator
     * @throws InvalidCipherTextException
     *             If the decryption attempt fails
     */
    private String decrypt(byte[] encrypted, byte[] privateKeyBytes) throws IOException, InvalidCipherTextException {
        Objects.requireNonNull(encrypted);
        Objects.requireNonNull(privateKeyBytes);

        AsymmetricKeyParameter privateKey = PrivateKeyFactory.createKey(privateKeyBytes);
        AsymmetricBlockCipher cipher = new RSAEngine();
        cipher = new PKCS1Encoding(cipher);
        cipher.init(false, privateKey);

        return new String(cipher.processBlock(encrypted, 0, encrypted.length));
    }

    /**
     * Reads an RSA key from file (public or private) and gets a byte array for use in encryption and decryption
     *
     * @param keyFile
     *            Path representing the file on disk to read
     * @param expectedType
     *            The key type specified in the PEM header/footer
     * @return Byte array for encryption and decryption operations
     * @throws IOException
     *             If there is an error reading the key
     */
    private byte[] readAndDecodeKey(Path keyFile, String expectedType) throws IOException {
        Objects.requireNonNull(keyFile);

        try (PemReader pemReader = new PemReader(Files.newBufferedReader(keyFile))) {
            PemObject pemObject = Optional.ofNullable(pemReader.readPemObject())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Invalid key provided - Only PEM (PKCS1 format) is supported"));
            if (!Objects.equals(expectedType, pemObject.getType())) {
                throw new IllegalArgumentException(
                        "Invalid key provided - Only PEM (PKCS1 format) is supported. (Found header: "
                                + pemObject.getType() + ")");
            }

            return pemObject.getContent();
        }
    }
}
