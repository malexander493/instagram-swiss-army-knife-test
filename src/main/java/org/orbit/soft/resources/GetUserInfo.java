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
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.PrintWriter;
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
public class GetUserInfo {
    
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
        
        Instagram instagram = new Instagram(userApiKey);

        try {
            UserInfo userInfo = instagram.getUserInfo(userId);

            UserInfoData userData = userInfo.getData();
            if ("console".equals(fileOutputFormat.toLowerCase()) || 
                "cj".equals(fileOutputFormat.toLowerCase())) {
                
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

            if ("json".equals(fileOutputFormat.toLowerCase()) || 
                "cj".equals(fileOutputFormat.toLowerCase())) {

                
                try ( 
                        PrintWriter writer = new PrintWriter(fileOutputPath
                                + "/" + userId + "_info", "UTF-8")) {
                    String json = new Gson().toJson(userData);
                    writer.println(json);
                    writer.close();
                }
            }
            
            log.info("!!! DONE !!!");
            Helpers.showRateLimitStatus(userInfo.getAPILimitStatus(), 
                    userInfo.getRemainingLimitStatus());

        } catch (InstagramException ex) {
            throw new InstagramException(ex.getMessage());
        }
         catch (JsonSyntaxException ex) {
            throw new InstagramException("Invalid userId is provided.");
        }
    }
}