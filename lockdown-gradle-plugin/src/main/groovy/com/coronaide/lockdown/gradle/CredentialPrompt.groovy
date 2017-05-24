/*
 * Copyright (C) 2017 The Corona-IDE@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.coronaide.lockdown.gradle

import groovy.swing.SwingBuilder

/**
 * Prompt mechanism which collects credentials from a user
 *
 * @author romeara
 * @since 0.1.0
 */
public class CredentialPrompt {

    private String username

    private String password

    public void prompt(){
        new SwingBuilder().edt {
            dialog(modal: true, // Otherwise the build will continue running before you closed the dialog
            title: 'Enter Credentials',
            alwaysOnTop: true,
            resizable: false, // Don't allow the user to resize the dialog
            locationRelativeTo: null, // Place dialog in center of the screen
            pack: true,
            show: true // Show the dialog
            ) {
                vbox {
                    tableLayout {
                        tr {
                            td { label 'Username: ' }
                            td { textField username, id: 'usernameField', columns: 15 }
                        }
                        tr {
                            td { label 'Password: ' }
                            td { passwordField id: 'passwordField', columns: 15 }
                        }
                        tr {
                            td {
                                button(defaultButton: true, text: 'OK', actionPerformed: {
                                    username = usernameField.text;
                                    password = passwordField.text;
                                    dispose();
                                })
                            }
                            td { }
                        }
                    }
                }
            }
        }
    }

    public String getUsername(){
        return username
    }

    public String getPassword(){
        return password
    }
}