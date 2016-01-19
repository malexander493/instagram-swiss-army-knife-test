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
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinstagram.Instagram;
import org.jinstagram.entity.tags.TagMediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;

/**
 * Get a list of recently tagged media.
 *
 */
@Parameters(commandNames = "getRecentMediaTags",  
        commandDescription = "Get a list of recently tagged media.")
public class GetRecentMediaTags extends BaseCommand {
    
    @Parameter(names = "-tag", required = true, 
            description = "Tag name to get information for.")
	public String tagName;
    
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

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
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

    /**
     *
     */
    @Override
    public void execute() {

        Instagram instagram = new Instagram(userApiKey);

        int mediaCount = 0;

        try (
                // put JSON in a file
                PrintWriter writer = new PrintWriter(fileOutputPath + "/"
                        + tagName + "_recent_taged_media", "UTF-8")) {

            TagMediaFeed tagMediaFeed = instagram.getRecentMediaTags(tagName);
            List<MediaFeedData> mediaFeedsList = tagMediaFeed.getData();

            if (mediaFeedsList.size() > 0) {

                log.info("Total media tags found in this call: "
                        + mediaFeedsList.size());

                Helpers.showRateLimitStatus(tagMediaFeed.getAPILimitStatus(),
                        tagMediaFeed.getRemainingLimitStatus());

                mediaCount += mediaFeedsList.size();

                for (MediaFeedData mediaData : mediaFeedsList) {

                    if ("json".equals(fileOutputFormat.toLowerCase())) {

                        String json = new Gson().toJson(mediaData);
                        writer.println(json);
                    }
                }
                // remove elements from list for next chunk
                mediaFeedsList.clear();

                TagMediaFeed recentTagMediaNextPage = instagram.
                        getTagMediaInfoNextPage(tagMediaFeed.getPagination());

                while (recentTagMediaNextPage.getPagination().
                        getNextUrl() != null) {

                    mediaFeedsList.addAll(recentTagMediaNextPage.getData());

                    log.info("Total media tags found in this call: "
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

                    recentTagMediaNextPage = instagram.getTagMediaInfoNextPage(
                            recentTagMediaNextPage.getPagination());

                    log.info("Total media collected: " + mediaCount);
                    if (recentTagMediaNextPage.getRemainingLimitStatus() > 0) {
                        Helpers.showRateLimitStatus(recentTagMediaNextPage.
                                getAPILimitStatus(),
                                recentTagMediaNextPage.
                                        getRemainingLimitStatus());
                    }
                }
                writer.close();
                log.info("!!! DONE !!!");
            } else {
                log.info("No media tags found against provided tag.");
            }
        } catch (InstagramException ex) {
            try {
                throw new InstagramException(ex.getMessage());
            } catch (InstagramException ex1) {
                java.util.logging.Logger.getLogger(
                        GetRecentMediaTags.class.getName())
                        .log(Level.SEVERE, null, ex1);
            }
        } catch (FileNotFoundException ex) {
            try {
                throw new FileNotFoundException(ex.getMessage());
            } catch (FileNotFoundException ex1) {
                java.util.logging.Logger.getLogger(
                        GetRecentMediaTags.class.getName())
                        .log(Level.SEVERE, null, ex1);
            }
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(
                    GetRecentMediaTags.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}