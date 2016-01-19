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
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;

/**
 * Get current popular media.
 *
 */
@Parameters(commandNames = "getPopularMedia",  
        commandDescription = "Get current popular media.")
public class GetPopularMedia extends BaseCommand {
    
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

        try (PrintWriter writer = new PrintWriter(fileOutputPath
                + "/popularMedia", "UTF-8")) {

            MediaFeed popularMediaFeed = instagram.getPopularMedia();
            List<MediaFeedData> mediaFeedsList = popularMediaFeed.getData();

            for (MediaFeedData mediaData : mediaFeedsList) {

                if ("json".equals(fileOutputFormat.toLowerCase())) {

                    String json = new Gson().toJson(mediaData);
                    writer.println(json);
                }
            }
            writer.close();

            log.info("Total Media collected: " + mediaFeedsList.size());
            Helpers.showRateLimitStatus(popularMediaFeed.getAPILimitStatus(),
                                popularMediaFeed.getRemainingLimitStatus());
            log.info("!!! DONE !!!");
        } catch (InstagramException ex) {
            try {
                throw new InstagramException(ex.getMessage());
            } catch (InstagramException ex1) {
                java.util.logging.Logger.getLogger(
                        GetPopularMedia.class.getName())
                        .log(Level.SEVERE, null, ex1);
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(
                    GetPopularMedia.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}
