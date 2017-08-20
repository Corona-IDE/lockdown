/*
 * Copyright (C) 2017 The Corona-IDE@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package org.starchartlabs.test.lockdown.model;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.starchartlabs.lockdown.model.KeyFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

public class KeyFilesTest {

    private static final Path PUBLIC_KEY_FILE = Paths.get("public");

    private static final Path PRIVATE_KEY_FILE = Paths.get("private");

    @Test(expectedExceptions = NullPointerException.class)
    public void createNullPublicKeyFile() throws Exception {
        new KeyFiles(null, PRIVATE_KEY_FILE);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void createNullPrivateKeyFile() throws Exception {
        new KeyFiles(PUBLIC_KEY_FILE, null);
    }

    @Test
    public void getTest() throws Exception {
        KeyFiles result = new KeyFiles(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE);

        Assert.assertEquals(result.getPublicKeyFile(), PUBLIC_KEY_FILE);
        Assert.assertEquals(result.getPrivateKeyFile(), PRIVATE_KEY_FILE);
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        KeyFiles result1 = new KeyFiles(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE);
        KeyFiles result2 = new KeyFiles(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE);

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        KeyFiles result = new KeyFiles(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE);

        Assert.assertFalse(result.equals(null));
    }

    @Test
    public void equalsDifferentClass() throws Exception {
        KeyFiles result = new KeyFiles(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE);

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        KeyFiles result = new KeyFiles(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE);

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        KeyFiles result1 = new KeyFiles(Paths.get("public1"), PRIVATE_KEY_FILE);
        KeyFiles result2 = new KeyFiles(Paths.get("public2"), PRIVATE_KEY_FILE);

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        KeyFiles result1 = new KeyFiles(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE);
        KeyFiles result2 = new KeyFiles(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE);

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        KeyFiles obj = new KeyFiles(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE);

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("publicKeyFile=" + PUBLIC_KEY_FILE.toString()));
        Assert.assertTrue(result.contains("privateKeyFile=" + PRIVATE_KEY_FILE.toString()));
    }

}
