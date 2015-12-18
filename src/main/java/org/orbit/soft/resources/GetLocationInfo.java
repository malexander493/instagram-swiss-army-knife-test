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
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.locations.LocationInfo;
import org.jinstagram.exceptions.InstagramException;

/**
 * Get information about a location.
 *
 */
public class GetLocationInfo {

    private static final Logger log = LogManager.getRootLogger();
    private static String userApiKey = "";
    private static String locationId = "";
    private static String fileOutputFormat = "";
    private static String fileOutputPath = "";

    public static void get(String[] args) throws IOException {

        userApiKey = StringEscapeUtils.escapeJava(args[0]);
        locationId = StringEscapeUtils.escapeJava(args[1]);
        fileOutputFormat = StringEscapeUtils.escapeJava(args[2]);
        fileOutputPath = StringEscapeUtils.escapeJava(args[3]);

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
                }
            }
            
            Helpers.showRateLimitStatus(locationInfo.getAPILimitStatus(),
                                locationInfo.getRemainingLimitStatus());
            log.info("!!! DONE !!!");
            } else {
                log.info("No information found agianst provided location id");
            }
        } catch (InstagramException ex) {
            throw new InstagramException(ex.getMessage());
        }  catch (JsonSyntaxException ex) {
            throw new IOException("Provide location id is not working");
        }
    }
}