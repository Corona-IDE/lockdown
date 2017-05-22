/*
 * Copyright (C) 2017 The Corona-IDE@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.coronaide.test.lockdown;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.coronaide.lockdown.KeyGenerator;
import com.coronaide.lockdown.model.KeyFiles;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class KeyGeneratorTest {

    private final KeyGenerator keyGenerator = new KeyGenerator();

    // Use a virtual file system for tests, as CI providers can be much lower power
    private FileSystem virtualFileSystem = Jimfs.newFileSystem(Configuration.unix());

    @Test(expectedExceptions = NullPointerException.class, groups = "KEY_GENERATION_TEST")
    public void createKeyPairNullPublicKeyDestination() throws Exception {
        Path publicKeyDestination = virtualFileSystem.getPath("key", "public");

        keyGenerator.createKeyPair(publicKeyDestination, null);
    }

    @Test(expectedExceptions = NullPointerException.class, groups = "KEY_GENERATION_TEST")
    public void createKeyPairNullPrivateKeyDestination() throws Exception {
        Path privateKeyDestination = virtualFileSystem.getPath("key", "private");

        keyGenerator.createKeyPair(null, privateKeyDestination);
    }

    @Test(groups = "KEY_GENERATION_TEST")
    public void createKeyPair() throws Exception {
        Path publicKeyDestination = virtualFileSystem.getPath("create.public");
        Path privateKeyDestination = virtualFileSystem.getPath("create.private");

        KeyFiles result = keyGenerator.createKeyPair(publicKeyDestination, privateKeyDestination);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getPublicKeyFile(), publicKeyDestination);
        Assert.assertEquals(result.getPrivateKeyFile(), privateKeyDestination);

        Assert.assertTrue(Files.exists(publicKeyDestination));
        Assert.assertTrue(Files.exists(privateKeyDestination));
    }

}
