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
import org.jinstagram.entity.users.feed.UserFeed;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

/**
 * Get the list of users this user is followed by.
 *
 */
public class GetUserFollowedByList {

    private static final Logger log = LogManager.getRootLogger();

    private static String userApiKey = "";
    private static String userId = "";
    private static String fileOutputFormat = "";
    private static String fileOutputPath = "";

    public static void get(String[] args) throws IOException {

        userApiKey = StringEscapeUtils.escapeJava(args[0]);
        userId = StringEscapeUtils.escapeJava(args[1]);
        fileOutputFormat = StringEscapeUtils.escapeJava(args[2]);
        fileOutputPath = StringEscapeUtils.escapeJava(args[3]);

        // init Instagram
        Instagram instagram = new Instagram(userApiKey);

        int followersCount = 0;

        try (
                // put output in a file
                PrintWriter writer = new PrintWriter(fileOutputPath + "/"
                        + userId + "_followed_by", "UTF-8")) {

            UserFeed userFeed = instagram.getUserFollowedByList(userId);
            List<UserFeedData> userFeedsList = userFeed.getUserList();

            if (userFeedsList.size() > 0) {

                log.info("Total Followed by found in this call: "
                        + userFeedsList.size());

                Helpers.showRateLimitStatus(userFeed.getAPILimitStatus(),
                        userFeed.getRemainingLimitStatus());

                followersCount += userFeedsList.size();

                for (UserFeedData userData : userFeedsList) {

                    if ("json".equals(fileOutputFormat.toLowerCase())) {

                        String json = new Gson().toJson(userData);
                        writer.println(json);
                    }

                }
                // remove elements from list for next chunk
                userFeedsList.clear();

                // pagination
                UserFeed recentUserFollowNextPage = instagram.
                        getUserFollowListNextPage(userFeed.getPagination());

                while (recentUserFollowNextPage.getPagination().
                        getNextUrl() != null) {

                    userFeedsList.addAll(recentUserFollowNextPage.
                            getUserList());

                    log.info("Total Followed by found in this call: "
                            + userFeedsList.size());

                    followersCount += userFeedsList.size();

                    for (UserFeedData userData : userFeedsList) {

                        if ("json".equals(fileOutputFormat.toLowerCase())) {

                            String json = new Gson().toJson(userData);
                            writer.println(json);
                        }

                    }
                    // remove elements from list for next chunk
                    userFeedsList.clear();

                    recentUserFollowNextPage = instagram.
                            getUserFollowListNextPage(recentUserFollowNextPage.
                                    getPagination());

                    log.info("Total Followed by collected: " + followersCount);
                    if (recentUserFollowNextPage.
                            getRemainingLimitStatus() > 0) {
                        Helpers.showRateLimitStatus(recentUserFollowNextPage.
                                getAPILimitStatus(),
                                recentUserFollowNextPage.
                                getRemainingLimitStatus());
                    }
                }
                log.info("!!! DONE !!!");
            } else {
                log.info("No Followed by found against provided userid.");
            }

        } catch (InstagramException ex) {
            throw new InstagramException(ex.getMessage());
        } catch (FileNotFoundException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }
}
