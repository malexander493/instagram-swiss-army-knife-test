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
import org.apache.commons.lang3.StringEscapeUtils;
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
public class GetMediaInfo {

    private static final Logger log = LogManager.getRootLogger();
    private static String userApiKey = "";
    private static String mediaId = "";
    private static String fileOutputFormat = "";
    private static String fileOutputPath = "";

    public static void get(String[] args) throws IOException {

        userApiKey = StringEscapeUtils.escapeJava(args[0]);
        mediaId = StringEscapeUtils.escapeJava(args[1]);
        fileOutputFormat = StringEscapeUtils.escapeJava(args[2]);
        fileOutputPath = StringEscapeUtils.escapeJava(args[3]);

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
                }
            }

            Helpers.showRateLimitStatus(mediaInfo.getAPILimitStatus(),
                    mediaInfo.getRemainingLimitStatus());

        } catch (InstagramException | IllegalArgumentException ex) {
            throw new InstagramException(ex.getMessage());
        }
        log.info("!!! DONE !!!");
    }
}
