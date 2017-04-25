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
package com.coronaide.lockdown.model;

import java.nio.file.Path;
import java.util.Objects;

import javax.annotation.Nullable;

/**
 * Represents the results of a key generation operation, with locations for the public and private key files
 *
 * @author romeara
 * @since 0.1.0
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
