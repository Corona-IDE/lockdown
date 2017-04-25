/*
 * Copyright (c) Apr 24, 2017 Corona IDE.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package com.coronaide.lockdown;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Objects;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import com.coronaide.lockdown.model.KeyFiles;

/**
 * Represents facilities to create key pairs for use in encryption and decryption of credential data
 *
 * @author romeara
 * @since 0.1.0
 */
public class KeyGenerator {

    /**
     * Creates a new key generation instance, initializing any necessary JVM state
     *
     * <p>
     * This implementation utilizes the bouncy castle library, and registers the {@link BouncyCastleProvider}
     */
    public KeyGenerator() {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Create a public/private pair of key files, which can be used for encryption and decryption of data
     *
     * @param publicKeyDestination
     *            The location to write the public key to
     * @param privateKeyDestination
     *            The location to write the private key to
     * @return A record of the output locations of the public and private key files
     * @throws IOException
     *             If there is an error writing to the locations specified
     * @since 0.1.0
     */
    public KeyFiles createKeyPair(File publicKeyDestination, File privateKeyDestination) throws IOException {
        Objects.requireNonNull(publicKeyDestination);
        Objects.requireNonNull(privateKeyDestination);

        KeyPairGenerator generator = null;

        try {
            generator = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
            generator.initialize(1024, SecureRandom.getInstanceStrong());
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            // We register the provider at construction, and RSA is a standard algorithm - if these exceptions occur,
            // something is legitimately wrong with the JVM
            throw new IllegalStateException("Error configuring key generator", e);
        }

        KeyPair pair = generator.generateKeyPair();

        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(publicKeyDestination))) {
            Base64.encode(pair.getPublic().getEncoded(), out);
        }

        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(privateKeyDestination))) {
            Base64.encode(pair.getPrivate().getEncoded(), out);
        }

        return new KeyFiles(publicKeyDestination.toPath(), privateKeyDestination.toPath());
    }

}