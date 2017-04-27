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
import java.nio.file.Files;
import java.nio.file.Path;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.coronaide.lockdown.CredentialStore;
import com.coronaide.lockdown.KeyGenerator;

public class CredentialStoreTest {

    private Path publicKey;

    private Path privateKey;

    @BeforeClass
    public void createKeys() throws Exception {
        publicKey = Files.createTempFile("key", "public");
        privateKey = Files.createTempFile("key", "private");

        publicKey.toFile().deleteOnExit();
        privateKey.toFile().deleteOnExit();

        KeyGenerator generator = new KeyGenerator();
        generator.createKeyPair(publicKey, privateKey);
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
        store.addOrUpdateCredentials(null, "user1", "pass1".toCharArray(), publicKey);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void addOrUpdateCredentialsNullUsername() throws Exception {
        Path targetFile = Files.createTempFile("cred", "store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);
        store.addOrUpdateCredentials("KEY1", null, "pass1".toCharArray(), publicKey);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void addOrUpdateCredentialsNullPassword() throws Exception {
        Path targetFile = Files.createTempFile("cred", "store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);
        store.addOrUpdateCredentials("KEY1", "user1", null, publicKey);
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
            store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), publicKey);
            store.addOrUpdateCredentials("KEY2", "user2", "pass2".toCharArray(), publicKey);

            // Remove Key
            store.deleteCredentials("KEY1");

            // Attempt to decrypt
            store.accessCredentials("KEY2", privateKey,
                    (a, b) -> validateCredentials(a, b, "user2", "pass2".toCharArray()));
        } catch (Exception e) {
            Assert.fail("Unexpected error", e);
        }

        store.accessCredentials("KEY1", privateKey,
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
        store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), publicKey);
        store.accessCredentials(null, privateKey, (a, b) -> Assert.fail("Load was attempted"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void accessCredentialsNullPrivateKey() throws Exception {
        Path targetFile = Files.createTempFile("cred", "store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);

        Assert.assertNotNull(store);
        Assert.assertTrue(targetFile.toFile().exists());

        // Setup a valid key to test against
        store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), publicKey);
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
        store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), publicKey);
        store.accessCredentials("KEY1", privateKey, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void accessCredentialsNoEntryForKey() throws Exception {
        Path targetFile = Files.createTempFile("cred", "store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);

        Assert.assertNotNull(store);
        Assert.assertTrue(targetFile.toFile().exists());

        // Setup a valid key to test against
        store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), publicKey);
        store.accessCredentials("KEY2", privateKey, (a, b) -> Assert.fail("Load was attempted"));
    }

    @Test(expectedExceptions = InvalidCipherTextException.class)
    public void accessCredentialsInvalidKey() throws Exception {
        Path publicKey2 = Files.createTempFile("key2", "public");
        Path privateKey2 = Files.createTempFile("key2", "private");

        publicKey2.toFile().deleteOnExit();
        privateKey2.toFile().deleteOnExit();

        KeyGenerator generator = new KeyGenerator();
        generator.createKeyPair(publicKey2, privateKey2);

        Path targetFile = Files.createTempFile("cred", "store");
        targetFile.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(targetFile);

        Assert.assertNotNull(store);
        Assert.assertTrue(targetFile.toFile().exists());

        // Setup a valid key to test against
        store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), publicKey);
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
        store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), publicKey);
        store.addOrUpdateCredentials("KEY2", "user2", "pass2".toCharArray(), publicKey);

        // Attempt to decrypt
        store.accessCredentials("KEY1", privateKey,
                (a, b) -> validateCredentials(a, b, "user1", "pass1".toCharArray()));
        store.accessCredentials("KEY2", privateKey,
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
        store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), publicKey);
        store.addOrUpdateCredentials("KEY2", "user2", "pass2".toCharArray(), publicKey);

        // Attempt to decrypt
        store.accessCredentials("KEY1", privateKey,
                (a, b) -> validateCredentials(a, b, "user1", "pass1".toCharArray()));
        store.accessCredentials("KEY2", privateKey,
                (a, b) -> validateCredentials(a, b, "user2", "pass2".toCharArray()));
    }

    private void validateCredentials(String actualUsername, char[] actualPassword, String expectedUsername,
            char[] expectedPassword) {
        Assert.assertEquals(actualUsername, expectedUsername);
        Assert.assertEquals(new String(actualPassword), new String(expectedPassword));
    }

}
