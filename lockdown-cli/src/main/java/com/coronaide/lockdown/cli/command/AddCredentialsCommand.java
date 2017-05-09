/*
 * Copyright (c) May 2, 2017 Corona IDE.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    "romeara" - initial API and implementation and/or initial documentation
 */
package com.coronaide.lockdown.cli.command;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
        try {
            CredentialStore store = CredentialStore.loadOrCreate(credentialStore.toPath());
            Console console = System.console();

            String username = console.readLine("Username: ");
            char[] password = console.readPassword("Password: ");

            if (password.length == 0) {
                throw new IllegalArgumentException("Blank password entered");
            }

            char[] confirmPassword = console.readPassword("Confirm Password: ");

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

}
