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
package org.orbit.soft.resources;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
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
public class SearchTags {

    private static final Logger log = LogManager.getRootLogger();
    private static String userApiKey = "";
    private static String tagName;
    private static String fileOutputFormat = "";
    private static String fileOutputPath = "";

    public static void get(String[] args) throws IOException {

        userApiKey = StringEscapeUtils.escapeJava(args[0]);
        tagName = StringEscapeUtils.escapeJava(args[1]);
        fileOutputFormat = StringEscapeUtils.escapeJava(args[2]);
        fileOutputPath = StringEscapeUtils.escapeJava(args[3]);

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
            throw new InstagramException(ex.getMessage());
        }
    }
}
