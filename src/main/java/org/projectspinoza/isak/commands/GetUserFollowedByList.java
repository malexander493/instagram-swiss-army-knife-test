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
import org.jinstagram.entity.users.feed.UserFeed;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

/**
 * Get the list of users this user is followed by.
 *
 */
@Parameters(commandNames = "getUserFollowedByList",  
        commandDescription = "Get the list of users this user is followed by.")
public class GetUserFollowedByList extends BaseCommand {
    @Parameter(names = "-uid", required = true, 
            description = "User ID to get information for. ")
	public String userId;
    
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
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
        userId = this.getUserId();
        fileOutputFormat = this.getFileOutputFormat();
        fileOutputPath = this.getFileOutputPath();

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
            try {
                throw new InstagramException(ex.getMessage());
            } catch (InstagramException ex1) {
                java.util.logging.Logger.getLogger(
                        GetUserFollowedByList.class.getName())
                        .log(Level.SEVERE, null, ex1);
            }
        } catch (FileNotFoundException ex) {
            try {
                throw new FileNotFoundException(ex.getMessage());
            } catch (FileNotFoundException ex1) {
                java.util.logging.Logger.getLogger(
                        GetUserFollowedByList.class.getName())
                        .log(Level.SEVERE, null, ex1);
            }
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(
                    GetUserFollowedByList.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}
