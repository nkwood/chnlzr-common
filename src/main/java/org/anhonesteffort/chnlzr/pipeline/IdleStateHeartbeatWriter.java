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
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class IdleStateHeartbeatWriter extends ChannelHandlerAdapter {

  public static final IdleStateHeartbeatWriter INSTANCE = new IdleStateHeartbeatWriter();
  public static final ByteBuf HEARTBEAT_BYTES = Unpooled.unreleasableBuffer(Unpooled.copyInt(0x00));
  private static final Logger log = LoggerFactory.getLogger(IdleStateHeartbeatWriter.class);

  private IdleStateHeartbeatWriter() {
    super();
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext context, Object event) throws Exception {
    if (event instanceof IdleStateEvent) {
      context.writeAndFlush(HEARTBEAT_BYTES.duplicate());
    } else {
      super.userEventTriggered(context, event);
    }
  }

}
