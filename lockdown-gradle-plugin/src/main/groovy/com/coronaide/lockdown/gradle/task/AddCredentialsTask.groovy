package com.coronaide.lockdown.gradle.task

import org.gradle.api.GradleException
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task which allows users to add/update credentials without downloading the CLI separately
 *
 * @author romeara
 * @since 0.1.0
 */
public class AddCredentialsTask extends JavaExec {

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

        main = 'com.coronaide.lockdown.cli.LockdownCommandLine'
        classpath = project.buildscript.configurations.classpath
        standardInput = System.in

        // arguments to pass to the application
        args 'addkey', "${lookupKey}", '-o', "${credentialFile}", '-k', "${publicKey}"

        super.exec()
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