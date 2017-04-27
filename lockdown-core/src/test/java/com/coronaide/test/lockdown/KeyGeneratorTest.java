/*
 * Copyright (c) Apr 25, 2017 Corona IDE.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package com.coronaide.test.lockdown;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.coronaide.lockdown.KeyGenerator;
import com.coronaide.lockdown.model.KeyFiles;

public class KeyGeneratorTest {

    private final KeyGenerator keyGenerator = new KeyGenerator();

    @Test(expectedExceptions = NullPointerException.class)
    public void createKeyPairNullPublicKeyDestination() throws Exception {
        Path publicKeyDestination = Files.createTempFile("key", "public");
        publicKeyDestination.toFile().deleteOnExit();

        keyGenerator.createKeyPair(publicKeyDestination, null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void createKeyPairNullPrivateKeyDestination() throws Exception {
        Path privateKeyDestination = Files.createTempFile("key", "private");
        privateKeyDestination.toFile().deleteOnExit();

        keyGenerator.createKeyPair(null, privateKeyDestination);
    }

    @Test
    public void createKeyPair() throws Exception {
        File directory = Files.createTempDirectory("key-gen-test").toFile();
        Path publicKeyDestination = Paths.get(directory.toString(), "key.public");
        Path privateKeyDestination = Paths.get(directory.toString(), "key.private");
        directory.deleteOnExit();
        publicKeyDestination.toFile().deleteOnExit();
        privateKeyDestination.toFile().deleteOnExit();

        KeyFiles result = keyGenerator.createKeyPair(publicKeyDestination, privateKeyDestination);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getPublicKeyFile(), publicKeyDestination);
        Assert.assertEquals(result.getPrivateKeyFile(), privateKeyDestination);

        Assert.assertTrue(publicKeyDestination.toFile().exists());
        Assert.assertTrue(privateKeyDestination.toFile().exists());
    }

}
