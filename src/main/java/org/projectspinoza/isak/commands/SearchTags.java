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
import org.jinstagram.entity.tags.TagInfoData;
import org.jinstagram.entity.tags.TagSearchFeed;
import org.jinstagram.exceptions.InstagramException;

/**
 * Search for tags by name - results are ordered first as an exact match, then
 * by popularity.
 *
 */
@Parameters(commandNames = "searchTags",  
        commandDescription = "Search for tags by name")
public class SearchTags extends BaseCommand {
    
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

    @Override
    public void execute() {

        Instagram instagram = new Instagram(userApiKey);

        try {

            PrintWriter writer = null;
            if ("json".equals(fileOutputFormat.toLowerCase())
                    || "cj".equals(fileOutputFormat.toLowerCase())) {

                writer = new PrintWriter(fileOutputPath
                        + "/" + tagName + "_tag_search_results",
                        "UTF-8");
            }

            TagSearchFeed tagsFeed = instagram.searchTags(tagName);
            List<TagInfoData> tagsList = tagsFeed.getTagList();

            if (tagsList.size() > 0) {

                for (TagInfoData tagData : tagsList) {

                    if ("console".equals(fileOutputFormat.toLowerCase())
                            || "cj".equals(fileOutputFormat.toLowerCase())) {

                        log.info("name: " + tagData.getTagName());
                        log.info("media_count: " + tagData.getMediaCount());
                        log.info("");
                    }

                    if ("json".equals(fileOutputFormat.toLowerCase())
                            || "cj".equals(fileOutputFormat.toLowerCase())) {

                        String json = new Gson().toJson(tagData);
                        if (writer != null) {
                            writer.println(json);
                        }
                    }
                }
                if (writer != null) {
                    writer.close();
                }

                log.info("Total Media collected: " + tagsList.size());
                Helpers.showRateLimitStatus(tagsFeed.getAPILimitStatus(),
                        tagsFeed.getRemainingLimitStatus());
                log.info("!!! DONE !!!");
            } else {
                log.info("No media tags found against provided tag.");
            }
        } catch (InstagramException ex) {
            try {
                throw new InstagramException(ex.getMessage());
            } catch (InstagramException ex1) {
                java.util.logging.Logger.getLogger(
                        SearchTags.class.getName())
                        .log(Level.SEVERE, null, ex1);
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(
                    SearchTags.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}
