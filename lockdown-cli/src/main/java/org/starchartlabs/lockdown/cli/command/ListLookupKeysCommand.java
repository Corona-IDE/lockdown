/*
 * Copyright (C) 2017 The Corona-IDE@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.lockdown.cli.command;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.kohsuke.args4j.Argument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starchartlabs.lockdown.CredentialStore;

/**
 * Command line handler which allows listing of lookup keys linked to credentials
 *
 * @author romeara
 * @since 2.0.0
 */
public class ListLookupKeysCommand implements Runnable {

    /** Logger reference to output information to the application log files */
    private static final Logger logger = LoggerFactory.getLogger(ListLookupKeysCommand.class);

    @Argument(index = 0, required = true, usage = "Specifies the credential store to list lookup keys from. Required.")
    private File credentialStore;

    @Override
    public void run() {
        try {
            CredentialStore store = CredentialStore.loadOrCreate(credentialStore.toPath());

            Set<String> lookupKeys = store.getLookupKeys();

            logger.info("Found {} lookup keys in {}:", lookupKeys.size(), credentialStore.getName());
            lookupKeys.stream()
                    .forEach(lookupKey -> logger.info("    {}", lookupKey));
        } catch (IOException e) {
            logger.error("Error reading credential store", e);
        }
    }

}
