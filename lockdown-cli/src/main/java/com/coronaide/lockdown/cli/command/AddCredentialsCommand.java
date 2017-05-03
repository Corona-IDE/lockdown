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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
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

    @Option(name = "-u", aliases = { "--username" }, required = false,
            usage = "Specifies the username to store. Default is to enter at runtime")
    private String username = null;

    @Override
    public void run() {
        try {
            CredentialStore store = CredentialStore.loadOrCreate(credentialStore.toPath());

            Terminal terminal = TerminalBuilder.builder()
                    .system(true)
                    .build();
            LineReader lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();

            if (username == null) {
                username = lineReader.readLine("Username: ");
            }
            char[] password = lineReader.readLine("Password: ", '*').toCharArray();

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
