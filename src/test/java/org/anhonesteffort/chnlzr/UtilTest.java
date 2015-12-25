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

import org.junit.Test;

public class UtilTest {

  @Test
  public void testGreatCircle() {
    final double[] LOCATION0 = new double[] { 37.807143d, -122.261150d };
    final double[] LOCATION1 = new double[] { 22.208335d, -159.507002d };

    assert Math.abs(Util.kmDistanceBetween(
        LOCATION0[0], LOCATION0[1], LOCATION1[0], LOCATION1[1]
    ) - 3946d) < 10.0d;

    final double[] LOCATION2 = new double[] { 52.475422d,   13.412440d };
    final double[] LOCATION3 = new double[] { 22.208335d, -159.507002d };

    assert Math.abs(Util.kmDistanceBetween(
        LOCATION2[0], LOCATION2[1], LOCATION3[0], LOCATION3[1]
    ) - 11674d) < 10.0d;
  }

}
