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
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinstagram.Instagram;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;

/**
 * Get a list of recent media objects from a given location.
 *
 */
@Parameters(commandNames = "getRecentMediaByLocation",  
        commandDescription = "Get a list of recent media objects "
                + "from a given location.")
public class GetRecentMediaByLocation extends BaseCommand {
    
    @Parameter(names = "-locationId", required = true, 
            description = "Location Id to get media.")
	public String locationId;
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json)")
	public String outputFormat;

    private static final Logger log = LogManager.getRootLogger();
    private static String userApiKey = "";
    private static String fileOutputFormat = "";
    private static String fileOutputPath = "";

    public void setUserApiKey(String userApiKey) {
        this.userApiKey = userApiKey;
    }

    public String getUserApiKey() {
        return userApiKey;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setFileOutputFormat(String fileOutputFormat) {
        this.fileOutputFormat = fileOutputFormat;
    }

    public String getFileOutputFormat() {
        return fileOutputFormat;
    }

    public void setFileOutputPath(String fileOutputPath) {
        this.fileOutputPath = fileOutputPath;
    }

    public String getFileOutputPath() {
        return fileOutputPath;
    }

    @Override
    public void execute() {


        Instagram instagram = new Instagram(userApiKey);

        int mediaCount = 0;

        try (
                // put JSON in a file
                PrintWriter writer = new PrintWriter(fileOutputPath + "/"
                        + locationId + "_recent_media_by_location", "UTF-8")) {

            MediaFeed locationMediaFeed = instagram.
                    getRecentMediaByLocation(locationId);
            List<MediaFeedData> mediaFeedsList = locationMediaFeed.getData();

            if (mediaFeedsList.size() > 0) {

                log.info("Total media found in this call: "
                        + mediaFeedsList.size());

                Helpers.showRateLimitStatus(locationMediaFeed.
                        getAPILimitStatus(),
                        locationMediaFeed.getRemainingLimitStatus());

                mediaCount += mediaFeedsList.size();

                for (MediaFeedData mediaData : mediaFeedsList) {

                    if ("json".equals(fileOutputFormat.toLowerCase())) {

                        String json = new Gson().toJson(mediaData);
                        writer.println(json);
                    }

                }
                // remove elements from list for next chunk
                mediaFeedsList.clear();

                MediaFeed recentLocationMediaNextPage = instagram.
                        getRecentMediaNextPage(locationMediaFeed.
                                getPagination());

                while (recentLocationMediaNextPage.getPagination().
                        getNextUrl() != null) {

                    mediaFeedsList.addAll(recentLocationMediaNextPage.
                            getData());

                    log.info("Total media found in this call: "
                            + mediaFeedsList.size());

                    mediaCount += mediaFeedsList.size();

                    for (MediaFeedData mediaData : mediaFeedsList) {

                        if ("json".equals(fileOutputFormat.toLowerCase())) {

                            String json = new Gson().toJson(mediaData);
                            writer.println(json);
                        }

                    }
                    // remove elements from list for next chunk
                    mediaFeedsList.clear();

                    recentLocationMediaNextPage = instagram.
                            getRecentMediaNextPage(recentLocationMediaNextPage.
                                    getPagination());

                    log.info("Total media collected: " + mediaCount);
                    if (recentLocationMediaNextPage.
                            getRemainingLimitStatus() > 0) {
                        
                        Helpers.showRateLimitStatus(recentLocationMediaNextPage.
                                getAPILimitStatus(),
                                recentLocationMediaNextPage.
                                getRemainingLimitStatus());
                    }
                }
                writer.close();
                log.info("!!! DONE !!!");
            } else {
                log.info("No recent media found against provided location id");
            }
        } catch (InstagramException ex) {
            try {
                throw new InstagramException(ex.getMessage());
            } catch (InstagramException ex1) {
                java.util.logging.Logger.getLogger(
                        GetRecentMediaByLocation.class.getName())
                        .log(Level.SEVERE, null, ex1);
            }
        } catch (FileNotFoundException ex) {
            try {
                throw new FileNotFoundException(ex.getMessage());
            } catch (FileNotFoundException ex1) {
                java.util.logging.Logger.getLogger(
                        GetRecentMediaByLocation.class.getName())
                        .log(Level.SEVERE, null, ex1);
            }
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(
                    GetRecentMediaByLocation.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}
