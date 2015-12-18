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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.orbit.soft.resources.GetLocationInfo;
import org.orbit.soft.resources.GetMediaComments;
import org.orbit.soft.resources.GetMediaInfo;
import org.orbit.soft.resources.GetPopularMedia;
import org.orbit.soft.resources.GetRecentMediaByLocation;
import org.orbit.soft.resources.GetRecentMediaFeed;
import org.orbit.soft.resources.GetRecentMediaTags;
import org.orbit.soft.resources.GetTagInfo;
import org.orbit.soft.resources.GetUserFollowList;
import org.orbit.soft.resources.GetUserFollowedByList;
import org.orbit.soft.resources.GetUserInfo;
import org.orbit.soft.resources.GetUserLikes;
import org.orbit.soft.resources.SearchLocation;
import org.orbit.soft.resources.SearchMediaByLatLng;
import org.orbit.soft.resources.SearchTags;
import org.orbit.soft.resources.SearchUser;

/**
 *
 * Takes arguments from main class then analyze and assign them to relevant
 * resources.
 *
 */
public class CommandExecutor {

    private static final Logger log = LogManager.getRootLogger();

    private static CommandAndControl isakCommand;
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

        isakCommand = new CommandAndControl();
        rootCommander = null;
        try {

            rootCommander = new JCommander();
            rootCommander.addCommand("isak", isakCommand);
            subCommander = rootCommander.getCommands().get("isak");
            isakCommander = subCommander;
            isakCommander.addCommand(new getUserInfo());
            isakCommander.addCommand(new getRecentMediaFeed());
            isakCommander.addCommand(new searchUser());
            isakCommander.addCommand(new getUserFollowList());
            isakCommander.addCommand(new getUserFollowedByList());
            isakCommander.addCommand(new getMediaInfo());
            isakCommander.addCommand(new searchMediaByLatLng());
            isakCommander.addCommand(new getPopularMedia());
            isakCommander.addCommand(new getMediaComments());
            isakCommander.addCommand(new getUserLikes());
            isakCommander.addCommand(new getTagInfo());
            isakCommander.addCommand(new getRecentMediaTags());
            isakCommander.addCommand(new searchTags());
            isakCommander.addCommand(new getLocationInfo());
            isakCommander.addCommand(new getRecentMediaByLocation());
            isakCommander.addCommand(new searchLocation());

            
            rootCommander.parse(args);

            String rootParsedCommand = rootCommander.getParsedCommand();

            Object rootSubCommand = getRootOptions("isak");

            Boolean help = false;
            String specificHelp = null;
            if (rootParsedCommand.equals("isak")) {
                CommandAndControl rootIsakCommand = (CommandAndControl) 
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
                                getUserInfo userInfo = (getUserInfo) subCommand;
                                userId = userInfo.userId;
                                outputFormat = userInfo.outputFormat;
                                outputPath = userInfo.outputPath;

                                String[] userInfoArgs = {apiKey, userId,
                                    outputFormat, outputPath};

                                try {
                                    GetUserInfo.get(userInfoArgs);
                                } catch (IOException ex) {
                                    log.info("getUserInfo Response: "
                                            + ex.getMessage());
                                }
                                break;
                            case "getRecentMediaFeed":
                                // Get Recent Media Feed of a user.
                                getRecentMediaFeed recentMediaFeed
                                        = (getRecentMediaFeed) subCommand;

                                userId = recentMediaFeed.userId;
                                outputFormat = recentMediaFeed.outputFormat;
                                outputPath = recentMediaFeed.outputPath;

                                String[] recentMediaFeedArgs = {apiKey, userId,
                                    outputFormat, outputPath};

                                try {
                                    GetRecentMediaFeed.get(recentMediaFeedArgs);
                                } catch (IOException ex) {
                                    log.info("getRecentMediaFeed Response: "
                                            + ex.getMessage());
                                }
                                break;
                            case "searchUser":
                                // Search for a user by name.
                                searchUser searchUserData = (searchUser) subCommand;
                                query = searchUserData.query;
                                outputFormat = searchUserData.outputFormat;
                                outputPath = searchUserData.outputPath;

                                String[] searchUserArgs = {apiKey, query,
                                    outputFormat, outputPath};

                                try {
                                    SearchUser.get(searchUserArgs);
                                } catch (IOException ex) {
                                    log.info("searchUser Response: "
                                            + ex.getMessage());
                                }
                                break;
                            case "getUserFollowList":
                                // Get the list of users this user follows.
                                getUserFollowList userFollowList
                                        = (getUserFollowList) subCommand;

                                userId = userFollowList.userId;
                                outputFormat = userFollowList.outputFormat;
                                outputPath = userFollowList.outputPath;

                                String[] userFollowListArgs = {apiKey, userId,
                                    outputFormat, outputPath};

                                try {
                                    GetUserFollowList.get(userFollowListArgs);
                                } catch (IOException ex) {
                                    log.info("getuserfollowlist Response: "
                                            + ex.getMessage());
                                }
                                break;
                            case "getUserFollowedByList":
                                // Get the list of users this user is followed by.
                                getUserFollowedByList userFollowedByList
                                        = (getUserFollowedByList) subCommand;

                                userId = userFollowedByList.userId;
                                outputFormat = userFollowedByList.outputFormat;
                                outputPath = userFollowedByList.outputPath;

                                String[] userFollowedByListArgs = {apiKey, userId,
                                    outputFormat, outputPath};

                                try {
                                    GetUserFollowedByList.
                                            get(userFollowedByListArgs);
                                } catch (IOException ex) {
                                    log.info("getUserFollowedByList Response: "
                                            + ex.getMessage());
                                }
                                break;
                            case "getMediaInfo":
                                // Get information about a media object.
                                getMediaInfo mediaInfo
                                        = (getMediaInfo) subCommand;

                                mediaId = mediaInfo.mediaId;
                                outputFormat = mediaInfo.outputFormat;
                                outputPath = mediaInfo.outputPath;

                                String[] mediaInfoArgs = {apiKey, mediaId,
                                    outputFormat, outputPath};

                                try {
                                    GetMediaInfo.
                                            get(mediaInfoArgs);
                                } catch (IOException ex) {
                                    log.info("GetMediaInfo Response: "
                                            + ex.getMessage());
                                }
                                break;
                            case "searchMediaByLatLng":
                                // Search for media in a given area.
                                searchMediaByLatLng searchMediaByLatLngFeed
                                        = (searchMediaByLatLng) subCommand;

                                latitude = searchMediaByLatLngFeed.latitude;
                                longitude = searchMediaByLatLngFeed.longitude;
                                outputFormat = searchMediaByLatLngFeed.outputFormat;
                                outputPath = searchMediaByLatLngFeed.outputPath;

                                String[] searchMediaByLatLngFeedArgs = {apiKey,
                                    latitude, longitude, outputFormat, outputPath};

                                try {
                                    SearchMediaByLatLng.
                                            get(searchMediaByLatLngFeedArgs);
                                } catch (IOException ex) {
                                    log.info("searchMediaByLatLng Response: "
                                            + ex.getMessage());
                                }
                                break;
                            case "getPopularMedia":
                                // Get current popular media.
                                getPopularMedia popularMediaFeed
                                        = (getPopularMedia) subCommand;

                                outputFormat = popularMediaFeed.outputFormat;
                                outputPath = popularMediaFeed.outputPath;

                                String[] popularMediaFeedArgs = {apiKey,
                                    outputFormat, outputPath};

                                try {
                                    GetPopularMedia.
                                            get(popularMediaFeedArgs);
                                } catch (IOException ex) {
                                    log.info("getPopularMedia Response: "
                                            + ex.getMessage());
                                }
                                break;
                            case "getMediaComments":
                                // Get a list of recent comments on a media object.
                                getMediaComments mediaCommentsFeed
                                        = (getMediaComments) subCommand;

                                mediaId = mediaCommentsFeed.mediaId;
                                outputFormat = mediaCommentsFeed.outputFormat;
                                outputPath = mediaCommentsFeed.outputPath;

                                String[] mediaCommentsFeedArgs = {apiKey, mediaId,
                                    outputFormat, outputPath};

                                try {
                                    GetMediaComments.
                                            get(mediaCommentsFeedArgs);
                                } catch (IOException ex) {
                                    log.info("getMediaComments Response: "
                                            + ex.getMessage());
                                }
                                break;
                            case "getUserLikes":
                                // Get a list of users who have liked this media.
                                getUserLikes userLikesFeed
                                        = (getUserLikes) subCommand;

                                mediaId = userLikesFeed.mediaId;
                                outputFormat = userLikesFeed.outputFormat;
                                outputPath = userLikesFeed.outputPath;

                                String[] userLikesFeedArgs = {apiKey, mediaId,
                                    outputFormat, outputPath};

                                try {
                                    GetUserLikes.
                                            get(userLikesFeedArgs);
                                } catch (IOException ex) {
                                    log.info("getUserLikes Response: "
                                            + ex.getMessage());
                                }
                                break;
                            case "getTagInfo":
                                // Get information about a tag object.
                                getTagInfo tagInfoFeed
                                        = (getTagInfo) subCommand;

                                tagName = tagInfoFeed.tagName;
                                outputFormat = tagInfoFeed.outputFormat;
                                outputPath = tagInfoFeed.outputPath;

                                String[] tagInfoFeedArgs = {apiKey, tagName,
                                    outputFormat, outputPath};

                                try {
                                    GetTagInfo.get(tagInfoFeedArgs);
                                } catch (IOException ex) {
                                    log.info("getTagInfo Response: "
                                            + ex.getMessage());
                                }
                                break;
                            case "getRecentMediaTags":
                                // Get a list of recently tagged media.
                                getRecentMediaTags recentMediaTagsFeed
                                        = (getRecentMediaTags) subCommand;

                                tagName = recentMediaTagsFeed.tagName;
                                outputFormat = recentMediaTagsFeed.outputFormat;
                                outputPath = recentMediaTagsFeed.outputPath;

                                String[] recentMediaTagsFeedArgs = {apiKey, tagName,
                                    outputFormat, outputPath};

                                try {
                                    GetRecentMediaTags.get(recentMediaTagsFeedArgs);
                                } catch (IOException ex) {
                                    log.info("getRecentMediaTags Response: "
                                            + ex.getMessage());
                                }
                                break;
                            case "searchTags":
                                // Search for tags by name
                                searchTags searchTagsFeed
                                        = (searchTags) subCommand;

                                tagName = searchTagsFeed.tagName;
                                outputFormat = searchTagsFeed.outputFormat;
                                outputPath = searchTagsFeed.outputPath;

                                String[] searchTagsFeedArgs = {apiKey, tagName,
                                    outputFormat, outputPath};

                                try {
                                    SearchTags.get(searchTagsFeedArgs);
                                } catch (IOException ex) {
                                    log.info("searchTags Response: "
                                            + ex.getMessage());
                                }
                                break;
                            case "getLocationInfo":
                                // Get information about a location.
                                getLocationInfo locationInfoFeed
                                        = (getLocationInfo) subCommand;

                                locationId = locationInfoFeed.locationId;
                                outputFormat = locationInfoFeed.outputFormat;
                                outputPath = locationInfoFeed.outputPath;

                                String[] locationInfoFeedArgs = {apiKey, locationId,
                                    outputFormat, outputPath};

                                try {
                                    GetLocationInfo.get(locationInfoFeedArgs);
                                } catch (IOException ex) {
                                    log.info("getLocationInfo Response: "
                                            + ex.getMessage());
                                }
                                break;
                            case "getRecentMediaByLocation":
                            // Get a list of recent media objects 
                                // from a given location.
                                getRecentMediaByLocation recentMediaByLocationFeed
                                        = (getRecentMediaByLocation) subCommand;

                                locationId = recentMediaByLocationFeed.locationId;
                                outputFormat = recentMediaByLocationFeed.outputFormat;
                                outputPath = recentMediaByLocationFeed.outputPath;

                                String[] recentMediaByLocationFeedArgs = {apiKey,
                                    locationId, outputFormat, outputPath};

                                try {
                                    GetRecentMediaByLocation.
                                            get(recentMediaByLocationFeedArgs);
                                } catch (IOException ex) {
                                    log.info("getLocationInfo Response: "
                                            + ex.getMessage());
                                }
                                break;
                            case "searchLocation":
                                // Search for a location by geographic coordinate.
                                searchLocation searchLocationFeed
                                        = (searchLocation) subCommand;

                                latitude = searchLocationFeed.latitude;
                                longitude = searchLocationFeed.longitude;
                                outputFormat = searchLocationFeed.outputFormat;
                                outputPath = searchLocationFeed.outputPath;

                                String[] searchLocationFeedArgs = {apiKey,
                                    latitude, longitude, outputFormat, outputPath};

                                try {
                                    SearchLocation.
                                            get(searchLocationFeedArgs);
                                } catch (IOException ex) {
                                    log.info("searchLocation Response: "
                                            + ex.getMessage());
                                }
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
