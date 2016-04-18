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

import org.anhonesteffort.dsp.ChannelSpec;
import org.capnproto.MessageBuilder;

import static org.anhonesteffort.chnlzr.Proto.BaseMessage;
import static org.anhonesteffort.chnlzr.Proto.Error;
import static org.anhonesteffort.chnlzr.Proto.Capabilities;
import static org.anhonesteffort.chnlzr.Proto.ChannelRequest;
import static org.anhonesteffort.chnlzr.Proto.ChannelState;
import static org.anhonesteffort.chnlzr.Proto.Samples;
import static org.anhonesteffort.chnlzr.Proto.BaseMessage.Type;

public class CapnpUtil {

  public static MessageBuilder builder(BaseMessage.Reader reader) {
    MessageBuilder message = new MessageBuilder();
    message.setRoot(BaseMessage.factory, reader);
    return message;
  }

  public static MessageBuilder error(int code) {
    MessageBuilder      message     = new MessageBuilder();
    BaseMessage.Builder baseMessage = message.initRoot(BaseMessage.factory);
    Error.Builder       error       = baseMessage.initError();

    baseMessage.setType(Type.ERROR);
    error.setCode(code);

    return message;
  }

  public static MessageBuilder capabilities(double latitude,
                                            double longitude,
                                            int    polarization,
                                            double minFreq,
                                            double maxFreq,
                                            long   rate)
  {
    MessageBuilder       message      = new MessageBuilder();
    BaseMessage.Builder  baseMessage  = message.initRoot(BaseMessage.factory);
    Capabilities.Builder capabilities = baseMessage.initCapabilities();

    baseMessage.setType(Type.CAPABILITIES);

    capabilities.setLatitude(latitude);
    capabilities.setLongitude(longitude);
    capabilities.setPolarization(polarization);
    capabilities.setMinFrequency(minFreq);
    capabilities.setMaxFrequency(maxFreq);
    capabilities.setMaxChannelRate(rate);

    return message;
  }

  public static MessageBuilder capabilities(double      latitude,
                                            double      longitude,
                                            int         polarization,
                                            ChannelSpec spec)
  {
    return capabilities(
        latitude,          longitude,         polarization,
        spec.getMinFreq(), spec.getMaxFreq(), spec.getSampleRate()
    );
  }

  public static MessageBuilder channelRequest(ChannelRequest.Reader request) {
    MessageBuilder      message     = new MessageBuilder();
    BaseMessage.Builder baseMessage = message.initRoot(BaseMessage.factory);

    baseMessage.setType(Type.CHANNEL_REQUEST);
    baseMessage.setChannelRequest(request);

    return message;
  }

  public static ChannelRequest.Reader channelRequest(double frequency,
                                                     double bandwidth,
                                                     long   sampleRate,
                                                     long   maxRateDiff)
  {
    MessageBuilder         message = new MessageBuilder();
    ChannelRequest.Builder request = message.initRoot(ChannelRequest.factory);

    request.setCenterFrequency(frequency);
    request.setBandwidth(bandwidth);
    request.setSampleRate(sampleRate);
    request.setMaxRateDiff(maxRateDiff);

    return request.asReader();
  }

  public static MessageBuilder state(Long sampleRate, Double frequency) {
    MessageBuilder       message     = new MessageBuilder();
    BaseMessage.Builder  baseMessage = message.initRoot(BaseMessage.factory);
    ChannelState.Builder state       = baseMessage.initChannelState();

    baseMessage.setType(Type.CHANNEL_STATE);
    state.setSampleRate(sampleRate);
    state.setCenterFrequency(frequency);

    return message;
  }

  public static MessageBuilder samples(int sampleCount) {
    MessageBuilder      message     = new MessageBuilder();
    BaseMessage.Builder baseMessage = message.initRoot(BaseMessage.factory);
    Samples.Builder     samples     = baseMessage.initSamples();

    baseMessage.setType(Type.SAMPLES);
    samples.initSamples((sampleCount * 2) * Float.BYTES);

    return message;
  }

  public static ChannelSpec spec(ChannelRequest.Reader request) {
    return new ChannelSpec(request.getCenterFrequency(),
                           request.getBandwidth(),
                           request.getSampleRate());
  }

  public static ChannelSpec spec(Capabilities.Reader capabilities) {
    return ChannelSpec.fromMinMax(capabilities.getMinFrequency(),
                                  capabilities.getMaxFrequency(),
                                  capabilities.getMaxChannelRate());
  }

}
