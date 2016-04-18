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
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.stream.IntStream;

public class ReadableByteBufChannelTest {

  @Test
  public void testOpenClose() throws Exception {
    final int                 BYTE_COUNT = 10;
    final ByteBuf             SOURCE     = Unpooled.buffer(BYTE_COUNT);
    final ReadableByteChannel CHANNEL    = new ReadableByteBufChannel(SOURCE, BYTE_COUNT);

    assert CHANNEL.isOpen();
    CHANNEL.close();
    assert !CHANNEL.isOpen();
  }

  @Test
  public void testReadOnce() throws Exception {
    final byte[]              CONTENT     = new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    final ByteBuf             SOURCE      = Unpooled.wrappedBuffer(CONTENT);
    final ByteBuffer          DESTINATION = ByteBuffer.allocate(CONTENT.length);
    final ReadableByteChannel CHANNEL     = new ReadableByteBufChannel(SOURCE, CONTENT.length);

    assert CHANNEL.read(DESTINATION) == CONTENT.length;
    assert DESTINATION.position()    == CONTENT.length;
    assert DESTINATION.limit()       == CONTENT.length;
    assert !CHANNEL.isOpen();

    IntStream.range(0, CONTENT.length)
             .forEach(i -> { assert DESTINATION.get(i) == CONTENT[i]; });
  }

  @Test
  public void testReadTwice() throws Exception {
    final byte[]              CONTENT     = new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    final ByteBuf             SOURCE      = Unpooled.buffer(5);
    final ByteBuffer          DESTINATION = ByteBuffer.allocate(CONTENT.length);
    final ReadableByteChannel CHANNEL     = new ReadableByteBufChannel(SOURCE, CONTENT.length);

    IntStream.range(0, 5).forEach(i -> SOURCE.writeByte(CONTENT[i]));

    assert CHANNEL.read(DESTINATION) == 5;
    assert DESTINATION.position()    == 5;
    assert CHANNEL.isOpen();

    IntStream.range(5, 10).forEach(i -> SOURCE.writeByte(CONTENT[i]));

    assert CHANNEL.read(DESTINATION) ==  5;
    assert DESTINATION.position()    == 10;
    assert !CHANNEL.isOpen();

    IntStream.range(0, CONTENT.length)
        .forEach(i -> { assert DESTINATION.get(i) == CONTENT[i]; });
  }

}
