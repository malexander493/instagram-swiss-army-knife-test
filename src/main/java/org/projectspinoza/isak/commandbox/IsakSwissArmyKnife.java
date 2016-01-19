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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.projectspinoza.isak.commands.GetLocationInfo;
import org.projectspinoza.isak.commands.GetMediaComments;
import org.projectspinoza.isak.commands.GetMediaInfo;
import org.projectspinoza.isak.commands.GetPopularMedia;
import org.projectspinoza.isak.commands.GetRecentMediaByLocation;
import org.projectspinoza.isak.commands.GetRecentMediaFeed;
import org.projectspinoza.isak.commands.GetRecentMediaTags;
import org.projectspinoza.isak.commands.GetTagInfo;
import org.projectspinoza.isak.commands.GetUserFollowList;
import org.projectspinoza.isak.commands.GetUserFollowedByList;
import org.projectspinoza.isak.commands.GetUserInfo;
import org.projectspinoza.isak.commands.GetUserLikes;
import org.projectspinoza.isak.commands.IsakCommand;
import org.projectspinoza.isak.commands.SearchLocation;
import org.projectspinoza.isak.commands.SearchMediaByLatLng;
import org.projectspinoza.isak.commands.SearchTags;
import org.projectspinoza.isak.commands.SearchUser;

/**
 *
 * Takes arguments from main class then analyze and assign them to relevant
 * resources.
 *
 */
public class IsakSwissArmyKnife {

    private static final Logger log = LogManager.getRootLogger();

    private static IsakCommand isakCommand;
    private static JCommander subCommander;
    private static JCommander rootCommander;
    private static JCommander isakCommander;
    private static boolean authorize;
    private static String apiKey;
    private static String userId;
    private static String outputFormat;
    private static String outputPath;
    private static String query;
    private static String mediaId;
    private static String latitude;
    private static String longitude;
    private static String tagName;
    private static String locationId;

    public static void buildAndExecuteCommand(String[] args) {

        isakCommand = new IsakCommand();
        rootCommander = null;
        try {

            rootCommander = new JCommander();
            rootCommander.addCommand("isak", isakCommand);
            subCommander = rootCommander.getCommands().get("isak");
            isakCommander = subCommander;
            isakCommander.addCommand(new GetUserInfo());
            isakCommander.addCommand(new GetRecentMediaFeed());
            isakCommander.addCommand(new SearchUser());
            isakCommander.addCommand(new GetUserFollowList());
            isakCommander.addCommand(new GetUserFollowedByList());
            isakCommander.addCommand(new GetMediaInfo());
            isakCommander.addCommand(new SearchMediaByLatLng());
            isakCommander.addCommand(new GetPopularMedia());
            isakCommander.addCommand(new GetMediaComments());
            isakCommander.addCommand(new GetUserLikes());
            isakCommander.addCommand(new GetTagInfo());
            isakCommander.addCommand(new GetRecentMediaTags());
            isakCommander.addCommand(new SearchTags());
            isakCommander.addCommand(new GetLocationInfo());
            isakCommander.addCommand(new GetRecentMediaByLocation());
            isakCommander.addCommand(new SearchLocation());

            
            rootCommander.parse(args);

            String rootParsedCommand = rootCommander.getParsedCommand();

            Object rootSubCommand = getRootOptions("isak");

            Boolean help = false;
            String specificHelp = null;
            if (rootParsedCommand.equals("isak")) {
                IsakCommand rootIsakCommand = (IsakCommand) 
                        rootSubCommand;
                help = rootIsakCommand.help;
                specificHelp = rootIsakCommand.specificHelp;

                if (help)
                    rootCommander.usage();
                
                if (specificHelp != null) {
                    System.out.println("");
                    isakCommander.usage(specificHelp);
                }
            }

            if (! help && specificHelp == null) {
                // CommandAndControl
                String parsedCommand = isakCommander.getParsedCommand();
                if (parsedCommand == null) {
                    log.info("Please provide command and options.");
                    rootCommander.usage();
                } else {

                    if (!isAuthorized()) {
                        authorizeUser();
                    }

                    if (isAuthorized()) {

                        Object subCommand = getSubCommand(parsedCommand);
                        log.info("Executing Command [" + parsedCommand + "]");

                        switch (parsedCommand) {
                            case "getUserInfo":
                                // Get basic information about a user.
                                GetUserInfo userInfo = (GetUserInfo) subCommand;
                                userId = userInfo.userId;
                                outputFormat = userInfo.outputFormat;
                                outputPath = userInfo.outputPath;

                                GetUserInfo getUserInfo = new GetUserInfo();
                                getUserInfo.setUserApiKey(apiKey);
                                getUserInfo.setUserId(userId);
                                getUserInfo.setFileOutputFormat(outputFormat);
                                getUserInfo.setFileOutputPath(outputPath);
                                getUserInfo.execute();
                                break;
                            case "getRecentMediaFeed":
                                // Get Recent Media Feed of a user.
                                GetRecentMediaFeed recentMediaFeed
                                        = (GetRecentMediaFeed) subCommand;

                                userId = recentMediaFeed.userId;
                                outputFormat = recentMediaFeed.outputFormat;
                                outputPath = recentMediaFeed.outputPath;
                                    
                                GetRecentMediaFeed getRecentMediaFeed = 
                                        new GetRecentMediaFeed();
                                getRecentMediaFeed.setUserApiKey(apiKey);
                                getRecentMediaFeed.setUserId(userId);
                                getRecentMediaFeed.
                                        setFileOutputFormat(outputFormat);
                                getRecentMediaFeed.
                                        setFileOutputPath(outputPath);
                                getRecentMediaFeed.execute();
                                break;
                            case "searchUser":
                                // Search for a user by name.
                                SearchUser searchUserData = 
                                        (SearchUser) subCommand;
                                query = searchUserData.query;
                                outputFormat = searchUserData.outputFormat;
                                outputPath = searchUserData.outputPath;

                                SearchUser searchUser = new SearchUser();
                                searchUser.setUserApiKey(apiKey);
                                searchUser.setQuery(query);
                                searchUser.setFileOutputFormat(outputFormat);
                                searchUser.setFileOutputPath(outputPath);
                                searchUser.execute();
                                break;
                            case "getUserFollowList":
                                // Get the list of users this user follows.
                                GetUserFollowList userFollowList
                                        = (GetUserFollowList) subCommand;

                                userId = userFollowList.userId;
                                outputFormat = userFollowList.outputFormat;
                                outputPath = userFollowList.outputPath;

                                GetUserFollowList getUserFollowList = 
                                        new GetUserFollowList();
                                getUserFollowList.setUserApiKey(apiKey);
                                getUserFollowList.setUserId(userId);
                                getUserFollowList.
                                        setFileOutputFormat(outputFormat);
                                getUserFollowList.setFileOutputPath(outputPath);
                                getUserFollowList.execute();
                                break;
                            case "getUserFollowedByList":
                                // Get the list of users 
                                // this user is followed by.
                                GetUserFollowedByList userFollowedByList
                                        = (GetUserFollowedByList) subCommand;

                                userId = userFollowedByList.userId;
                                outputFormat = userFollowedByList.outputFormat;
                                outputPath = userFollowedByList.outputPath;

                                GetUserFollowedByList getUserFollowedByList = 
                                        new GetUserFollowedByList();
                                getUserFollowedByList.setUserApiKey(apiKey);
                                getUserFollowedByList.setUserId(userId);
                                getUserFollowedByList.
                                        setFileOutputFormat(outputFormat);
                                getUserFollowedByList.
                                        setFileOutputPath(outputPath);
                                getUserFollowedByList.execute();
                                break;
                            case "getMediaInfo":
                                // Get information about a media object.
                                GetMediaInfo mediaInfo
                                        = (GetMediaInfo) subCommand;

                                mediaId = mediaInfo.mediaId;
                                outputFormat = mediaInfo.outputFormat;
                                outputPath = mediaInfo.outputPath;

                                GetMediaInfo getMediaInfo = new GetMediaInfo();
                                getMediaInfo.setUserApiKey(apiKey);
                                getMediaInfo.setMediaId(mediaId);
                                getMediaInfo.
                                        setFileOutputFormat(outputFormat);
                                getMediaInfo.
                                        setFileOutputPath(outputPath);
                                getMediaInfo.execute();
                                break;
                            case "searchMediaByLatLng":
                                // Search for media in a given area.
                                SearchMediaByLatLng searchMediaByLatLngFeed
                                        = (SearchMediaByLatLng) subCommand;

                                latitude = searchMediaByLatLngFeed.latitude;
                                longitude = searchMediaByLatLngFeed.longitude;
                                outputFormat = searchMediaByLatLngFeed.
                                        outputFormat;
                                outputPath = searchMediaByLatLngFeed.outputPath;

                                SearchMediaByLatLng searchMediaByLatLng = 
                                        new SearchMediaByLatLng();
                                searchMediaByLatLng.setUserApiKey(apiKey);
                                searchMediaByLatLng.setLatitude(latitude);
                                searchMediaByLatLng.setLongitude(longitude);
                                searchMediaByLatLng.
                                        setFileOutputFormat(outputFormat);
                                searchMediaByLatLng.
                                        setFileOutputPath(outputPath);
                                searchMediaByLatLng.execute();
                                break;
                            case "getPopularMedia":
                                // Get current popular media.
                                GetPopularMedia popularMediaFeed
                                        = (GetPopularMedia) subCommand;

                                outputFormat = popularMediaFeed.outputFormat;
                                outputPath = popularMediaFeed.outputPath;

                                GetPopularMedia getPopularMedia = 
                                        new GetPopularMedia();
                                
                                getPopularMedia.setUserApiKey(apiKey);
                                getPopularMedia.
                                        setFileOutputFormat(outputFormat);
                                getPopularMedia.
                                        setFileOutputPath(outputPath);
                                getPopularMedia.execute();
                                break;
                            case "getMediaComments":
                                // Get a list of recent comments 
                                // on a media object.
                                GetMediaComments mediaCommentsFeed
                                        = (GetMediaComments) subCommand;

                                mediaId = mediaCommentsFeed.mediaId;
                                outputFormat = mediaCommentsFeed.outputFormat;
                                outputPath = mediaCommentsFeed.outputPath;

                                GetMediaComments getMediaComments = 
                                        new GetMediaComments();
                                
                                getMediaComments.setMediaId(mediaId);
                                getMediaComments.setUserApiKey(apiKey);
                                getMediaComments.
                                        setFileOutputFormat(outputFormat);
                                getMediaComments.
                                        setFileOutputPath(outputPath);
                                getMediaComments.execute();
                                break;
                            case "getUserLikes":
                                // Get a list of users 
                                // who have liked this media.
                                GetUserLikes userLikesFeed
                                        = (GetUserLikes) subCommand;

                                mediaId = userLikesFeed.mediaId;
                                outputFormat = userLikesFeed.outputFormat;
                                outputPath = userLikesFeed.outputPath;

                                GetUserLikes getUserLikes = new GetUserLikes();
                                
                                getUserLikes.setMediaId(mediaId);
                                getUserLikes.setUserApiKey(apiKey);
                                getUserLikes.setFileOutputFormat(outputFormat);
                                getUserLikes.setFileOutputPath(outputPath);
                                getUserLikes.execute();
                                break;
                            case "getTagInfo":
                                // Get information about a tag object.
                                GetTagInfo tagInfoFeed
                                        = (GetTagInfo) subCommand;

                                tagName = tagInfoFeed.tagName;
                                outputFormat = tagInfoFeed.outputFormat;
                                outputPath = tagInfoFeed.outputPath;

                                GetTagInfo getTagInfo = new GetTagInfo();
                                
                                getTagInfo.setTagName(tagName);
                                getTagInfo.setUserApiKey(apiKey);
                                getTagInfo.setFileOutputFormat(outputFormat);
                                getTagInfo.setFileOutputPath(outputPath);
                                getTagInfo.execute();
                                break;
                            case "getRecentMediaTags":
                                // Get a list of recently tagged media.
                                GetRecentMediaTags recentMediaTagsFeed
                                        = (GetRecentMediaTags) subCommand;

                                tagName = recentMediaTagsFeed.tagName;
                                outputFormat = recentMediaTagsFeed.outputFormat;
                                outputPath = recentMediaTagsFeed.outputPath;

                                GetRecentMediaTags getRecentMediaTags = 
                                        new GetRecentMediaTags();
                                    
                                getRecentMediaTags.setTagName(tagName);
                                getRecentMediaTags.setUserApiKey(apiKey);
                                getRecentMediaTags.setFileOutputFormat(outputFormat);
                                getRecentMediaTags.setFileOutputPath(outputPath);
                                getRecentMediaTags.execute();
                                break;
                            case "searchTags":
                                // Search for tags by name
                                SearchTags searchTagsFeed
                                        = (SearchTags) subCommand;

                                tagName = searchTagsFeed.tagName;
                                outputFormat = searchTagsFeed.outputFormat;
                                outputPath = searchTagsFeed.outputPath;

                                SearchTags searchTags = new SearchTags();
                                searchTags.setTagName(tagName);
                                searchTags.setUserApiKey(apiKey);
                                searchTags.setFileOutputFormat(outputFormat);
                                searchTags.setFileOutputPath(outputPath);
                                searchTags.execute();
                                break;
                            case "getLocationInfo":
                                // Get information about a location.
                                GetLocationInfo locationInfoFeed
                                        = (GetLocationInfo) subCommand;

                                locationId = locationInfoFeed.locationId;
                                outputFormat = locationInfoFeed.outputFormat;
                                outputPath = locationInfoFeed.outputPath;

                                GetLocationInfo getLocationInfo = 
                                        new GetLocationInfo();
                                getLocationInfo.setLocationId(locationId);
                                getLocationInfo.setUserApiKey(apiKey);
                                getLocationInfo.
                                        setFileOutputFormat(outputFormat);
                                getLocationInfo.setFileOutputPath(outputPath);
                                getLocationInfo.execute();
                                break;
                            case "getRecentMediaByLocation":
                                // Get a list of recent media objects 
                                // from a given location.
                                GetRecentMediaByLocation 
                                        recentMediaByLocationFeed
                                        = (GetRecentMediaByLocation) subCommand;

                                locationId = recentMediaByLocationFeed.
                                        locationId;
                                outputFormat = recentMediaByLocationFeed.
                                        outputFormat;
                                outputPath = recentMediaByLocationFeed.
                                        outputPath;

                                GetRecentMediaByLocation 
                                        getRecentMediaByLocation = 
                                        new GetRecentMediaByLocation();
                                
                                getRecentMediaByLocation.
                                        setLocationId(locationId);
                                getRecentMediaByLocation.setUserApiKey(apiKey);
                                getRecentMediaByLocation.
                                        setFileOutputFormat(outputFormat);
                                getRecentMediaByLocation.
                                        setFileOutputPath(outputPath);
                                getRecentMediaByLocation.execute();
                                break;
                            case "searchLocation":
                                // Search for a location 
                                // by geographic coordinate.
                                SearchLocation searchLocationFeed
                                        = (SearchLocation) subCommand;

                                latitude = searchLocationFeed.latitude;
                                longitude = searchLocationFeed.longitude;
                                outputFormat = searchLocationFeed.outputFormat;
                                outputPath = searchLocationFeed.outputPath;

                                SearchLocation searchLocation = 
                                        new SearchLocation();
                                searchLocation.setUserApiKey(apiKey);
                                searchLocation.setLatitude(latitude);
                                searchLocation.setLongitude(longitude);
                                searchLocation.
                                        setFileOutputFormat(outputFormat);
                                searchLocation.
                                        setFileOutputPath(outputPath);
                                searchLocation.execute();
                                break;
                        }
                    } else {
                        log.info("User not authorized!");
                    }
                }
            }

        } catch (ParameterException ex) {
            log.info(ex.getMessage());
            rootCommander.usage();
        }
    }

    private static Object getSubCommand(String parsedCommand) {
        Object obj = null;
        try {
            obj = isakCommander.getCommands().get(parsedCommand).getObjects()
                    .get(0);
        } catch (ParameterException | NullPointerException ex) {

        }
        return obj;
    }

    private static Object getRootOptions(String parsedCommand) {
        Object obj = null;
        try {
            obj = rootCommander.getCommands().get(parsedCommand).getObjects()
                    .get(0);
        } catch (ParameterException | NullPointerException ex) {

        }
        return obj;
    }

    private static boolean isAuthorized() {
        return authorize;
    }

    private static boolean authorizeUser() {
        if (isAuthorized()) {
            return true;
        }
        try {
            if (!setCredentials()) {
                System.err.println("Credentials not provided!");
                return false;
            }
            authorize = true;

        } catch (IOException ex) {
            System.err.println("Cannot read isak.properties file!");
            authorize = false;
        }
        return true;
    }

    private static boolean setCredentials() throws IOException {
        if (!rootCommander.getParsedCommand().equals("isak")) {
            System.err.println("Invalid Command: "
                    + rootCommander.getParsedCommand());
            return false;
        }
        if (isakCommand.getApiKey() == null) {
            String env_var = System.getenv("ISAK_CONF");
            if (env_var == null || env_var.isEmpty()) {
                log.info("Environment variable not set. ISAK_CONF");
                return false;
            }
            File propConfFile = new File(env_var + File.separator
                    + "isak.properties");
            if (!propConfFile.exists()) {
                log.info("isak.properties file does not exist in: "
                        + env_var);
                return false;
            }
            Properties prop = new Properties();
            try (InputStream propInstream = new FileInputStream(propConfFile)) {
                prop.load(propInstream);
            }
            isakCommand.setApiKey(prop.getProperty("apiKey").trim());
            apiKey = isakCommand.getApiKey();
            authorize = true;
        }
        return true;
    }
}
