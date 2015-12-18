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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.orbit.soft.commandbox;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 *
 * Assign commands and their options.
 * 
 */
@Parameters(commandNames = "isak", separators = "=", 
        commandDescription = "isak command system")
public class CommandAndControl {
    private String apiKey;

    public String getApiKey(){
            return apiKey;
    }
    public void setApiKey(String key){
            apiKey = key;
    }

    @Parameter(names = "-help", description = "Usage of ISAK")
    public boolean help = false;
    
    @Parameter(names = "--help", description = "Usage of ISAK")
    public String specificHelp;
}

class BaseCommand {
	@Parameter(names = "-o", required = true,
                description = "Output file path.")
	public String outputPath;
        
}

@Parameters(commandNames = "getUserInfo",  
        commandDescription = "Get basic information about a user.")
class getUserInfo extends BaseCommand {
    @Parameter(names = "-uid", required = true, 
            description = "User ID to get information for. ")
	public String userId;
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json, console, cj(console an json))")
	public String outputFormat;
}

@Parameters(commandNames = "getRecentMediaFeed",  
        commandDescription = "Get the most recent media published by a user.")
class getRecentMediaFeed extends BaseCommand {
    @Parameter(names = "-uid", required = true, 
            description = "User ID to get information for. ")
	public String userId;
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json)")
	public String outputFormat;
}

@Parameters(commandNames = "searchUser",  
        commandDescription = "Search for a user by name.")
class searchUser extends BaseCommand {
    @Parameter(names = "-q", required = true, 
            description = "Search for a user by name.")
	public String query;
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json, console, cj(console and json))")
	public String outputFormat;
}

@Parameters(commandNames = "getUserFollowList",  
        commandDescription = "Get the list of users this user follows.")
class getUserFollowList extends BaseCommand {
    @Parameter(names = "-uid", required = true, 
            description = "User ID to get information for. ")
	public String userId;
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json)")
	public String outputFormat;
}

@Parameters(commandNames = "getUserFollowedByList",  
        commandDescription = "Get the list of users this user is followed by.")
class getUserFollowedByList extends BaseCommand {
    @Parameter(names = "-uid", required = true, 
            description = "User ID to get information for. ")
	public String userId;
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json)")
	public String outputFormat;
}

@Parameters(commandNames = "getMediaInfo",  
        commandDescription = "Get information about a media object.")
class getMediaInfo extends BaseCommand {
    @Parameter(names = "-mid", required = true, 
            description = "Media ID to get information for. ")
	public String mediaId;
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json)")
	public String outputFormat;
}

@Parameters(commandNames = "searchMediaByLatLng",  
        commandDescription = "Search for media in a given area.")
class searchMediaByLatLng extends BaseCommand {
    @Parameter(names = "-lat", required = true, 
            description = "latitude of area")
	public String latitude;
    
    @Parameter(names = "-lng", required = true, 
            description = "longitude of area")
	public String longitude;
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json)")
	public String outputFormat;
}

@Parameters(commandNames = "getPopularMedia",  
        commandDescription = "Get current popular media.")
class getPopularMedia extends BaseCommand {
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json)")
	public String outputFormat;
}

@Parameters(commandNames = "getMediaComments",  
        commandDescription = "Get a list of recent comments on a media object.")
class getMediaComments extends BaseCommand {
    
    @Parameter(names = "-mid", required = true, 
            description = "Media ID to get information for. ")
	public String mediaId;
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json)")
	public String outputFormat;
}

@Parameters(commandNames = "getUserLikes",  
        commandDescription = "Get a list of users who have liked this media.")
class getUserLikes extends BaseCommand {
    
    @Parameter(names = "-mid", required = true, 
            description = "Media ID to get information for. ")
	public String mediaId;
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json)")
	public String outputFormat;
}

@Parameters(commandNames = "getTagInfo",  
        commandDescription = "Get information about a tag object.")
class getTagInfo extends BaseCommand {
    
    @Parameter(names = "-tag", required = true, 
            description = "Tag name to get information for.")
	public String tagName;
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json, console, cj(console and json))")
	public String outputFormat;
}

@Parameters(commandNames = "getRecentMediaTags",  
        commandDescription = "Get a list of recently tagged media.")
class getRecentMediaTags extends BaseCommand {
    
    @Parameter(names = "-tag", required = true, 
            description = "Tag name to get information for.")
	public String tagName;
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json)")
	public String outputFormat;
}

@Parameters(commandNames = "searchTags",  
        commandDescription = "Search for tags by name")
class searchTags extends BaseCommand {
    
    @Parameter(names = "-tag", required = true, 
            description = "Tag name to get information for.")
	public String tagName;
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json)")
	public String outputFormat;
}

@Parameters(commandNames = "getLocationInfo",  
        commandDescription = "Get information about a location.")
class getLocationInfo extends BaseCommand {
    
    @Parameter(names = "-locationId", required = true, 
            description = "Location Id to get information for.")
	public String locationId;
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json, console, cj(console and json))")
	public String outputFormat;
}

@Parameters(commandNames = "getRecentMediaByLocation",  
        commandDescription = "Get a list of recent media objects "
                + "from a given location.")
class getRecentMediaByLocation extends BaseCommand {
    
    @Parameter(names = "-locationId", required = true, 
            description = "Location Id to get media.")
	public String locationId;
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json)")
	public String outputFormat;
}

@Parameters(commandNames = "searchLocation",  
        commandDescription = "Search for a location by geographic coordinate.")
class searchLocation extends BaseCommand {
    
    @Parameter(names = "-lat", required = true, 
            description = "latitude of area")
	public String latitude;
    
    @Parameter(names = "-lng", required = true, 
            description = "longitude of area")
	public String longitude;
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json, console, cj(console and json))")
	public String outputFormat;
}