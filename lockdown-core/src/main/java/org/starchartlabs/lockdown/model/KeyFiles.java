/*
 * Copyright (C) 2017 The Corona-IDE@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package org.starchartlabs.lockdown.model;

import java.nio.file.Path;
import java.util.Objects;

import javax.annotation.Nullable;

/**
 * Represents the results of a key generation operation, with locations for the public and private key files
 *
 * @author romeara
 * @since 1.0.0
 */
public class KeyFiles {

    private final Path publicKeyFile;

    private final Path privateKeyFile;

    /**
     * @param publicKeyFile
     *            Path to a generated public key file, used for encryption
     * @param privateKeyFile
     *            Path to a generated private key file, used for decryption
     * @since 0.1.0
     */
    public KeyFiles(Path publicKeyFile, Path privateKeyFile) {
        this.publicKeyFile = Objects.requireNonNull(publicKeyFile);
        this.privateKeyFile = Objects.requireNonNull(privateKeyFile);
    }

    /**
     * @return Path to a generated public key file, used for encryption
     * @since 0.1.0
     */
    public Path getPublicKeyFile() {
        return publicKeyFile;
    }

    /**
     * @return Path to a generated private key file, used for decryption
     * @since 0.1.0
     */
    public Path getPrivateKeyFile() {
        return privateKeyFile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPublicKeyFile(),
                getPrivateKeyFile());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof KeyFiles) {
            KeyFiles compare = (KeyFiles) obj;

            result = Objects.equals(compare.getPublicKeyFile(), getPublicKeyFile())
                    && Objects.equals(compare.getPrivateKeyFile(), getPrivateKeyFile());
        }

        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(getClass().getSimpleName()).append('{')
                .append("publicKeyFile").append('=').append(getPublicKeyFile())
                .append("privateKeyFile").append('=').append(getPrivateKeyFile())
                .append('}').toString();
    }

}
