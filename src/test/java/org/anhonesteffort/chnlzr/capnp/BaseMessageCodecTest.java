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

package org.anhonesteffort.chnlzr.capnp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.anhonesteffort.chnlzr.netty.IdleStateHeartbeatWriter;
import org.capnproto.MessageBuilder;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.LinkedList;
import java.util.List;

import static org.anhonesteffort.chnlzr.capnp.Proto.BaseMessage;
import static org.anhonesteffort.chnlzr.capnp.Proto.Capabilities;
import static org.anhonesteffort.chnlzr.capnp.Proto.BaseMessage.Type;

public class BaseMessageCodecTest {

  @Test
  public void test() throws Exception {
    final BaseMessageEncoder    ENCODER = BaseMessageEncoder.INSTANCE;
    final BaseMessageDecoder    DECODER = new BaseMessageDecoder();
    final ChannelHandlerContext CONTEXT = Mockito.mock(ChannelHandlerContext.class);

    final MessageBuilder INPUT_MESSAGE = new MessageBuilder();
    final ByteBuf        OUTPUT_BYTES  = Unpooled.buffer(4096);
    final List<Object>   OUTPUT_POJOS  = new LinkedList<>();

    final BaseMessage.Builder  INPUT_BASE         = INPUT_MESSAGE.initRoot(BaseMessage.factory);
    final Capabilities.Builder INPUT_CAPABILITIES = INPUT_BASE.initCapabilities();

    final Long   CHANNEL_RATE = 3000l;
    final Double MIN_FREQ     = 1000d;
    final Double MAX_FREQ     = 2000d;

    INPUT_BASE.setType(Type.CAPABILITIES);
    INPUT_CAPABILITIES.setMaxChannelRate(CHANNEL_RATE);
    INPUT_CAPABILITIES.setMinFrequency(MIN_FREQ);
    INPUT_CAPABILITIES.setMaxFrequency(MAX_FREQ);

    ENCODER.encode(CONTEXT, INPUT_MESSAGE, OUTPUT_BYTES);
    DECODER.decode(CONTEXT, OUTPUT_BYTES, OUTPUT_POJOS);
    assert OUTPUT_POJOS.size() == 1;

    BaseMessage.Reader  OUTPUT_BASE         = (BaseMessage.Reader) OUTPUT_POJOS.get(0);
    Capabilities.Reader OUTPUT_CAPABILITIES = OUTPUT_BASE.getCapabilities();

    assert OUTPUT_BASE.getType()                   == Type.CAPABILITIES;
    assert OUTPUT_CAPABILITIES.getMaxChannelRate() == CHANNEL_RATE;
    assert OUTPUT_CAPABILITIES.getMinFrequency()   == MIN_FREQ;
    assert OUTPUT_CAPABILITIES.getMaxFrequency()   == MAX_FREQ;
  }

  @Test
  public void testWithHeartbeat() throws Exception {
    final BaseMessageEncoder    ENCODER = BaseMessageEncoder.INSTANCE;
    final BaseMessageDecoder    DECODER = new BaseMessageDecoder();
    final ChannelHandlerContext CONTEXT = Mockito.mock(ChannelHandlerContext.class);

    final MessageBuilder INPUT_MESSAGE = new MessageBuilder();
    final ByteBuf        OUTPUT_BYTES  = Unpooled.buffer(4096);
    final List<Object>   OUTPUT_POJOS  = new LinkedList<>();

    final BaseMessage.Builder  INPUT_BASE         = INPUT_MESSAGE.initRoot(BaseMessage.factory);
    final Capabilities.Builder INPUT_CAPABILITIES = INPUT_BASE.initCapabilities();

    final Long   CHANNEL_RATE = 3000l;
    final Double MIN_FREQ     = 1000d;
    final Double MAX_FREQ     = 2000d;

    INPUT_BASE.setType(Type.CAPABILITIES);
    INPUT_CAPABILITIES.setMaxChannelRate(CHANNEL_RATE);
    INPUT_CAPABILITIES.setMinFrequency(MIN_FREQ);
    INPUT_CAPABILITIES.setMaxFrequency(MAX_FREQ);

    OUTPUT_BYTES.writeBytes(IdleStateHeartbeatWriter.HEARTBEAT_BYTES.duplicate());

    ENCODER.encode(CONTEXT, INPUT_MESSAGE, OUTPUT_BYTES);
    DECODER.decode(CONTEXT, OUTPUT_BYTES, OUTPUT_POJOS);
    assert OUTPUT_POJOS.size() == 0;
    DECODER.decode(CONTEXT, OUTPUT_BYTES, OUTPUT_POJOS);
    assert OUTPUT_POJOS.size() == 1;

    BaseMessage.Reader  OUTPUT_BASE         = (BaseMessage.Reader) OUTPUT_POJOS.get(0);
    Capabilities.Reader OUTPUT_CAPABILITIES = OUTPUT_BASE.getCapabilities();

    assert OUTPUT_BASE.getType()                   == Type.CAPABILITIES;
    assert OUTPUT_CAPABILITIES.getMaxChannelRate() == CHANNEL_RATE;
    assert OUTPUT_CAPABILITIES.getMinFrequency()   == MIN_FREQ;
    assert OUTPUT_CAPABILITIES.getMaxFrequency()   == MAX_FREQ;
  }

}
