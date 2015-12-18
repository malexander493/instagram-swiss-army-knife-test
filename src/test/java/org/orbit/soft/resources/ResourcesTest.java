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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import org.jinstagram.Instagram;
import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.comments.MediaCommentsFeed;
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.common.User;
import org.jinstagram.entity.likes.LikesFeed;
import org.jinstagram.entity.locations.LocationInfo;
import org.jinstagram.entity.locations.LocationSearchFeed;
import org.jinstagram.entity.media.MediaInfoFeed;
import org.jinstagram.entity.tags.TagInfoData;
import org.jinstagram.entity.tags.TagInfoFeed;
import org.jinstagram.entity.tags.TagMediaFeed;
import org.jinstagram.entity.tags.TagSearchFeed;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.entity.users.basicinfo.UserInfoData;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.entity.users.feed.UserFeed;
import org.jinstagram.entity.users.feed.UserFeedData;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test for all Resources
 * 
 * 
 * @author Orbit Software Solutions
 */
public class ResourcesTest {
    
    private static final Logger log = LoggerFactory.
            getLogger(ResourcesTest.class);
    
    private static Instagram instagram = null;
    
    @BeforeClass
    public static void beforeMethod() {
        try {
            org.junit.Assume.assumeTrue(isApiKeyAvailable());
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ResourcesTest.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    private static boolean isApiKeyAvailable() throws FileNotFoundException, 
            IOException {

        String envVar = System.getenv("ISAK_CONF");
        if (envVar == null || envVar.isEmpty()) {
            log.info("Environment variable not set. ISAK_CONF");
            return false;
        }
        File propConfFile = new File(envVar + File.separator
                + "isak.properties");
        if (!propConfFile.exists()) {
            log.info("isak.properties file does not exist in: "
                    + envVar);
            return false;
        }
        Properties prop = new Properties();
        try (InputStream propInstream = new FileInputStream(propConfFile)) {
            prop.load(propInstream);
        }
        String apiKey = prop.getProperty("apiKey").trim();
        instagram = new Instagram(apiKey);
        return true;
    }
    
    
    @Test
    public void getPopularMedia() throws Exception {

        log.info("Printing a list of popular media...");
        MediaFeed popularMedia = instagram.getPopularMedia();

        printMediaFeedList(popularMedia.getData());
    }
    
    @Test
    public void getMediaInfo() throws Exception {

        String mediaId = "1116636004468547023_43109246";
        log.info("Get information about a media object.");
        MediaInfoFeed mediaInfo = instagram.getMediaInfo(mediaId);

        printMediaFeed(mediaInfo.getData());
    }
    
    @Test
    public void searchMedia() throws Exception {

        // London  - 51.5072° N, 0.1275° W
        double latitude = 51.5072;
        double longitude = 0.1275;
        
        log.info("Search for media in a given area....");
        MediaFeed searchMediaFeed = instagram.searchMedia(latitude, longitude);

        printMediaFeedList(searchMediaFeed.getData());
    }
    
    @Test
    public void getMediaComments() throws Exception {

        String mediaId = "1116636004468547023_43109246";
        
        log.info("Printing a list of recent comments on a media...");
        MediaCommentsFeed commentsFeed = instagram.getMediaComments(mediaId);

        printMediaCommentList(commentsFeed.getCommentDataList());
    }
    
    @Test
    public void getUserLikes() throws Exception {

        String mediaId = "1116636004468547023_43109246";
        
        log.info("Printing a list of users who have liked this media.");
        LikesFeed userLikes = instagram.getUserLikes(mediaId);

        printUserLikesList(userLikes.getUserList());
    }
    
    @Test
    public void getTagInfo() throws Exception {

        String tagName = "bmw";
        log.info("Printing information about a tag object....");
        
        TagInfoFeed tagFeed = instagram.getTagInfo(tagName);
        TagInfoData tagData = tagFeed.getTagInfo();
            
        log.info("name: " + tagData.getTagName());
        log.info("media_count: " + tagData.getMediaCount());
    }
    
    @Test
    public void searchTags() throws Exception {

        String tagName = "bmw";
        log.info("Search for tags by name...");
        
        TagSearchFeed tagsFeed = instagram.searchTags(tagName);
        List<TagInfoData> tagsList = tagsFeed.getTagList();

        final int countBreaker = 5;
        int count = 0;
        for (TagInfoData tagData : tagsList) {
            
            count ++;
            if (count == countBreaker) {
                log.info("Too many tags to get!!! Breaking the loop.");
                break;
            }
            
            log.info("-------------------------------------------");
            log.info("name: " + tagData.getTagName());
            log.info("media_count: " + tagData.getMediaCount());
            log.info("-------------------------------------------");
        }
    }

    @Test
    public void searchLocation() throws Exception {

        // London  - 51.5072° N, 0.1275° W
        double latitude = 51.5072;
        double longitude = 0.1275;

        LocationSearchFeed locationSearchFeed = 
                instagram.searchLocation(latitude, longitude);

        List<Location> locationList = locationSearchFeed.getLocationList();
        log.info("Printing Location Details for Latitude " 
                + latitude + " and longitude " + longitude);
        
        final int countBreaker = 5;
        int count = 0;
        for (Location location : locationList) {
            
            count++;
            if (count == countBreaker) {
                log.info("Too many locations to get!!! Breaking the loop.");
                break;
            }
            
            log.info("-------------------------------------------");

            log.info("Id : " + location.getId());
            log.info("Name : " + location.getName());
            log.info("Latitude : " + location.getLatitude());
            log.info("Longitude : " + location.getLatitude());

            log.info("-------------------------------------------");

        }
    }
    
    @Test
    public void getLocationInfo() throws Exception {

        String locationId = "1";
        log.info("Printing information about a location.");
        
        LocationInfo locationInfo = instagram.getLocationInfo(locationId);
        Location location = locationInfo.getLocationData();

        log.info("id: " + location.getId());
        log.info("name: " + location.getName());
        log.info("latitude: " + location.getLatitude());
        log.info("longitude: " + location.getLongitude());
    }
    
    @Test
    public void getRecentMediaByLocation() throws Exception {
        
        String locationId = "1";

        MediaFeed locationMediaFeed = instagram.
                getRecentMediaByLocation(locationId);
        
        assertTrue(locationMediaFeed.getData().size() > 0);

        // next page
        MediaFeed locationMediaFeed2 = instagram.
                        getRecentMediaNextPage(locationMediaFeed.
                                getPagination());
        
        assertTrue(locationMediaFeed2.getData().size() > 0);

        assertNotEquals(locationMediaFeed.getData().get(0).getId(), 
                locationMediaFeed2.getData().get(1).getId());
    }

    @Test
    public void userFollowedBy() throws Exception {
        // instagram user id
        String userId = "25025320";

        UserFeed feed1 = instagram.getUserFollowedByList(userId);
        assertEquals(50, feed1.getUserList().size());

        UserFeed feed2 = instagram.getUserFollowedByListNextPage(
                userId, feed1.getPagination().getNextCursor());
        assertEquals(50, feed2.getUserList().size());

        assertNotEquals(feed1.getUserList().get(0).getId(), 
                feed2.getUserList().get(1).getId());
    }

    @Test
    public void userFollower() throws Exception {
        // instagram user id
        String userId = "25025320";

        UserFeed feed1 = instagram.getUserFollowList(userId);
        assertEquals(50, feed1.getUserList().size());

        UserFeed feed2 = instagram.getUserFollowListNextPage(
                userId, feed1.getPagination().getNextCursor());
        assertEquals(50, feed2.getUserList().size());

        assertNotEquals(feed1.getUserList().get(0).getId(), 
                feed2.getUserList().get(1).getId());
    }

    @Test
    public void searchUser() throws Exception {
        String query = "sachin"; 
        UserFeed userFeed = instagram.searchUser(query);

        log.info("Search for a user by name.");
        
        final int countBreaker = 5;
        int count = 0;
        for (UserFeedData userFeedData : userFeed.getUserList()) {
            
            count++;

            if (count == countBreaker) {
                log.info("Too many users to get!!! Breaking the loop.");
                break;
            }
            
            log.info("**************************************************");
            log.info("Id : " + userFeedData.getId());
            log.info("Username : " + userFeedData.getUserName());
            log.info("Name  : " + userFeedData.getFullName());
            log.info("Bio : " + userFeedData.getBio());
            log.info("Profile Picture URL : " + userFeedData.
                    getProfilePictureUrl());
            log.info("Website : " + userFeedData.getWebsite());
            log.info("**************************************************");
        }
    }
    
    @Test
    public void getUserInfo() throws Exception {
        String userId = "25025320";
        UserInfo userInfo = instagram.getUserInfo(userId);

        log.info("Printing basic information about a user...");
        
        UserInfoData userData = userInfo.getData();
        
        log.info("**************************************************");
        log.info("Id : " + userData.getId());
        log.info("Username : " + userData.getUsername());
        log.info("Name  : " + userData.getFullName());
        log.info("Bio : " + userData.getBio());
        log.info("Profile Picture URL : " + userData.
                getProfilePicture());
        log.info("Website : " + userData.getWebsite());
        log.info("media_count: "
                    + userData.getCounts().getMedia());
            log.info("follows: " + userData.getCounts().getFollows());
            log.info("followed_by: "
                    + userData.getCounts().getFollowedBy());
        log.info("**************************************************");
    }
    

    @Test
    public void getMediaByTags() throws Exception {
        String tagName = "london";
        
        log.info("Printing list of recently tagged media...");
        
        TagMediaFeed recentMediaTags = instagram.getRecentMediaTags(tagName);
        printMediaFeedList(recentMediaTags.getData());
    }

    @Test
    public void testGetAllUserPhotos() throws Exception {
        getUserPhotos("18428658");
    }

    private List<MediaFeedData> getUserPhotos(String userId) throws Exception {
        // Don't get all the photos, just break the page count on 5
        
        log.info("Printing the most recent media published by a user...");
        
        MediaFeed recentMediaFeed = instagram.getRecentMediaFeed(userId);
        List<MediaFeedData> userPhotos = new ArrayList<>();

        for (MediaFeedData mediaFeedData : recentMediaFeed.getData()) {
            userPhotos.add(mediaFeedData);
        }

        final int countBreaker = 5;
        int count = 0;
        while (recentMediaFeed.getPagination() != null) {
            count++;

            if (count == countBreaker) {
                log.info("Too many photos to get!!! Breaking the loop.");
                break;
            }

            try {
                recentMediaFeed = instagram.getRecentMediaNextPage(
                        recentMediaFeed.getPagination());
                for (MediaFeedData mediaFeedData : recentMediaFeed.getData()) {
                    userPhotos.add(mediaFeedData);
                }
            } catch (Exception ex) {
                break;
            }
        }

        return userPhotos;
    }


    private void printMediaFeedList(List<MediaFeedData> mediaFeedDataList) {

        final int countBreaker = 5;
        int count = 0;
        
        for (MediaFeedData mediaFeedData : mediaFeedDataList) {
            
            count++;

            if (count > countBreaker) {
                log.info("Too many photos to get!!! Breaking the loop.");
                break;
            }            
            log.info("-------------------------------------------");

            log.info("Id : " + mediaFeedData.getId());
            log.info("Image Filter : " + mediaFeedData.getImageFilter());
            log.info("Link : " + mediaFeedData.getLink());

            log.info("-------------------------------------------");

        }
    }
    
    private void printMediaFeed(MediaFeedData mediaData) {

        log.info("-------------------------------------------");
        log.info("id: " + mediaData.getId());
        log.info("CreatedTime: " + mediaData.getCreatedTime());
        log.info("ImageFilter: " + mediaData.getImageFilter());
        log.info("Link: " + mediaData.getLink());
        log.info("Type: " + mediaData.getType());
        log.info("Location: " + mediaData.getLocation());
        log.info("Tags: " + mediaData.getTags());
        log.info("-------------------------------------------");

    }
    
    private void printMediaCommentList(List<CommentData> mediaFeedDataList) {

        final int countBreaker = 5;
        int count = 0;
        
        for (CommentData mediaData : mediaFeedDataList) {
            
            count++;

            if (count > countBreaker) {
                log.info("Too many comments to get!!! Breaking the loop.");
                break;
            }            
            log.info("-------------------------------------------");
            log.info("Id : " + mediaData.getId());
            log.info("Created Time: " + mediaData.getCreatedTime());
            log.info("Text: " + mediaData.getText());
            log.info("-------------------------------------------");
        }
    }
    
    private void printUserLikesList(List<User> likeData) {
        
        final int countBreaker = 5;
        int count = 0;
        
        for (User userLikeData : likeData) {
            
             count++;

            if (count > countBreaker) {
                log.info("Too many likes to get!!! Breaking the loop.");
                break;
            }
            
            log.info("-------------------------------------------");
            log.info("Id: " + userLikeData.getId());
            log.info("Full Name: " + userLikeData.getFullName());
            log.info("Profile Picture Url: " 
                    + userLikeData.getProfilePictureUrl());
            log.info("Bio: " + userLikeData.getBio());
            log.info("User Name: " + userLikeData.getUserName());
            log.info("Website Url: " + userLikeData.getWebsiteUrl());
            log.info("-------------------------------------------");
        }
    }
}
