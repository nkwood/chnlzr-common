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

package org.anhonesteffort.chnlzr.netty;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;

public class ReadableByteBufChannel implements ReadableByteChannel {

  private final ByteBuf byteBuf;
  private final int     bytesToRead;
  private int           bytesRead = 0;

  public ReadableByteBufChannel(ByteBuf byteBuf, int bytesToRead) {
    this.byteBuf     = byteBuf;
    this.bytesToRead = bytesToRead;
  }

  @Override
  public int read(ByteBuffer dst) throws ClosedChannelException {
    if (!isOpen()) {
      throw new ClosedChannelException();
    }

    int bytesRemaining = bytesToRead - bytesRead;
    int bytesAvailable = byteBuf.readableBytes();
    int bytesToPut     = Math.min(Math.min(bytesRemaining, bytesAvailable), dst.remaining());

    if (bytesToPut < 1) {
      return 0;
    }

    dst.put(byteBuf.readBytes(bytesToPut).array());

    bytesRead += bytesToPut;
    return bytesToPut;
  }

  @Override
  public boolean isOpen() {
    return bytesRead < bytesToRead;
  }

  @Override
  public void close() {
    bytesRead = bytesToRead;
  }

}
