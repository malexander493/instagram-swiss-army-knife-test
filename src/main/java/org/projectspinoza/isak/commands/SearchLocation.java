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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinstagram.Instagram;
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.locations.LocationSearchFeed;
import org.jinstagram.exceptions.InstagramException;

/**
 * Search for a location by geographic coordinate.
 *
 */
@Parameters(commandNames = "searchLocation",  
        commandDescription = "Search for a location by geographic coordinate.")
public class SearchLocation extends BaseCommand {
    
    @Parameter(names = "-lat", required = true, 
            description = "latitude of area")
	public String latitude;
    
    @Parameter(names = "-lng", required = true, 
            description = "longitude of area")
	public String longitude;
    
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

    private double latitudeD;
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLatitude() {
        return latitude;
    }
    
    private double longitudeD;
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitude() {
        return longitude;
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


        String latitudeStr = latitude.replace(".", "");
        latitudeStr = latitudeStr.replace("-", "");
        String longitudeStr = longitude.replace(".", "");
        longitudeStr = longitudeStr.replace("-", "");
        
        try {
            latitudeD = Double.parseDouble(latitude);
        } catch (Exception e) {
            try {
                throw new IOException("Latitude " + latitude
                        + " must be type of Double.");
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(
                        SearchMediaByLatLng.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }

        try {
            longitudeD = Double.parseDouble(longitude);
        } catch (Exception e) {
            try {
                throw new IOException("Longitude " + longitude
                        + " must be type of Double.");
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(
                        SearchMediaByLatLng.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }

        Instagram instagram = new Instagram(userApiKey);

        String fileName = "lat_lng_location_" + latitudeStr + "_"
                + longitudeStr;

        int mediaCount = 0;
        try {

            PrintWriter writer = null;
            if ("json".equals(fileOutputFormat.toLowerCase())
                    || "cj".equals(fileOutputFormat.toLowerCase())) {

                writer = new PrintWriter(fileOutputPath
                        + "/" + fileName, "UTF-8");
            }

            LocationSearchFeed locationFeed = instagram.
                    searchLocation(latitudeD, longitudeD);
            List<Location> locationFeedsList = locationFeed.getLocationList();

            if (locationFeedsList.size() > 0) {
                mediaCount += locationFeedsList.size();

                for (Location locationData : locationFeedsList) {

                    if ("console".equals(fileOutputFormat.toLowerCase())
                            || "cj".equals(fileOutputFormat.toLowerCase())) {

                        log.info("");
                        log.info("id: " + locationData.getId());
                        log.info("name: " + locationData.getName());
                        log.info("latitude: " + locationData.getLatitude());
                        log.info("longitude: " + locationData.getLongitude());
                    }
                    if ("json".equals(fileOutputFormat.toLowerCase())
                            || "cj".equals(fileOutputFormat.toLowerCase())) {

                        String json = new Gson().toJson(locationData);
                        if (writer != null) {
                            writer.println(json);
                        }
                    }
                }
                // remove elements from list for next chunk
                locationFeedsList.clear();
                if (writer != null) {
                    writer.close();
                }

                log.info("Total Locations collected: " + mediaCount);
                Helpers.showRateLimitStatus(locationFeed.getAPILimitStatus(),
                        locationFeed.getRemainingLimitStatus());
            } else {
                log.info("No location found against provided lat lng.");
            }
            log.info("!!! DONE !!!");
        } catch (InstagramException ex) {
            try {
                throw new InstagramException(ex.getMessage());
            } catch (InstagramException ex1) {
                java.util.logging.Logger.getLogger(
                        SearchLocation.class.getName())
                        .log(Level.SEVERE, null, ex1);
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(
                    SearchLocation.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}
