/*
 * Copyright (C) 2017 The Corona-IDE@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.test.lockdown.cli;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.starchartlabs.lockdown.CredentialStore;
import org.starchartlabs.lockdown.cli.LockdownCommandLine;
import org.starchartlabs.lockdown.cli.command.ListLookupKeysCommand;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

public class ListLookupKeysTest {

    /** Logger reference to output information to the application log files */
    private static final Logger logger = LoggerFactory.getLogger(ListLookupKeysTest.class);

    private static final Path TEST_KEY_DIRECTORY = Paths.get("org/starchartlabs/test/lockdown/cli/keys");

    private static final Path TEST_KEY_1_PUBLIC = TEST_KEY_DIRECTORY.resolve("test_rsa_1.pub");

    private static final Path TEST_KEY_1_PRIVATE = TEST_KEY_DIRECTORY.resolve("test_rsa_1");

    private static final String COMMAND = "list";

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

    @AfterMethod
    public void clearLoggers() {
        TestLoggerFactory.clear();
    }

    @Test
    public void listLookupKeysNoLookupKey() throws Exception {
        TestLogger testLogger = TestLoggerFactory.getTestLogger(ListLookupKeysCommand.class);

        Path outputDirectory = Files.createTempDirectory("lockdown.cli.test");
        outputDirectory.toFile().deleteOnExit();

        Path testStore = outputDirectory.resolve("test-no.properties");
        testStore.toFile().deleteOnExit();

        logger.debug("Test outputing to: {}", outputDirectory.toAbsolutePath().toString());

        String[] args = new String[] { COMMAND };

        // Simulate user input
        LockdownCommandLine.main(args);

        // Check no credentials were stored - usage printed
        List<String> events = testLogger.getLoggingEvents().stream()
                .filter(event -> Level.INFO.equals(event.getLevel()))
                .map(LoggingEvent::getMessage)
                .collect(Collectors.toList());

        Assert.assertFalse(events.contains("Found {} lookup keys in {}:"));
    }

    @Test
    public void listLookupKeys() throws Exception {
        TestLogger testLogger = TestLoggerFactory.getTestLogger(ListLookupKeysCommand.class);

        Path outputDirectory = Files.createTempDirectory("lockdown.cli.test");
        outputDirectory.toFile().deleteOnExit();

        Path testStore = outputDirectory.resolve("test.properties");
        testStore.toFile().deleteOnExit();

        logger.debug("Test outputing to: {}", outputDirectory.toAbsolutePath().toString());

        CredentialStore store = CredentialStore.loadOrCreate(testStore);

        Assert.assertNotNull(store);
        Assert.assertTrue(testStore.toFile().exists());

        // Add encrypted key(s)
        store.addOrUpdateCredentials("KEY1", "user1", "pass1".toCharArray(), publicKey1);
        store.addOrUpdateCredentials("KEY2", "user2", "pass2".toCharArray(), publicKey1);

        String[] args = new String[] { COMMAND, testStore.toAbsolutePath().toString() };

        // Simulate user input
        LockdownCommandLine.main(args);

        // Check no credentials were stored - usage printed
        List<String> messages = testLogger.getLoggingEvents().stream()
                .filter(event -> Level.INFO.equals(event.getLevel()))
                .map(event -> MessageFormatter.arrayFormat(event.getMessage(), event.getArguments().toArray()).getMessage())
                .collect(Collectors.toList());

        Assert.assertTrue(messages.contains("Found 2 lookup keys in test.properties:"));
        Assert.assertTrue(messages.contains("    KEY1"));
        Assert.assertTrue(messages.contains("    KEY2"));
    }

}
