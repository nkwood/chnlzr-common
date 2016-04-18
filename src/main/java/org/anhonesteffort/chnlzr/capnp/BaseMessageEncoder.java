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

package org.anhonesteffort.chnlzr.capnp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.anhonesteffort.chnlzr.netty.WritableByteBufChannel;
import org.capnproto.MessageBuilder;
import org.capnproto.SerializePacked;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

@ChannelHandler.Sharable
public class BaseMessageEncoder extends MessageToByteEncoder<MessageBuilder> {

  public static final BaseMessageEncoder INSTANCE = new BaseMessageEncoder();

  private BaseMessageEncoder() {
    super();
  }

  @Override
  protected void encode(ChannelHandlerContext context,
                        MessageBuilder        input,
                        ByteBuf               output)
      throws IOException
  {
    WritableByteChannel outputChannel = new WritableByteBufChannel(output);
    int                 msgStartIndex = output.writerIndex();

    output.writerIndex(msgStartIndex + 4);
    SerializePacked.writeToUnbuffered(outputChannel, input);

    int msgEndIndex  = output.writerIndex();
    int msgByteCount = (msgEndIndex - msgStartIndex) - 4;

    output.writerIndex(msgStartIndex);
    output.writeInt(msgByteCount);
    output.writerIndex(msgEndIndex);
  }

}
