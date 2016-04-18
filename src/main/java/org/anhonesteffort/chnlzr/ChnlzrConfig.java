/*
 * Copyright (C) 2016 An Honest Effort LLC.
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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ChnlzrConfig {

  protected final Properties properties;
  private   final int        bufferHighWaterMark;
  private   final int        bufferLowWaterMark;
  private   final int        connectionTimeoutMs;
  private   final long       idleStateThresholdMs;
  private   final int        clientWriteQueueSize;

  public ChnlzrConfig() throws IOException {
    properties = new Properties();
    properties.load(new FileInputStream("chnlzr.properties"));

    bufferHighWaterMark  = Integer.parseInt(properties.getProperty("buffer_high_water_mark"));
    bufferLowWaterMark   = Integer.parseInt(properties.getProperty("buffer_low_water_mark"));
    connectionTimeoutMs  = Integer.parseInt(properties.getProperty("connection_timeout_ms"));
    idleStateThresholdMs = Long.parseLong(properties.getProperty("idle_state_threshold_ms"));
    clientWriteQueueSize = Integer.parseInt(properties.getProperty("client_write_queue_size"));
  }

  public int bufferHighWaterMark() {
    return bufferHighWaterMark;
  }

  public int bufferLowWaterMark() {
    return bufferLowWaterMark;
  }

  public int connectionTimeoutMs() {
    return connectionTimeoutMs;
  }

  public long idleStateThresholdMs() {
    return idleStateThresholdMs;
  }

  public int clientWriteQueueSize() {
    return clientWriteQueueSize;
  }

}
