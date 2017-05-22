/*
 * Copyright (C) 2017 The Corona-IDE@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.coronaide.lockdown.cli;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommandHandler;
import org.kohsuke.args4j.spi.SubCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coronaide.lockdown.cli.command.AddCredentialsCommand;
import com.coronaide.lockdown.cli.command.KeyGeneratorCommand;

/**
 * Command line application which allows generation of public/private key pairs
 *
 * @author romeara
 * @since 0.1.0
 */
public class LockdownCommandLine {

    /** Logger reference to output information to the application log files */
    private static final Logger logger = LoggerFactory.getLogger(LockdownCommandLine.class);

    @Argument(handler = SubCommandHandler.class)
    @SubCommands({
        @SubCommand(name = "generate", impl = KeyGeneratorCommand.class),
        @SubCommand(name = "addkey", impl = AddCredentialsCommand.class)
    })
    private Runnable command;

    /**
     * Starts the application
     *
     * @param args
     *            Arguments specifying program behavior
     * @since 0.1.0
     */
    public static void main(String[] args) {
        new LockdownCommandLine().run(args);
    }

    /**
     * Runs an instance of an operation
     *
     * @param args
     *            Arguments specifying program behavior
     * @since 0.1.0
     */
    public void run(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);

        try {
            // parse the arguments.
            parser.parseArgument(args);

            if (command != null) {
                command.run();
            } else {
                logger.error("Invalid command line arguments");
                printUsage(parser);
            }
        } catch (CmdLineException e) {
            logger.error("Invalid command line arguments", e);
            printUsage(parser);
        }
    }

    /**
     * Prints the usage of application
     *
     * @param parser
     *            The main application parser to print usage details for
     */
    private void printUsage(CmdLineParser parser) {
        Objects.requireNonNull(parser);

        StringWriter usageWriter = new StringWriter();

        usageWriter.append("java LockdownCommandLine (generate | addkey) [options...] arguments...");
        usageWriter.append('\n');
        usageWriter.append('\n');

        parser.printUsage(usageWriter, null);

        // Print sub-command usages
        Map<String, Object> subCommands = new HashMap<>();
        subCommands.put("generate", new KeyGeneratorCommand());
        subCommands.put("addkey", new AddCredentialsCommand());

        for (Entry<String, Object> subCommand : subCommands.entrySet()) {
            usageWriter.append(subCommand.getKey());
            usageWriter.append('\n');

            CmdLineParser subParser = new CmdLineParser(subCommand.getValue());
            subParser.printUsage(usageWriter, null);
            usageWriter.append('\n');
        }

        String usage = usageWriter.toString();
        logger.error("\n{}", usage);
    }

}
