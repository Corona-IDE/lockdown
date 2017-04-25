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
import java.nio.file.Paths;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.coronaide.lockdown.KeyGenerator;
import com.coronaide.lockdown.model.KeyFiles;

public class KeyGeneratorTest {

    private final KeyGenerator keyGenerator = new KeyGenerator();

    @Test(expectedExceptions = NullPointerException.class)
    public void createKeyPairNullPublicKeyDestination() throws Exception {
        File publicKeyDestination = Files.createTempFile("key", "public").toFile();
        publicKeyDestination.deleteOnExit();

        keyGenerator.createKeyPair(publicKeyDestination, null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void createKeyPairNullPrivateKeyDestination() throws Exception {
        File privateKeyDestination = Files.createTempFile("key", "private").toFile();
        privateKeyDestination.deleteOnExit();

        keyGenerator.createKeyPair(null, privateKeyDestination);
    }

    @Test
    public void createKeyPair() throws Exception {
        File directory = Files.createTempDirectory("key-gen-test").toFile();
        File publicKeyDestination = Paths.get(directory.toString(), "key.public").toFile();
        File privateKeyDestination = Paths.get(directory.toString(), "key.private").toFile();
        directory.deleteOnExit();
        publicKeyDestination.deleteOnExit();
        privateKeyDestination.deleteOnExit();

        KeyFiles result = keyGenerator.createKeyPair(publicKeyDestination, privateKeyDestination);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getPublicKeyFile(), publicKeyDestination.toPath());
        Assert.assertEquals(result.getPrivateKeyFile(), privateKeyDestination.toPath());

        Assert.assertTrue(publicKeyDestination.exists());
        Assert.assertTrue(privateKeyDestination.exists());
    }

}
