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

package org.anhonesteffort.chnlzr.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.anhonesteffort.chnlzr.ReadableByteBufChannel;
import org.capnproto.SerializePacked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static org.anhonesteffort.chnlzr.Proto.BaseMessage;

public class BaseMessageDecoder extends ByteToMessageDecoder {

  private static final Logger log = LoggerFactory.getLogger(BaseMessageDecoder.class);

  private int messageLength = -1;

  @Override
  protected void decode(ChannelHandlerContext context,
                        ByteBuf               input,
                        List<Object>          output)
  {
    if (messageLength < 0 && input.readableBytes() >= 4) {
      messageLength = input.readInt();
    }

    if (messageLength > 0 && input.readableBytes() >= messageLength) {
      try {

        output.add(
            SerializePacked.readFromUnbuffered(new ReadableByteBufChannel(input, messageLength))
                           .getRoot(BaseMessage.factory)
        );

      } catch (IOException e) {
        log.warn("received invalid protocol buffer, closing connection", e);
        context.close();
      } finally {
        messageLength = -1;
      }
    }

    else if (messageLength == 0)
      messageLength = -1;
  }

}
