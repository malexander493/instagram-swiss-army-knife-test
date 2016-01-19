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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 *
 * Assign commands and their options.
 */
@Parameters(commandNames = "isak", separators = "=", 
        commandDescription = "isak command system")
public class IsakCommand {
    
    private String apiKey;

    @Parameter(names = "-help", description = "Usage of ISAK")
    public boolean help = false;
    
    @Parameter(names = "--help", description = "Usage of ISAK")
    public String specificHelp;
    
    public String getApiKey() {
            return apiKey;
    }
    public void setApiKey(String key) {
            apiKey = key;
    }
}
