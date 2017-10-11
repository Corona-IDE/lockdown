/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package org.starchartlabs.lockdown.test.gradle.task;

import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.starchartlabs.lockdown.gradle.task.ListLookupKeysTask;
import org.testng.annotations.Test;

public class ListLookupKeysTaskTest {

    // Make sure creating doing simple listing doesn't break
    @Test
    public void configure() throws Exception {
        Path outputDirectory = Files.createTempDirectory("lockdown.cli.test");
        outputDirectory.toFile().deleteOnExit();

        Path testStore = outputDirectory.resolve("test.properties");
        testStore.toFile().deleteOnExit();

        Project project = ProjectBuilder.builder().build();
        ListLookupKeysTask listTask = project.getTasks().create("listLookupKeysTest", ListLookupKeysTask.class);
        listTask.setCredentialFile(testStore.toAbsolutePath().toString());

        listTask.exec();
    }

}
