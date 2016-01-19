/*
 * Copyright 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.projectspinoza.isak.commandbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jline.console.ConsoleReader;

import com.beust.jcommander.ParameterException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * Responsible to take commands from console and assign them to command and
 * control
 *
 *
 * @author Orbit Software Solutions
 */
public class Main {

    private static final Logger log = LogManager.getRootLogger();

    public static void main(String[] args) throws IOException {

        log.info("Welcome to ISAK");
        log.info("Run 'isak -help' to display the help index.");
        log.info("Run 'isak --help <command>' to display help for specific "
                + "commands");

        final ConsoleReader reader = new ConsoleReader();
        String commandLine;

        OUTER:
        while (true) {
            try {
                commandLine = reader.readLine("isak>");
                switch (commandLine.trim()) {
                    case "":
                        continue;
                    case "exit":
                        log.info("Good Bye...");
                        reader.flush();
                        break OUTER;
                    default:
                        List<String> mList = new ArrayList<>();
                        Pattern regex = Pattern.
                                compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
                        Matcher regexMatcher = regex.matcher(commandLine);
                        while (regexMatcher.find()) {
                            mList.add(regexMatcher.group());
                        }
                        String[] cmdArgs
                                = mList.toArray(new String[mList.size()]);
                        IsakSwissArmyKnife.buildAndExecuteCommand(cmdArgs);
                        break;
                }
            } catch (ParameterException ex) {
                log.info(ex.getMessage());
            } catch (IllegalStateException | IOException ex) {
                reader.println(ex.getMessage());
                reader.flush();
            }
        }
        log.info("!!! DONE !!!");
    }
}
