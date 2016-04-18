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

package org.anhonesteffort.chnlzr.netty;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritableByteChannel;

public class WritableByteBufChannel implements WritableByteChannel {

  private final ByteBuf byteBuf;
  private       boolean isOpen = true;

  public WritableByteBufChannel(ByteBuf byteBuf) {
    this.byteBuf = byteBuf;
  }

  @Override
  public int write(ByteBuffer src) throws ClosedChannelException {
    if (!isOpen())
      throw new ClosedChannelException();

    int previousWriterIndex = byteBuf.writerIndex();
    byteBuf.writeBytes(src);

    return byteBuf.writerIndex() - previousWriterIndex;
  }

  @Override
  public boolean isOpen() {
    return isOpen && byteBuf.isWritable();
  }

  @Override
  public void close() {
    isOpen = false;
  }

}
