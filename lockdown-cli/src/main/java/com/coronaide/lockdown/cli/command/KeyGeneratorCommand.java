/*
 * Copyright (c) Apr 28, 2017 Corona IDE.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package com.coronaide.lockdown.cli.command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coronaide.lockdown.KeyGenerator;
import com.coronaide.lockdown.model.KeyFiles;

/**
 * Command line handler which allows generation of public/private key pairs
 *
 * @author romeara
 * @since 0.1.0
 */
public class KeyGeneratorCommand implements Runnable {

    /** Logger reference to output information to the application log files */
    private static final Logger logger = LoggerFactory.getLogger(KeyGeneratorCommand.class);

    private static final String PUBLIC_KEY_SUFFIX = ".pub";

    @Option(name = "-o", aliases = { "--output" }, required = true,
            usage = "Specifies the directory to output keys to. Required")
    private File outputDirectory;

    @Option(name = "-n", aliases = { "--name" }, required = false,
            usage = "Specifies the base name for keys. Default is 'lockdown_rsa'")
    private String baseFileName = "lockdown_rsa";

    @Option(name = "-f", aliases = { "--force" }, required = false,
            usage = "Overwrite any existing keys at the output locations. Default is to halt if keys are present")
    private boolean overwrite = false;

    @Override
    public void run() {
        KeyGenerator generator = new KeyGenerator();

        Path publicKeyDestination = outputDirectory.toPath().resolve(baseFileName + PUBLIC_KEY_SUFFIX);
        Path privateKeyDestination = outputDirectory.toPath().resolve(baseFileName);

        Collection<Path> unremovableKeys = getUnremovableKeys(publicKeyDestination, privateKeyDestination);

        if (unremovableKeys.isEmpty()) {
            try {
                KeyFiles generatedKeys = generator.createKeyPair(publicKeyDestination, privateKeyDestination);

                logger.info("Key files generated to \n\tPublic: {}\n\tPrivate: {}",
                        generatedKeys.getPublicKeyFile(),
                        generatedKeys.getPrivateKeyFile());
            } catch (IOException e) {
                logger.error("Error generating keys", e);
            }
        } else {
            logger.error("Key(s) exist at target location. Use -f to overwrite existing keys ({})", unremovableKeys);
        }
    }

    /**
     * Cross-references the target key locations which have existing files and the overwrite option specified by the
     * user
     *
     * @param publicKeyDestination
     *            The target location for the public key to be generated
     * @param privateKeyDestination
     *            The target location for the private key to be generated
     * @return A collection of the keys which exist and cannot be removed per the client's specified options
     */
    private Collection<Path> getUnremovableKeys(Path publicKeyDestination, Path privateKeyDestination) {
        Collection<Path> existingKeys = new ArrayList<>();

        if (publicKeyDestination.toFile().exists()) {
            existingKeys.add(publicKeyDestination);
        }

        if (privateKeyDestination.toFile().exists()) {
            existingKeys.add(privateKeyDestination);
        }

        if (overwrite && !existingKeys.isEmpty()) {
            existingKeys.stream()
            .map(Path::toFile)
            .forEach(File::delete);

            existingKeys.clear();
        }

        return existingKeys;
    }

}
