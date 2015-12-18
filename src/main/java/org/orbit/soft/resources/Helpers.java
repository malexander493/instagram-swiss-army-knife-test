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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Orbit Software Solutions
 */
public class Helpers {
    private static final Logger log = LogManager.getRootLogger();
    
    public static void showRateLimitStatus(int APILimit, int RemainingLimit) {
        log.info("---------------------------------------------------");
        log.info("API LIMIT: [" + APILimit + "]");
        log.info("REMAINING CALLS: [" + RemainingLimit + "]");
        log.info("---------------------------------------------------");
    }
}
