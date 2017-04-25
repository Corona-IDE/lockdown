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
package com.coronaide.test.lockdown.model;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.coronaide.lockdown.model.KeyFiles;

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
