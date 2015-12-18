/*
 * Copyright (C) 2015 Orbit Software Solutions
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.orbit.soft.commandbox;

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
                        CommandExecutor.buildAndExecuteCommand(cmdArgs);
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
