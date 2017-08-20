/*
 * Copyright (C) 2017 The Corona-IDE@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package org.starchartlabs.lockdown.cli.command;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starchartlabs.lockdown.KeyGenerator;
import org.starchartlabs.lockdown.model.KeyFiles;

/**
 * Command line handler which allows generation of public/private key pairs
 *
 * @author romeara
 * @since 1.0.0
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
        FileSystem fileSystem = FileSystems.getDefault();
        KeyGenerator generator = new KeyGenerator();

        Path destinationDirectory = fileSystem.getPath(outputDirectory.toPath().toString());
        Path publicKeyDestination = destinationDirectory.resolve(baseFileName + PUBLIC_KEY_SUFFIX);
        Path privateKeyDestination = destinationDirectory.resolve(baseFileName);

        Collection<Path> unremovableKeys = getUnremovableKeys(publicKeyDestination, privateKeyDestination);

        if (unremovableKeys.isEmpty()) {
            try {
                Files.createDirectories(destinationDirectory);

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
        Collection<Path> existingKeys = Arrays.asList(publicKeyDestination, privateKeyDestination).stream()
                .filter(Files::exists)
                .collect(Collectors.toList());

        if (overwrite) {
            try {
                for (Path key : existingKeys) {
                    Files.delete(key);
                }

                existingKeys.clear();
            } catch (IOException e) {
                logger.error("Error removing keys", e);
            }
        }

        return existingKeys;
    }

}
