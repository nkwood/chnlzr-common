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

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.capnproto.MessageBuilder;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.stream.IntStream;

public class WriteQueuingContextTest {

  @Test
  public void testChannelWritable() {
    final ChannelHandlerContext CONTEXT = Mockito.mock(ChannelHandlerContext.class);
    final Channel               CHANNEL = Mockito.mock(Channel.class);
    final ChannelFuture         FUTURE  = Mockito.mock(ChannelFuture.class);

    Mockito.when(CONTEXT.channel()).thenReturn(CHANNEL);
    Mockito.when(CHANNEL.closeFuture()).thenReturn(FUTURE);
    Mockito.when(CHANNEL.isWritable()).thenReturn(true);

    final WriteQueuingContext QUEUE = new WriteQueuingContext(CONTEXT, 20);

    IntStream.range(0, 10).forEach(i -> QUEUE.writeOrQueue(new MessageBuilder()));
    Mockito.verify(CONTEXT, Mockito.times(10)).writeAndFlush(Mockito.any());
    Mockito.verify(CONTEXT, Mockito.never()).write(Mockito.any());
    Mockito.verify(CONTEXT, Mockito.never()).flush();
  }

  @Test
  public void testChannelWritableChange() {
    final ChannelHandlerContext CONTEXT = Mockito.mock(ChannelHandlerContext.class);
    final Channel               CHANNEL = Mockito.mock(Channel.class);
    final ChannelFuture         FUTURE  = Mockito.mock(ChannelFuture.class);

    Mockito.when(CONTEXT.channel()).thenReturn(CHANNEL);
    Mockito.when(CHANNEL.closeFuture()).thenReturn(FUTURE);
    Mockito.when(CHANNEL.isWritable()).thenReturn(false);

    final WriteQueuingContext QUEUE = new WriteQueuingContext(CONTEXT, 20);

    IntStream.range(0, 10).forEach(i -> QUEUE.writeOrQueue(new MessageBuilder()));
    Mockito.verify(CONTEXT, Mockito.never()).writeAndFlush(Mockito.any());
    Mockito.verify(CONTEXT, Mockito.never()).write(Mockito.any());
    Mockito.verify(CONTEXT, Mockito.never()).flush();

    Mockito.when(CHANNEL.isWritable()).thenReturn(true);
    QUEUE.onWritabilityChanged();

    Mockito.verify(CONTEXT, Mockito.never()).writeAndFlush(Mockito.any());
    Mockito.verify(CONTEXT, Mockito.times(10)).write(Mockito.any());
    Mockito.verify(CONTEXT, Mockito.times(1)).flush();
  }

  @Test
  public void testCloseOnOverflow() {
    final ChannelHandlerContext CONTEXT = Mockito.mock(ChannelHandlerContext.class);
    final Channel               CHANNEL = Mockito.mock(Channel.class);
    final ChannelFuture         FUTURE  = Mockito.mock(ChannelFuture.class);

    Mockito.when(CONTEXT.writeAndFlush(Mockito.any(MessageBuilder.class))).thenReturn(FUTURE);
    Mockito.when(CONTEXT.channel()).thenReturn(CHANNEL);
    Mockito.when(CHANNEL.closeFuture()).thenReturn(FUTURE);
    Mockito.when(CHANNEL.isWritable()).thenReturn(false);

    final WriteQueuingContext QUEUE = new WriteQueuingContext(CONTEXT, 10);

    IntStream.range(0, 10).forEach(i -> QUEUE.writeOrQueue(new MessageBuilder()));
    Mockito.verify(FUTURE, Mockito.never()).addListener(Mockito.eq(ChannelFutureListener.CLOSE));
    QUEUE.writeOrQueue(new MessageBuilder());
    Mockito.verify(FUTURE, Mockito.times(1)).addListener(Mockito.eq(ChannelFutureListener.CLOSE));
  }

}
