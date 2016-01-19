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
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinstagram.Instagram;
import org.jinstagram.entity.media.MediaInfoFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;

/**
 * Get information about a media object.
 *
 */
@Parameters(commandNames = "getMediaInfo",  
        commandDescription = "Get information about a media object.")
public class GetMediaInfo extends BaseCommand {
    @Parameter(names = "-mid", required = true, 
            description = "Media ID to get information for. ")
	public String mediaId;
    
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

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaId() {
        return mediaId;
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

        userApiKey = this.getUserApiKey();
        mediaId = this.getMediaId();
        fileOutputFormat = this.getFileOutputFormat();
        fileOutputPath = this.getFileOutputPath();

        Instagram instagram = new Instagram(userApiKey);

        try {
            MediaInfoFeed mediaInfo = instagram.getMediaInfo(mediaId);

            MediaFeedData mediaData = mediaInfo.getData();

            if ("console".equals(fileOutputFormat.toLowerCase())
                    || "cj".equals(fileOutputFormat.toLowerCase())) {
                
                log.info("");
                log.info("id: " + mediaData.getId());
                log.info("CreatedTime: " + mediaData.getCreatedTime());
                log.info("ImageFilter: " + mediaData.getImageFilter());
                log.info("Link: "
                        + mediaData.getLink());
                log.info("Type: " + mediaData.getType());
                log.info("Location: "
                        + mediaData.getLocation());
                log.info("Tags: "
                        + mediaData.getTags());

            }
            if ("json".equals(fileOutputFormat.toLowerCase())
                    || "cj".equals(fileOutputFormat.toLowerCase())) {

                try ( // put entities JSON in a file
                        PrintWriter writer = new PrintWriter(fileOutputPath
                                + "/" + mediaId + "_media_info", "UTF-8")) {
                    String json = new Gson().toJson(mediaData);
                    writer.println(json);
                    writer.close();
                } catch (FileNotFoundException | 
                        UnsupportedEncodingException ex) {
                    java.util.logging.Logger.getLogger(
                            GetMediaInfo.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }

            Helpers.showRateLimitStatus(mediaInfo.getAPILimitStatus(),
                    mediaInfo.getRemainingLimitStatus());

        } catch (InstagramException | IllegalArgumentException ex) {
            try {
                throw new InstagramException(ex.getMessage());
            } catch (InstagramException ex1) {
                java.util.logging.Logger.getLogger(
                        GetMediaInfo.class.getName())
                        .log(Level.SEVERE, null, ex1);
            }
        }
        log.info("!!! DONE !!!");
    }
}
