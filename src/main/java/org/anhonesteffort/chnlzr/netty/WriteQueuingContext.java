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

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.anhonesteffort.chnlzr.capnp.ProtoFactory;
import org.capnproto.MessageBuilder;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.anhonesteffort.chnlzr.capnp.Proto.Error;

public class WriteQueuingContext {

  private final ProtoFactory          proto = new ProtoFactory();
  private final ChannelHandlerContext context;
  private final Queue<MessageBuilder> msgQueue;

  public WriteQueuingContext(ChannelHandlerContext context, int queueSize) {
    this.context = context;
    msgQueue     = new LinkedBlockingQueue<>(queueSize);

    context.channel().closeFuture().addListener(close -> msgQueue.clear());
  }

  public ChannelFuture getCloseFuture() {
    return context.channel().closeFuture();
  }

  public void onWritabilityChanged() {
    if (!context.channel().isWritable()) {
      return;
    }

    MessageBuilder message = msgQueue.poll();
    boolean        flush   = message != null;

    while (message != null) {
      context.write(message);

      if (context.channel().isWritable()) {
        message = msgQueue.poll();
      } else {
        message = null;
      }
    }

    if (flush) {
      context.flush();
    }
  }

  public void writeOrQueue(MessageBuilder message) {
    if (context.channel().isWritable() && msgQueue.isEmpty()) {
      context.writeAndFlush(message);
    } else if (!msgQueue.offer(message)) {
      context.writeAndFlush(proto.error(Error.ERROR_OVERFLOW))
             .addListener(ChannelFutureListener.CLOSE);
    }
  }

  public ChannelFuture writeAndClose(MessageBuilder message) {
    return context.writeAndFlush(message)
                  .addListener(ChannelFutureListener.CLOSE);
  }

  public ChannelFuture close() {
    return context.close();
  }

}
