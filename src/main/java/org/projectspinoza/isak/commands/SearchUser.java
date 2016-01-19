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
 * Search for a user by name.
 * 
 * 
 */
@Parameters(commandNames = "searchUser",  
        commandDescription = "Search for a user by name.")
public class SearchUser extends BaseCommand {
    @Parameter(names = "-q", required = true, 
            description = "Search for a user by name.")
	public String query;
    
    @Parameter(names = "-of", required = true,
            description = "Output format (json, console, cj(console and json))")
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

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
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
            try {
                throw new InstagramException(ex.getMessage());
            } catch (InstagramException ex1) {
                java.util.logging.Logger.getLogger(
                    SearchUser.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(
                    SearchUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}