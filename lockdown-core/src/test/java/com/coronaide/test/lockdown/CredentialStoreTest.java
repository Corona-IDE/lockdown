/*
 * Copyright (c) Apr 26, 2017 Corona IDE.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package com.coronaide.test.lockdown;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.RuntimeCryptoException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.coronaide.lockdown.CredentialStore;

public class CredentialStoreTest {

    private static final Path TEST_KEY_DIRECTORY = Paths.get("com/coronaide/test/lockdown/core/keys");

    private static final Path TEST_KEY_1_PUBLIC = TEST_KEY_DIRECTORY.resolve("test_rsa_1.pub");

    private static final Path TEST_KEY_1_PRIVATE = TEST_KEY_DIRECTORY.resolve("test_rsa_1");

    private static final Path TEST_KEY_2_PUBLIC = TEST_KEY_DIRECTORY.resolve("test_rsa_2.pub");

    private static final Path TEST_KEY_2_PRIVATE = TEST_KEY_DIRECTORY.resolve("test_rsa_2");

    private Path publicKey1;

    private Path privateKey1;

    private Path publicKey2;

    private Path privateKey2;

    @BeforeClass
    public void createKeys() throws Exception {
        Path tempDirectory = Files.createTempDirectory("credentital-store-test");
        tempDirectory.toFile().deleteOnExit();

        publicKey1 = tempDirectory.resolve("test1_rsa.pub");
        privateKey1 = tempDirectory.resolve("test1_rsa");
        publicKey2 = tempDirectory.resolve("test2_rsa.pub");
        privateKey2 = tempDirectory.resolve("test2_rsa");

        // Copy from resource directory to temporary location where files are usable (resources may be within a jar
        // during run)
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(TEST_KEY_1_PUBLIC.toString())) {
            Files.copy(in, publicKey1);
        }

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(TEST_KEY_1_PRIVATE.toString())) {
            Files.copy(in, privateKey1);
        }

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(TEST_KEY_2_PUBLIC.toString())) {
            Files.copy(in, publicKey2);
        }

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(TEST_KEY_2_PRIVATE.toString())) {
            Files.copy(in, privateKey2);
        }

        publicKey1.toFile().deleteOnExit();
        privateKey1.toFile().deleteOnExit();
        publicKey2.toFile().deleteOnExit();
        privateKey2.toFile().deleteOnExit();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void loadOrCreateNullLocation() throws Exception {
        CredentialStore.loadOrCreate(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void loadNullLocation() throws Exception {
        CredentialStore.load(null);
    }

    @Test
    public void loadOrCreateNewFile() throws Exception {
        Path credFileDirectory = Files.createTempDirectory("creds");
        Path targetFile = credFileDirectory.resolve("credfile.store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);

        Assert.assertNotNull(store);
        Assert.assertTrue(targetFile.toFile().exists());
    }

    @Test(expectedExceptions = FileNotFoundException.class)
    public void loadFileDoesNotExist() throws Exception {
        Path credFileDirectory = Files.createTempDirectory("creds");
        Path targetFile = credFileDirectory.resolve("credfile.store");

        CredentialStore.load(targetFile);
    }

    @Test
    public void load() throws Exception {
        Path targetFile = Files.createTempFile("cred", "store");
        targetFile.toFile().deleteOnExit();
        Assert.assertTrue(targetFile.toFile().exists(), "Test misconfigured - ensure load exists");

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);

        Assert.assertNotNull(store);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void addOrUpdateCredentialsNullLookupKey() throws Exception {
        Path targetFile = Files.createTempFile("cred", "store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);
        store.addOrUpdateCredentials(null, "user1", "pass1".toCharArray(), publicKey1);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void addOrUpdateCredentialsNullUsername() throws Exception {
        Path targetFile = Files.createTempFile("cred", "store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);
        store.addOrUpdateCredentials("KEY1", null, "pass1".toCharArray(), publicKey1);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void addOrUpdateCredentialsNullPassword() throws Exception {
        Path targetFile = Files.createTempFile("cred", "store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);
        store.addOrUpdateCredentials("KEY1", "user1", null, publicKey1);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void addOrUpdateCredentialsNullPublicKey() throws Exception {
        Path targetFile = Files.createTempFile("cred", "store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);
        store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void deleteCredentialsNullLookupKey() throws Exception {
        Path credFileDirectory = Files.createTempDirectory("creds");
        Path targetFile = credFileDirectory.resolve("credfile.store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);

        store.deleteCredentials(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void deleteCredentials() throws Exception {
        Path credFileDirectory = Files.createTempDirectory("creds");
        Path targetFile = credFileDirectory.resolve("credfile.store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);

        try {
            Assert.assertNotNull(store);
            Assert.assertTrue(targetFile.toFile().exists());

            // Add encrypted key(s)
            store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), publicKey1);
            store.addOrUpdateCredentials("KEY2", "user2", "pass2".toCharArray(), publicKey1);

            // Remove Key
            store.deleteCredentials("KEY1");

            // Attempt to decrypt
            store.accessCredentials("KEY2", privateKey1,
                    (a, b) -> validateCredentials(a, b, "user2", "pass2".toCharArray()));
        } catch (Exception e) {
            Assert.fail("Unexpected error", e);
        }

        store.accessCredentials("KEY1", privateKey1,
                (a, b) -> validateCredentials(a, b, "user1", "pass1".toCharArray()));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void accessCredentialsNullLookupKey() throws Exception {
        Path targetFile = Files.createTempFile("cred", "store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);

        Assert.assertNotNull(store);
        Assert.assertTrue(targetFile.toFile().exists());

        // Setup a valid key to test against
        store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), publicKey1);
        store.accessCredentials(null, privateKey1, (a, b) -> Assert.fail("Load was attempted"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void accessCredentialsNullPrivateKey() throws Exception {
        Path targetFile = Files.createTempFile("cred", "store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);

        Assert.assertNotNull(store);
        Assert.assertTrue(targetFile.toFile().exists());

        // Setup a valid key to test against
        store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), publicKey1);
        store.accessCredentials("KEY1", null, (a, b) -> Assert.fail("Load was attempted"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void accessCredentialsNullConsumer() throws Exception {
        Path targetFile = Files.createTempFile("cred", "store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);

        Assert.assertNotNull(store);
        Assert.assertTrue(targetFile.toFile().exists());

        // Setup a valid key to test against
        store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), publicKey1);
        store.accessCredentials("KEY1", privateKey1, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void accessCredentialsNoEntryForKey() throws Exception {
        Path targetFile = Files.createTempFile("cred", "store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);

        Assert.assertNotNull(store);
        Assert.assertTrue(targetFile.toFile().exists());

        // Setup a valid key to test against
        store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), publicKey1);
        store.accessCredentials("KEY2", privateKey1, (a, b) -> Assert.fail("Load was attempted"));
    }

    @Test(expectedExceptions = { CryptoException.class, RuntimeCryptoException.class })
    public void accessCredentialsInvalidKey() throws Exception {
        Path targetFile = Files.createTempFile("cred", "store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);

        Assert.assertNotNull(store);
        Assert.assertTrue(targetFile.toFile().exists());

        // Setup a valid key to test against
        store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), publicKey1);
        store.accessCredentials("KEY1", privateKey2, (a, b) -> Assert.fail("Load was attempted"));
    }

    @Test
    public void loadAndCreateEncryptAndDecrypt() throws Exception {
        Path credFileDirectory = Files.createTempDirectory("creds");
        Path targetFile = credFileDirectory.resolve("credfile.store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);

        Assert.assertNotNull(store);
        Assert.assertTrue(targetFile.toFile().exists());

        // Add encrypted key(s)
        store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), publicKey1);
        store.addOrUpdateCredentials("KEY2", "user2", "pass2".toCharArray(), publicKey1);

        // Attempt to decrypt
        store.accessCredentials("KEY1", privateKey1,
                (a, b) -> validateCredentials(a, b, "user1", "pass1".toCharArray()));
        store.accessCredentials("KEY2", privateKey1,
                (a, b) -> validateCredentials(a, b, "user2", "pass2".toCharArray()));
    }

    @Test
    public void loadEncryptAndDecrypt() throws Exception {
        Path targetFile = Files.createTempFile("cred", "store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);

        Assert.assertNotNull(store);
        Assert.assertTrue(targetFile.toFile().exists());

        // Add encrypted key(s)
        store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), publicKey1);
        store.addOrUpdateCredentials("KEY2", "user2", "pass2".toCharArray(), publicKey1);

        // Attempt to decrypt
        store.accessCredentials("KEY1", privateKey1,
                (a, b) -> validateCredentials(a, b, "user1", "pass1".toCharArray()));
        store.accessCredentials("KEY2", privateKey1,
                (a, b) -> validateCredentials(a, b, "user2", "pass2".toCharArray()));
    }

    private void validateCredentials(String actualUsername, char[] actualPassword, String expectedUsername,
            char[] expectedPassword) {
        Assert.assertEquals(actualUsername, expectedUsername);
        Assert.assertEquals(new String(actualPassword), new String(expectedPassword));
    }

}
