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
import org.jinstagram.entity.users.feed.UserFeed;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

/**
 * Search for a user by name.
 * 
 * 
 */
public class SearchUser {

    private static final Logger log = LogManager.getRootLogger();

    private static String userApiKey = "";
    private static String query = "";
    private static String fileOutputFormat = "";
    private static String fileOutputPath = "";

    public static void get(String[] args) throws IOException {

        userApiKey = StringEscapeUtils.escapeJava(args[0]);
        query = StringEscapeUtils.escapeJava(args[1]);
        fileOutputFormat = StringEscapeUtils.escapeJava(args[2]);
        fileOutputPath = StringEscapeUtils.escapeJava(args[3]);

        Instagram instagram = new Instagram(userApiKey);

        try {

            PrintWriter writer = null;
            if ("json".equals(fileOutputFormat.toLowerCase())
                    || "cj".equals(fileOutputFormat.toLowerCase())) {
                
                writer = new PrintWriter(fileOutputPath
                        + "/" + query + "_search_user", "UTF-8");
            }

            UserFeed userFeed = instagram.searchUser(query);

            List<UserFeedData> userList = userFeed.getUserList();

            for (UserFeedData userData : userList) {

                if ("console".equals(fileOutputFormat.toLowerCase())
                        || "cj".equals(fileOutputFormat.toLowerCase())) {

                    log.info("");
                    log.info("id: " + userData.getId());
                    log.info("username: " + userData.getUserName());
                    log.info("full_name: " + userData.getFullName());
                    log.info("profile_picture: "
                            + userData.getProfilePictureUrl());
                    log.info("bio: " + userData.getBio());
                    log.info("website: " + userData.getWebsite());
                }

                if ("json".equals(fileOutputFormat.toLowerCase())
                        || "cj".equals(fileOutputFormat.toLowerCase())) {

                    String json = new Gson().toJson(userData);
                    if (writer != null)
                        writer.println(json);
                }
            }
            if (writer != null)
                writer.close();
            log.info("");
            log.info("Total Users collected: " + userList.size());

            Helpers.showRateLimitStatus(userFeed.getAPILimitStatus(),
                    userFeed.getRemainingLimitStatus());

        } catch (InstagramException ex) {
            throw new InstagramException(ex.getMessage());
        }
    }
}