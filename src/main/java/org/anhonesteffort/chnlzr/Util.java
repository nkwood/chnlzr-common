/*
 * Copyright (C) 2015 An Honest Effort LLC, coping.
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

package org.anhonesteffort.chnlzr;

public class Util {

  public static double kmDistanceBetween(double lat1, double lon1, double lat2, double lon2) {
    double diffLat = Math.toRadians(lat2 - lat1);
    double diffLon = Math.toRadians(lon2 - lon1);

    double a = Math.sin(diffLat / 2d) * Math.sin(diffLat / 2d) +
               Math.sin(diffLon / 2d) * Math.sin(diffLon / 2d) *
               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

    return 6371d * (2d * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)));
  }

}
