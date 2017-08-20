/*
 * Copyright (C) 2017 The Corona-IDE@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.coronaide.test.lockdown.cli;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.coronaide.lockdown.CredentialStore;
import com.coronaide.lockdown.cli.LockdownCommandLine;

@SuppressWarnings("deprecation")
public class AddCredentialsTest {

    /** Logger reference to output information to the application log files */
    private static final Logger logger = LoggerFactory.getLogger(AddCredentialsTest.class);

    private static final Path TEST_KEY_DIRECTORY = Paths.get("org/starchartlabs/test/lockdown/cli/keys");

    private static final Path TEST_KEY_1_PUBLIC = TEST_KEY_DIRECTORY.resolve("test_rsa_1.pub");

    private static final Path TEST_KEY_1_PRIVATE = TEST_KEY_DIRECTORY.resolve("test_rsa_1");

    private static final String COMMAND = "addkey";

    private static final String LOOKUP_KEY = "ADDTEST";

    private Path publicKey1;

    private Path privateKey1;

    @BeforeClass
    public void createKeys() throws Exception {
        Path tempDirectory = Files.createTempDirectory("credentital-store-test");
        tempDirectory.toFile().deleteOnExit();

        publicKey1 = tempDirectory.resolve("test_rsa.pub");
        privateKey1 = tempDirectory.resolve("test_rsa");

        // Copy from resource directory to temporary location where files are usable (resources may be within a jar
        // during run)
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(TEST_KEY_1_PUBLIC.toString())) {
            Files.copy(in, publicKey1);
        }

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(TEST_KEY_1_PRIVATE.toString())) {
            Files.copy(in, privateKey1);
        }

        publicKey1.toFile().deleteOnExit();
        privateKey1.toFile().deleteOnExit();
    }

    @Test
    public void addCredentialsNoLookupKey() throws Exception {
        Path outputDirectory = Files.createTempDirectory("lockdown.cli.test");
        outputDirectory.toFile().deleteOnExit();

        Path testStore = outputDirectory.resolve("test.properties");
        testStore.toFile().deleteOnExit();

        logger.debug("Test outputing to: {}", outputDirectory.toAbsolutePath().toString());

        String[] args = new String[] { COMMAND, "-o", testStore.toAbsolutePath().toString(), "-k",
                publicKey1.toAbsolutePath().toString() };

        // Simulate user input
        runWithSimulatedInput(args, "username", "password", "password");

        // Check no credentials were stored - usage printed
        Assert.assertFalse(Files.exists(testStore));
    }

    @Test
    public void addCredentialsNoStoreFile() throws Exception {
        Path outputDirectory = Files.createTempDirectory("lockdown.cli.test");
        outputDirectory.toFile().deleteOnExit();

        Path testStore = outputDirectory.resolve("test.properties");
        testStore.toFile().deleteOnExit();

        logger.debug("Test outputing to: {}", outputDirectory.toAbsolutePath().toString());

        String[] args = new String[] { COMMAND, LOOKUP_KEY, "-k", publicKey1.toAbsolutePath().toString() };

        // Simulate user input
        runWithSimulatedInput(args, "username", "password", "password");

        // Check no credentials were stored - usage printed
        Assert.assertFalse(Files.exists(testStore));
    }

    @Test
    public void addCredentialsNoKey() throws Exception {
        Path outputDirectory = Files.createTempDirectory("lockdown.cli.test");
        outputDirectory.toFile().deleteOnExit();

        Path testStore = outputDirectory.resolve("test.properties");
        testStore.toFile().deleteOnExit();

        logger.debug("Test outputing to: {}", outputDirectory.toAbsolutePath().toString());

        String[] args = new String[] { COMMAND, LOOKUP_KEY, "-o", testStore.toAbsolutePath().toString() };

        // Simulate user input
        runWithSimulatedInput(args, "username", "password", "password");

        // Check no credentials were stored - usage printed
        Assert.assertFalse(Files.exists(testStore));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void addCredentialsInvalidKey() throws Exception {
        Path outputDirectory = Files.createTempDirectory("lockdown.cli.test");
        outputDirectory.toFile().deleteOnExit();

        Path testStore = outputDirectory.resolve("test.properties");
        testStore.toFile().deleteOnExit();

        logger.debug("Test outputing to: {}", outputDirectory.toAbsolutePath().toString());

        String[] args = new String[] { COMMAND, LOOKUP_KEY, "-o", testStore.toAbsolutePath().toString(), "-k",
                privateKey1.toAbsolutePath().toString() };

        // Simulate user input
        runWithSimulatedInput(args, "username", "password", "password");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void addCredentialsEmptyPassword() throws Exception {
        Path outputDirectory = Files.createTempDirectory("lockdown.cli.test");
        outputDirectory.toFile().deleteOnExit();

        Path testStore = outputDirectory.resolve("test.properties");
        testStore.toFile().deleteOnExit();

        logger.debug("Test outputing to: {}", outputDirectory.toAbsolutePath().toString());

        String[] args = new String[] { COMMAND, LOOKUP_KEY, "-o", testStore.toAbsolutePath().toString(), "-k",
                publicKey1.toAbsolutePath().toString() };

        // Simulate user input
        runWithSimulatedInput(args, "username", "", "password");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void addCredentialsConfirmDoesntMatch() throws Exception {
        Path outputDirectory = Files.createTempDirectory("lockdown.cli.test");
        outputDirectory.toFile().deleteOnExit();

        Path testStore = outputDirectory.resolve("test.properties");
        testStore.toFile().deleteOnExit();

        logger.debug("Test outputing to: {}", outputDirectory.toAbsolutePath().toString());

        String[] args = new String[] { COMMAND, LOOKUP_KEY, "-o", testStore.toAbsolutePath().toString(), "-k",
                publicKey1.toAbsolutePath().toString() };

        // Simulate user input
        runWithSimulatedInput(args, "username", "password", "nope");
    }

    @Test
    public void addCredentials() throws Exception {
        Path outputDirectory = Files.createTempDirectory("lockdown.cli.test");
        outputDirectory.toFile().deleteOnExit();

        Path testStore = outputDirectory.resolve("test.properties");
        testStore.toFile().deleteOnExit();

        logger.debug("Test outputing to: {}", outputDirectory.toAbsolutePath().toString());

        String[] args = new String[] { COMMAND, LOOKUP_KEY, "-o", testStore.toAbsolutePath().toString(), "-k",
                publicKey1.toAbsolutePath().toString() };

        // Simulate user input
        runWithSimulatedInput(args, "username", "password", "password");

        // Check credentials were stored correctly
        CredentialStore store = CredentialStore.loadOrCreate(testStore);

        store.accessCredentials(LOOKUP_KEY, privateKey1,
                (a, b) -> validateCredentials(a, b, "username", "password".toCharArray()));
    }

    private void runWithSimulatedInput(String[] args, String username, String password, String confirmPassword) {
        StringBuilder builder = new StringBuilder();
        builder.append(username).append('\n');
        builder.append(password).append('\n');
        builder.append(confirmPassword).append('\n');

        InputStream stdin = System.in;

        try {
            System.setIn(new ByteArrayInputStream(builder.toString().getBytes()));

            LockdownCommandLine.main(args);
        } finally {
            System.setIn(stdin);
        }
    }

    private void validateCredentials(String actualUsername, char[] actualPassword, String expectedUsername,
            char[] expectedPassword) {
        Assert.assertEquals(actualUsername, expectedUsername);
        Assert.assertEquals(new String(actualPassword), new String(expectedPassword));
    }

}
