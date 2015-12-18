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
import org.jinstagram.entity.common.User;
import org.jinstagram.entity.likes.LikesFeed;
import org.jinstagram.exceptions.InstagramException;

/**
 * Get a list of users who have liked this media.
 *
 */
public class GetUserLikes {
    
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

        try (PrintWriter writer = new PrintWriter(fileOutputPath
                + "/" + mediaId + "_media_likes", "UTF-8")) {

            LikesFeed userLikes = instagram.getUserLikes(mediaId);
            List<User> likeData = userLikes.getUserList();

            if (likeData.size() > 0) {

                for (User likeData1 : likeData) {
                    if ("json".equals(fileOutputFormat.toLowerCase())) {

                        String json = new Gson().toJson(likeData1);
                        writer.println(json);
                    }
                }
                writer.close();
                log.info("Total Likes collected: " + likeData.size());
                
                Helpers.showRateLimitStatus(userLikes.getAPILimitStatus(),
                                    userLikes.getRemainingLimitStatus());
                
                log.info("!!! DONE !!!");
            } else {
                log.info("No likes found against provided mediaId.");
            }
            
        } catch (InstagramException ex) {
            throw new InstagramException(ex.getMessage());
        }
    }
}
