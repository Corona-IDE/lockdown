/*
 * Copyright (C) 2017 The Corona-IDE@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.coronaide.lockdown.cli.command;

import java.io.Closeable;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coronaide.lockdown.CredentialStore;

/**
 * Command line handler which allows adding of credentials to credential stores
 *
 * @author romeara
 * @since 0.1.0
 */
public class AddCredentialsCommand implements Runnable {

    /** Logger reference to output information to the application log files */
    private static final Logger logger = LoggerFactory.getLogger(AddCredentialsCommand.class);

    @Argument(index = 0, required = true,
            usage = "Key used to reference the set of credentials in the credential store")
    private String lookupKey;

    @Option(name = "-o", aliases = { "--output" }, required = true,
            usage = "Specifies the credential store to create/add to. Required")
    private File credentialStore;

    @Option(name = "-k", aliases = { "--key" }, required = true,
            usage = "Specifies the public key to use to encrypt credentials. Required")
    private File publicKey;

    @Override
    public void run() {
        try (InputHandler inputHandler = new InputHandler()) {
            CredentialStore store = CredentialStore.loadOrCreate(credentialStore.toPath());

            String username = inputHandler.readLine("Username: ");
            char[] password = inputHandler.readPassword("Password: ");

            if (password.length == 0) {
                throw new IllegalArgumentException("Blank password entered");
            }

            char[] confirmPassword = inputHandler.readPassword("Confirm Password: ");

            if (!Arrays.equals(password, confirmPassword)) {
                throw new IllegalArgumentException("Passwords did not match");
            }

            // Don't need confirm anymore, null it
            Arrays.fill(confirmPassword, '\0');

            try {
                store.addOrUpdateCredentials(lookupKey, username, password, publicKey.toPath());
            } finally {
                Arrays.fill(password, '\0');
            }
        } catch (IOException e) {
            logger.error("Error writing to credential store", e);
        } catch (InvalidCipherTextException e) {
            logger.error("Error encrypting credentials", e);
        }
    }

    /**
     * Represents an abstraction of the input mechanism for getting credentials to add during the command's run
     *
     * <p>
     * Tries to use System.console(), as is provides a more secure password entry mechanism. Falls back to Scanner with
     * an erasing thread if the console is unavailable
     *
     * @author romeara
     * @since 0.1.0
     */
    private static class InputHandler implements Closeable {

        private final Optional<Console> console;

        private final Scanner scanner;

        public InputHandler() {
            console = Optional.ofNullable(System.console());
            scanner = new Scanner(System.in);
        }

        public String readLine(String prompt) {
            Objects.requireNonNull(prompt);

            return console.map(c -> c.readLine(prompt))
                    .orElseGet(() -> readFromScanner(prompt));
        }

        public char[] readPassword(String prompt) throws IOException {
            Objects.requireNonNull(prompt);

            char[] result = null;

            if (console.isPresent()) {
                result = console.map(c -> c.readPassword(prompt)).orElse(null);
            } else {
                try (ConsoleEraser eraser = new ConsoleEraser()) {
                    result = readFromScanner(prompt).toCharArray();
                }
            }

            return result;
        }

        private String readFromScanner(String prompt) {
            Objects.requireNonNull(prompt);

            System.out.print(prompt);
            String result = scanner.nextLine();
            System.out.print('\n');

            return result;
        }

        @Override
        public void close() throws IOException {
            scanner.close();
        }

    }

    /**
     * Thread used in conjunction with Scanner for entering passwords without printing back to the console
     *
     * @author romeara
     * @since 0.1.2
     */
    private static class ConsoleEraser extends Thread implements Closeable {

        private boolean running = true;

        public ConsoleEraser() {
            start();
        }

        @Override
        public void run() {
            while (running) {
                System.out.print("\b ");

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        @Override
        public synchronized void close() throws IOException {
            running = false;
        }
    }

}
