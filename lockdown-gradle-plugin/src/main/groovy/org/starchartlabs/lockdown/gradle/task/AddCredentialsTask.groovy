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
 * Gradle task which allows users to add/update credentials without downloading the CLI separately
 *
 * @author romeara
 * @since 1.0.0
 */
public class AddCredentialsTask extends DefaultTask {

    private String publicKey

    private String credentialFile

    private String lookupKey

    @TaskAction
    public void exec() {
        //If not explicitly specified, allow use of -P argument from command line
        if(lookupKey == null && project.hasProperty('lookupKey')){
            lookupKey = project.getProperty('lookupKey')
        }

        //Check all required arguments are provided
        if(lookupKey == null){
            throw new GradleException('Lookup key to assign to credentials not provided')
        }else if(credentialFile == null){
            throw new GradleException('Credential file to add credentials to not provided')
        }else if(publicKey == null){
            throw new GradleException('Public key for encrypting added credentials not provided')
        }

        CredentialPrompt credentials = new CredentialPrompt()
        credentials.prompt()

        CredentialStore store = CredentialStore.loadOrCreate(Paths.get(credentialFile));
        store.addOrUpdateCredentials(lookupKey, credentials.username, credentials.password.toCharArray(), Paths.get(publicKey))

        logger.lifecycle("Credentials added to store at ${credentialFile} under key ${lookupKey}")
    }

    public void setPublicKey(String publicKey){
        this.publicKey = publicKey
    }

    public void setCredentialFile(String credentialFile){
        this.credentialFile = credentialFile
    }

    public void setLookupKey(String lookupKey){
        this.lookupKey = lookupKey
    }

}