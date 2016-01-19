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
import com.google.gson.JsonSyntaxException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinstagram.Instagram;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.entity.users.basicinfo.UserInfoData;
import org.jinstagram.exceptions.InstagramException;

/**
 * Get basic information about a user.
 *
 */
@Parameters(commandNames = "getUserInfo",
        commandDescription = "Get basic information about a user.")
public class GetUserInfo extends BaseCommand {

    @Parameter(names = "-uid", required = true,
            description = "User ID to get information for. ")
    public String userId;

    @Parameter(names = "-of", required = true,
            description = "Output format (json, console, cj(console an json))")
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

        Instagram instagram = new Instagram(userApiKey);

        try {
            UserInfo userInfo = instagram.getUserInfo(userId);

            UserInfoData userData = userInfo.getData();
            if ("console".equals(fileOutputFormat.toLowerCase())
                    || "cj".equals(fileOutputFormat.toLowerCase())) {

                log.info("");
                log.info("id: " + userData.getId());
                log.info("username: " + userData.getUsername());
                log.info("full_name: " + userData.getFullName());
                log.info("profile_picture: "
                        + userData.getProfilePicture());
                log.info("bio: " + userData.getBio());
                log.info("website: " + userData.getWebsite());
                log.info("media_count: "
                        + userData.getCounts().getMedia());
                log.info("follows: " + userData.getCounts().getFollows());
                log.info("followed_by: "
                        + userData.getCounts().getFollowedBy());
            }

            if ("json".equals(fileOutputFormat.toLowerCase())
                    || "cj".equals(fileOutputFormat.toLowerCase())) {

                try (
                        PrintWriter writer = new PrintWriter(fileOutputPath
                                + "/" + userId + "_info", "UTF-8")) {
                    String json = new Gson().toJson(userData);
                    writer.println(json);
                    writer.close();
                } catch (FileNotFoundException | 
                        UnsupportedEncodingException ex) {
                    java.util.logging.Logger.getLogger(
                            GetUserInfo.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }

            log.info("!!! DONE !!!");
            Helpers.showRateLimitStatus(userInfo.getAPILimitStatus(),
                    userInfo.getRemainingLimitStatus());

        } catch (InstagramException ex) {
            try {
                throw new InstagramException(ex.getMessage());
            } catch (InstagramException ex1) {
                java.util.logging.Logger.getLogger(GetUserInfo.class.getName())
                        .log(Level.SEVERE, null, ex1);
            }
        } catch (JsonSyntaxException ex) {
            try {
                throw new InstagramException("Invalid userId is provided.");
            } catch (InstagramException ex1) {
                java.util.logging.Logger.getLogger(GetUserInfo.class.getName())
                        .log(Level.SEVERE, null, ex1);
            }
        }
    }
}
