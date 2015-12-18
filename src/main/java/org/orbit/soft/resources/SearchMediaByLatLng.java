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
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;

/**
 * Search for media in a given area.
 *
 */
public class SearchMediaByLatLng {

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
            throw new IOException("Latitude " + args[1]
                    + " must be type of Double.");
        }

        try {
            longitude = Double.parseDouble(args[2]);
        } catch (Exception e) {
            throw new IOException("Longitude " + args[2]
                    + " must be type of Double.");
        }

        fileOutputFormat = StringEscapeUtils.escapeJava(args[3]);
        fileOutputPath = StringEscapeUtils.escapeJava(args[4]);

        Instagram instagram = new Instagram(userApiKey);

        String fileName = "lat_lng_media_" + latitudeStr + "_" + longitudeStr;
        try (PrintWriter writer = new PrintWriter(fileOutputPath
                + "/" + fileName, "UTF-8")) {

            MediaFeed searchMediaFeed = instagram.
                    searchMedia(latitude, longitude);
            List<MediaFeedData> mediaFeedsList = searchMediaFeed.getData();

            if (mediaFeedsList.size() > 0) {
                for (MediaFeedData mediaData : mediaFeedsList) {

                    if ("json".equals(fileOutputFormat.toLowerCase())) {

                        String json = new Gson().toJson(mediaData);
                        writer.println(json);
                    }
                }
                writer.close();

                log.info("Total Media collected: " + mediaFeedsList.size());

                Helpers.showRateLimitStatus(searchMediaFeed.getAPILimitStatus(),
                        searchMediaFeed.getRemainingLimitStatus());
            } else {
                log.info("No Media found against provided lat lng.");
            }
            log.info("!!! DONE !!!");
        } catch (InstagramException ex) {
            throw new InstagramException(ex.getMessage());
        }
    }
}
