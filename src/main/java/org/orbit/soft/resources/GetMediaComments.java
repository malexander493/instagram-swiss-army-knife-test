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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinstagram.Instagram;
import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.comments.MediaCommentsFeed;
import org.jinstagram.exceptions.InstagramException;

/**
 * Get a list of recent comments on a media object.
 *
 */
public class GetMediaComments {

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

        try (
                // put JSON in a file
                PrintWriter writer = new PrintWriter(fileOutputPath + "/"
                        + mediaId + "_media_comments", "UTF-8")) {

            MediaCommentsFeed commentsFeed = instagram.
                    getMediaComments(mediaId);
            List<CommentData> mediaFeedsList = commentsFeed.
                    getCommentDataList();

            if (mediaFeedsList.size() > 0) {

                for (CommentData mediaData : mediaFeedsList) {

                    if ("json".equals(fileOutputFormat.toLowerCase())) {

                        String json = new Gson().toJson(mediaData);
                        writer.println(json);
                    }
                }
                writer.close();

                log.info("Total media comments collected: " + 
                        mediaFeedsList.size());
                Helpers.showRateLimitStatus(commentsFeed.getAPILimitStatus(),
                        commentsFeed.getRemainingLimitStatus());
                log.info("!!! DONE !!!");
            } else {
                log.info("No media comments found against provided userid.");
            }
        } catch (InstagramException ex) {
            throw new InstagramException(ex.getMessage());
        } catch (FileNotFoundException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }
}
