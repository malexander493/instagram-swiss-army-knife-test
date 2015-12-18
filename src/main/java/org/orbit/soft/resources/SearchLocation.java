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
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.locations.LocationSearchFeed;
import org.jinstagram.exceptions.InstagramException;

/**
 * Search for a location by geographic coordinate.
 *
 */
public class SearchLocation {

    private static final Logger log = LogManager.getRootLogger();
    private static String userApiKey = "";
    private static double latitude;
    private static double longitude;
    private static String fileOutputFormat = "";
    private static String fileOutputPath = "";

    public static void get(String[] args) throws IOException {

        userApiKey = StringEscapeUtils.escapeJava(args[0]);

        String latitudeStr = args[1].replace(".", "");
        latitudeStr = latitudeStr.replace("-", "");
        String longitudeStr = args[2].replace(".", "");
        longitudeStr = longitudeStr.replace("-", "");

        try {
            latitude = Double.parseDouble(args[1]);
        } catch (Exception e) {
            System.err.println("latitude " + args[1]
                    + " must be type of Double.");
        }

        try {
            longitude = Double.parseDouble(args[2]);
        } catch (Exception e) {
            System.err.println("longitude " + args[2]
                    + " must be type of Double.");
        }

        fileOutputFormat = StringEscapeUtils.escapeJava(args[3]);
        fileOutputPath = StringEscapeUtils.escapeJava(args[4]);

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
                    searchLocation(latitude, longitude);
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
            throw new InstagramException(ex.getMessage());
        }
    }
}
