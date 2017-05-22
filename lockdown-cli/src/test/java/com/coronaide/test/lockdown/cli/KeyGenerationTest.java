/*
 * Copyright (C) 2017 The Corona-IDE@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.coronaide.test.lockdown.cli;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.coronaide.lockdown.CredentialStore;
import com.coronaide.lockdown.cli.LockdownCommandLine;

public class KeyGenerationTest {

    private static final String GENERATE_CMD = "generate";

    private static final String DEFAULT_PUBLIC_NAME = "lockdown_rsa.pub";

    private static final String DEFAULT_PRIVATE_NAME = "lockdown_rsa";

    /** Logger reference to output information to the application log files */
    private static final Logger logger = LoggerFactory.getLogger(KeyGenerationTest.class);

    @Test(groups = "KEY_GENERATION_TEST")
    public void generateNoArguments() throws Exception {
        Path outputDirectory = Files.createTempDirectory("lockdown.cli.test");
        outputDirectory.toFile().deleteOnExit();

        logger.debug("Test outputing to: {}", outputDirectory.toAbsolutePath().toString());

        String[] args = new String[] { GENERATE_CMD };

        LockdownCommandLine.main(args);

        // Should not be generated at all
        Path publicKey = outputDirectory.resolve(DEFAULT_PUBLIC_NAME);
        Path privateKey = outputDirectory.resolve(DEFAULT_PRIVATE_NAME);

        Assert.assertFalse(publicKey.toFile().exists());
        Assert.assertFalse(privateKey.toFile().exists());
    }

    @Test(expectedExceptions = NullPointerException.class, groups = "KEY_GENERATION_TEST")
    public void generateKeysExistNoForce() throws Exception {
        Path outputDirectory = Files.createTempDirectory("lockdown.cli.test");
        outputDirectory.toFile().deleteOnExit();

        Path publicKey = outputDirectory.resolve(DEFAULT_PUBLIC_NAME);
        Path privateKey = outputDirectory.resolve(DEFAULT_PRIVATE_NAME);

        Files.createFile(publicKey);
        Files.createFile(privateKey);

        logger.debug("Test outputing to: {}", outputDirectory.toAbsolutePath().toString());

        String[] args = new String[] { GENERATE_CMD, "-o", outputDirectory.toAbsolutePath().toString() };

        LockdownCommandLine.main(args);

        // Should not be generated over blank files, which will fail the encrypt step
        Assert.assertTrue(Files.exists(publicKey));
        Assert.assertTrue(Files.exists(privateKey));

        Path testStore = outputDirectory.resolve("test.store");

        publicKey.toFile().deleteOnExit();
        privateKey.toFile().deleteOnExit();
        testStore.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(testStore);

        store.addOrUpdateCredentials("TEST", "user", "pass".toCharArray(), publicKey);
    }

    @Test(groups = "KEY_GENERATION_TEST")
    public void generateMinimumArgsShortForm() throws Exception {
        Path outputDirectory = Files.createTempDirectory("lockdown.cli.test");
        outputDirectory.toFile().deleteOnExit();

        logger.debug("Test outputing to: {}", outputDirectory.toAbsolutePath().toString());

        String[] args = new String[] { GENERATE_CMD, "-o", outputDirectory.toAbsolutePath().toString() };

        LockdownCommandLine.main(args);

        // Should be generated in the provided location with default names. Load and attempt use
        Path publicKey = outputDirectory.resolve(DEFAULT_PUBLIC_NAME);
        Path privateKey = outputDirectory.resolve(DEFAULT_PRIVATE_NAME);

        Assert.assertTrue(Files.exists(publicKey));
        Assert.assertTrue(Files.exists(privateKey));

        Path testStore = outputDirectory.resolve("test.store");

        publicKey.toFile().deleteOnExit();
        privateKey.toFile().deleteOnExit();
        testStore.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(testStore);

        store.addOrUpdateCredentials("TEST", "user", "pass".toCharArray(), publicKey);
        store.accessCredentials("TEST", privateKey, (a, b) -> validateCredentials(a, b, "user", "pass".toCharArray()));
    }

    @Test(groups = "KEY_GENERATION_TEST")
    public void generateFileNameShortForm() throws Exception {
        String baseName = "test_rsa";

        Path outputDirectory = Files.createTempDirectory("lockdown.cli.test");
        outputDirectory.toFile().deleteOnExit();

        logger.debug("Test outputing to: {}", outputDirectory.toAbsolutePath().toString());

        String[] args = new String[] { GENERATE_CMD, "-o", outputDirectory.toAbsolutePath().toString(), "-n",
                baseName };

        LockdownCommandLine.main(args);

        // Should be generated in the provided location with default names. Load and attempt use
        Path publicKey = outputDirectory.resolve(baseName + ".pub");
        Path privateKey = outputDirectory.resolve(baseName);

        Assert.assertTrue(Files.exists(publicKey));
        Assert.assertTrue(Files.exists(privateKey));

        Path testStore = outputDirectory.resolve("test.store");

        publicKey.toFile().deleteOnExit();
        privateKey.toFile().deleteOnExit();
        testStore.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(testStore);

        store.addOrUpdateCredentials("TEST", "user", "pass".toCharArray(), publicKey);
        store.accessCredentials("TEST", privateKey, (a, b) -> validateCredentials(a, b, "user", "pass".toCharArray()));
    }

    @Test(groups = "KEY_GENERATION_TEST")
    public void generateForceOverwriteShortForm() throws Exception {
        Path outputDirectory = Files.createTempDirectory("lockdown.cli.test");
        outputDirectory.toFile().deleteOnExit();

        Path publicKey = outputDirectory.resolve(DEFAULT_PUBLIC_NAME);
        Path privateKey = outputDirectory.resolve(DEFAULT_PRIVATE_NAME);

        Files.createFile(publicKey);
        Files.createFile(privateKey);

        logger.debug("Test outputing to: {}", outputDirectory.toAbsolutePath().toString());

        String[] args = new String[] { GENERATE_CMD, "-o", outputDirectory.toAbsolutePath().toString(), "-f" };

        LockdownCommandLine.main(args);

        // Should be generated in the provided location with default names. Load and attempt use
        Assert.assertTrue(Files.exists(publicKey));
        Assert.assertTrue(Files.exists(privateKey));

        Path testStore = outputDirectory.resolve("test.store");

        publicKey.toFile().deleteOnExit();
        privateKey.toFile().deleteOnExit();
        testStore.toFile().deleteOnExit();

        CredentialStore store = CredentialStore.loadOrCreate(testStore);

        store.addOrUpdateCredentials("TEST", "user", "pass".toCharArray(), publicKey);
        store.accessCredentials("TEST", privateKey, (a, b) -> validateCredentials(a, b, "user", "pass".toCharArray()));
    }

    private void validateCredentials(String actualUsername, char[] actualPassword, String expectedUsername,
            char[] expectedPassword) {
        Assert.assertEquals(actualUsername, expectedUsername);
        Assert.assertEquals(new String(actualPassword), new String(expectedPassword));
    }

}
