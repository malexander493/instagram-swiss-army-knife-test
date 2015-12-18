package org.orbit.soft.commandbox;

import com.beust.jcommander.JCommander;
import org.junit.Assert;
import org.junit.Test;
/**
 * Unit test for CommandExecutor.
 * 
 * 
 */
public class CommandExecutorTest {
    
    private static CommandAndControl isakCommand;
    private static JCommander subCommander;
    private static JCommander rootCommander;
    private static JCommander isakCommander;

    public static Object getSubCommand(String parsedCommand) {
        return isakCommander.getCommands().get(parsedCommand).getObjects()
                .get(0);
    }
    
    @Test
    public void evaluateCommands() {
        isakCommand = new CommandAndControl();
        rootCommander = null;

        rootCommander = new JCommander();
        rootCommander.addCommand("isak", isakCommand);
        subCommander = rootCommander.getCommands().get("isak");
        isakCommander = subCommander;
        isakCommander.addCommand(new getUserInfo());
        
        String[] args = {"isak", "getUserInfo", "-uid", "123", "-of", "JSON", 
            "-o", "/BigData/isakData" };
        rootCommander.parse(args);
            
        String parsedCommand = isakCommander.getParsedCommand();
        
        Assert.assertEquals("getUserInfo", parsedCommand);
        
        Object subCommand = getSubCommand(parsedCommand);
        
        getUserInfo userInfo = (getUserInfo) subCommand;
        
        Assert.assertEquals("123", userInfo.userId);
        Assert.assertEquals("JSON", userInfo.outputFormat);
        Assert.assertEquals("/BigData/isakData", userInfo.outputPath);
    }
}
