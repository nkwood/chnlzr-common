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
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.stream.IntStream;

public class WritableByteBufChannelTest {

  @Test
  public void testOpenClose() throws Exception {
    final int                 BYTE_COUNT = 10;
    final ByteBuf             SOURCE     = Unpooled.buffer(BYTE_COUNT);
    final WritableByteChannel CHANNEL    = new WritableByteBufChannel(SOURCE);

    assert CHANNEL.isOpen();
    CHANNEL.close();
    assert !CHANNEL.isOpen();
  }

  @Test
  public void testWriteOnce() throws Exception {
    final byte[]              CONTENT     = new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    final ByteBuffer          SOURCE      = ByteBuffer.wrap(CONTENT);
    final ByteBuf             DESTINATION = Unpooled.buffer(CONTENT.length);
    final WritableByteChannel CHANNEL     = new WritableByteBufChannel(DESTINATION);

    assert CHANNEL.write(SOURCE)     == CONTENT.length;
    assert DESTINATION.writerIndex() == CONTENT.length;
    assert !CHANNEL.isOpen();

    IntStream.range(0, CONTENT.length)
             .forEach(i -> { assert DESTINATION.getByte(i) == CONTENT[i]; });
  }

  @Test
  public void testWriteTwice() throws Exception {
    final byte[]              CONTENT     = new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    final ByteBuffer          SOURCE      = ByteBuffer.allocate(CONTENT.length);
    final ByteBuf             DESTINATION = Unpooled.buffer(CONTENT.length);
    final WritableByteChannel CHANNEL     = new WritableByteBufChannel(DESTINATION);

    SOURCE.limit(5);
    IntStream.range(0, 5).forEach(i -> SOURCE.put(CONTENT[i]));
    SOURCE.position(0);

    assert CHANNEL.write(SOURCE)     == 5;
    assert DESTINATION.writerIndex() == 5;
    assert CHANNEL.isOpen();

    SOURCE.limit(10);
    IntStream.range(5, 10).forEach(i -> SOURCE.put(i, CONTENT[i]));
    SOURCE.position(5);

    assert CHANNEL.write(SOURCE)     == 5;
    assert DESTINATION.writerIndex() == 10;
    assert !CHANNEL.isOpen();

    IntStream.range(0, CONTENT.length)
             .forEach(i -> { assert DESTINATION.getByte(i) == CONTENT[i]; });
  }

}
