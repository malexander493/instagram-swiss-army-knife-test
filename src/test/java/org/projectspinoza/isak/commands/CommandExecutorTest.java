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
package org.projectspinoza.isak.commands;

import org.projectspinoza.isak.commands.GetUserInfo;
import org.projectspinoza.isak.commands.IsakCommand;
import com.beust.jcommander.JCommander;
import org.junit.Assert;
import org.junit.Test;
/**
 * Unit test for CommandExecutor.
 * 
 * 
 */
public class CommandExecutorTest {
    
    private static IsakCommand isakCommand;
    private static JCommander subCommander;
    private static JCommander rootCommander;
    private static JCommander isakCommander;

    public static Object getSubCommand(String parsedCommand) {
        return isakCommander.getCommands().get(parsedCommand).getObjects()
                .get(0);
    }
    
    @Test
    public void evaluateCommands() {
        isakCommand = new IsakCommand();
        rootCommander = null;

        rootCommander = new JCommander();
        rootCommander.addCommand("isak", isakCommand);
        subCommander = rootCommander.getCommands().get("isak");
        isakCommander = subCommander;
        isakCommander.addCommand(new GetUserInfo());
        
        String[] args = {"isak", "getUserInfo", "-uid", "123", "-of", "JSON", 
            "-o", "/BigData/isakData" };
        rootCommander.parse(args);
            
        String parsedCommand = isakCommander.getParsedCommand();
        
        Assert.assertEquals("getUserInfo", parsedCommand);
        
        Object subCommand = getSubCommand(parsedCommand);
        
        GetUserInfo userInfo = (GetUserInfo) subCommand;
        
        Assert.assertEquals("123", userInfo.userId);
        Assert.assertEquals("JSON", userInfo.outputFormat);
        Assert.assertEquals("/BigData/isakData", userInfo.outputPath);
    }
}
