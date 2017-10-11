/*
 * Copyright (C) 2017 The Corona-IDE@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package org.starchartlabs.lockdown.gradle.task

import java.nio.file.Paths

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.starchartlabs.lockdown.CredentialStore
import org.starchartlabs.lockdown.gradle.CredentialPrompt

/**
 * Gradle task which allows users to list the lookup keys already configured in a credential store
 *
 * @author romeara
 * @since 2.0.0
 */
public class ListLookupKeysTask extends DefaultTask {

    private String credentialFile

    @TaskAction
    public void exec() {
        ///Check all required arguments are provided
        if(credentialFile == null){
            throw new GradleException('Credential file to list lookup keys of not provided')
        }

        CredentialStore store = CredentialStore.loadOrCreate(Paths.get(credentialFile));
        
        logger.lifecycle("Lookup keys available in ${credentialFile}")
        store.getLookupKeys().forEach{ key -> logger.lifecycle("    ${key}") }
    }

    public void setCredentialFile(String credentialFile){
        this.credentialFile = credentialFile
    }

}