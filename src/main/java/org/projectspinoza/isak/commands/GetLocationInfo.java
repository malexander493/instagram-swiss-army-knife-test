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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinstagram.Instagram;
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.locations.LocationInfo;
import org.jinstagram.exceptions.InstagramException;

/**
 * Get information about a location.
 *
 */
@Parameters(commandNames = "getLocationInfo",  
        commandDescription = "Get information about a location.")
public class GetLocationInfo extends BaseCommand {
    
    @Parameter(names = "-locationId", required = true, 
            description = "Location Id to get information for.")
	public String locationId;
    
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

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationId() {
        return locationId;
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
            
            LocationInfo locationInfo = instagram.getLocationInfo(locationId);
            if (! "".equals(locationInfo.getLocationData().getId())) { 
            Location location = locationInfo.getLocationData();
            
            if ("console".equals(fileOutputFormat.toLowerCase())
                    || "cj".equals(fileOutputFormat.toLowerCase())) {
                log.info("id: " + location.getId());
                log.info("name: " + location.getName());
                log.info("latitude: " + location.getLatitude());
                log.info("longitude: " + location.getLongitude());
            }

            if ("json".equals(fileOutputFormat.toLowerCase())
                    || "cj".equals(fileOutputFormat.toLowerCase())) {

                try (
                    // put entities JSON in a file
                    PrintWriter writer = new PrintWriter(fileOutputPath
                                + "/" + locationId + "_location_info", 
                            "UTF-8")) {
                    String json = new Gson().toJson(location);
                    writer.println(json);
                    writer.close();
                } catch (FileNotFoundException | 
                        UnsupportedEncodingException ex) {
                    java.util.logging.Logger.getLogger(
                            GetLocationInfo.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
            
            Helpers.showRateLimitStatus(locationInfo.getAPILimitStatus(),
                                locationInfo.getRemainingLimitStatus());
            log.info("!!! DONE !!!");
            } else {
                log.info("No information found agianst provided location id");
            }
        } catch (InstagramException ex) {
            try {
                throw new InstagramException(ex.getMessage());
            } catch (InstagramException ex1) {
                java.util.logging.Logger.getLogger(
                        GetLocationInfo.class.getName())
                        .log(Level.SEVERE, null, ex1);
            }
        }  catch (JsonSyntaxException ex) {
            try {
                throw new IOException("Provide location id is not working");
            } catch (IOException ex1) {
                java.util.logging.Logger.getLogger(
                        GetLocationInfo.class.getName())
                        .log(Level.SEVERE, null, ex1);
            }
        }
    }
}