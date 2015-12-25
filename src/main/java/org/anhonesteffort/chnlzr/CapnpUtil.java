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
import org.capnproto.StructList;

import java.util.List;
import java.util.stream.IntStream;

import static org.anhonesteffort.chnlzr.Proto.BaseMessage;
import static org.anhonesteffort.chnlzr.Proto.Error;
import static org.anhonesteffort.chnlzr.Proto.Capabilities;
import static org.anhonesteffort.chnlzr.Proto.ChannelRequest;
import static org.anhonesteffort.chnlzr.Proto.ChannelState;
import static org.anhonesteffort.chnlzr.Proto.Samples;
import static org.anhonesteffort.chnlzr.Proto.HostId;
import static org.anhonesteffort.chnlzr.Proto.BrkrState;
import static org.anhonesteffort.chnlzr.Proto.ChannelGrant;
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

  public static ChannelRequest.Reader channelRequest(double latitude,
                                                     double longitude,
                                                     double maxLocationDiff,
                                                     int    polarization,
                                                     double frequency,
                                                     double bandwidth,
                                                     long   sampleRate,
                                                     long   maxRateDiff)
  {
    MessageBuilder         message = new MessageBuilder();
    ChannelRequest.Builder request = message.initRoot(ChannelRequest.factory);

    request.setLatitude(latitude);
    request.setLongitude(longitude);
    request.setMaxLocationDiff(maxLocationDiff);
    request.setPolarization(polarization);
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
    samples.initSamples(sampleCount * 2);

    return message;
  }

  public static MessageBuilder chnlzrHello(String hostname, int port) {
    MessageBuilder      message     = new MessageBuilder();
    BaseMessage.Builder baseMessage = message.initRoot(BaseMessage.factory);
    HostId.Builder      id          = baseMessage.initChnlzrHello().initId();

    baseMessage.setType(Type.CHNLZR_HELLO);
    id.setHostname(hostname);
    id.setPort(port);

    return message;
  }

  public static MessageBuilder brkrHello(HostId.Reader hostId) {
    MessageBuilder      message     = new MessageBuilder();
    BaseMessage.Builder baseMessage = message.initRoot(BaseMessage.factory);

    baseMessage.setType(Type.BRKR_HELLO);
    baseMessage.initBrkrHello().setId(hostId);

    return message;
  }

  // todo: gotta be a better way
  public static MessageBuilder brkrState(List<Capabilities.Reader> capabilities,
                                         List<ChannelGrant.Reader> grants)
  {
    MessageBuilder                           message     = new MessageBuilder();
    BaseMessage.Builder                      baseMessage = message.initRoot(BaseMessage.factory);
    BrkrState.Builder                        brkrState   = baseMessage.initBrkrState();
    StructList.Builder<Capabilities.Builder> capsList    = brkrState.initChnlzrs(capabilities.size());
    StructList.Builder<ChannelGrant.Builder> grantsList  = brkrState.initGrants(grants.size());

    baseMessage.setType(Type.BRKR_STATE);

    IntStream.range(0, capabilities.size()).forEach(i -> {
      Capabilities.Reader  original = capabilities.get(i);
      Capabilities.Builder copy     = capsList.get(i);

      copy.setLatitude(original.getLatitude());
      copy.setLongitude(original.getLongitude());
      copy.setPolarization(original.getPolarization());
      copy.setMinFrequency(original.getMinFrequency());
      copy.setMaxFrequency(original.getMaxFrequency());
      copy.setMaxChannelRate(original.getMaxChannelRate());
    });

    IntStream.range(0, grants.size()).forEach(i -> {
      ChannelGrant.Reader  original = grants.get(i);
      ChannelGrant.Builder copy     = grantsList.get(i);

      copy.setId(original.getId());
      copy.setLatitude(original.getLatitude());
      copy.setLongitude(original.getLongitude());
      copy.setPolarization(original.getPolarization());
      copy.setCenterFrequency(original.getCenterFrequency());
      copy.setBandwidth(original.getBandwidth());
      copy.setSampleRate(original.getSampleRate());
      copy.setMaxRateDiff(original.getMaxRateDiff());
    });

    return message;
  }

  public static MessageBuilder multiplexRequest(long grantId) {
    MessageBuilder      message     = new MessageBuilder();
    BaseMessage.Builder baseMessage = message.initRoot(BaseMessage.factory);

    baseMessage.setType(Type.MULTIPLEX_REQUEST);
    baseMessage.initMultiplexRequest().setGrantId(grantId);

    return message;
  }

  public static MessageBuilder getBrkrList() {
    MessageBuilder      message     = new MessageBuilder();
    BaseMessage.Builder baseMessage = message.initRoot(BaseMessage.factory);

    baseMessage.setType(Type.GET_BRKR_LIST);

    return message;
  }

  public static MessageBuilder brkrList(List<HostId.Reader> brkrHosts) {
    MessageBuilder                     message     = new MessageBuilder();
    BaseMessage.Builder                baseMessage = message.initRoot(BaseMessage.factory);
    StructList.Builder<HostId.Builder> brkrs       = baseMessage.initBrkrList().initChnlbrkrs(brkrHosts.size());

    baseMessage.setType(Type.BRKR_LIST);

    IntStream.range(0, brkrHosts.size()).forEach(i -> {
      brkrs.get(i).setHostname(brkrHosts.get(i).getHostname());
      brkrs.get(i).setPort(brkrHosts.get(i).getPort());
    });

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

  public static ChannelSpec spec(ChannelGrant.Reader request) {
    return new ChannelSpec(request.getCenterFrequency(),
                           request.getBandwidth(),
                           request.getSampleRate());
  }

  public static HostId.Reader hostId(String hostname, int port) {
    MessageBuilder message = new MessageBuilder();
    HostId.Builder id      = message.initRoot(HostId.factory);

    id.setHostname(hostname);
    id.setPort(port);

    return id.asReader();
  }

  public static ChannelGrant.Reader grant(long   id,
                                          double latitude,
                                          double longitude,
                                          int    polarization,
                                          double frequency,
                                          double bandwidth,
                                          long   sampleRate,
                                          long   maxRateDiff)
  {
    MessageBuilder       message = new MessageBuilder();
    ChannelGrant.Builder grant   = message.initRoot(ChannelGrant.factory);

    grant.setId(id);
    grant.setLatitude(latitude);
    grant.setLongitude(longitude);
    grant.setPolarization(polarization);
    grant.setCenterFrequency(frequency);
    grant.setBandwidth(bandwidth);
    grant.setSampleRate(sampleRate);
    grant.setMaxRateDiff(maxRateDiff);

    return grant.asReader();
  }

  public static boolean grantSatisfiesRequest(ChannelGrant.Reader   grant,
                                              ChannelRequest.Reader request)
  {
    double distanceBetween = Util.kmDistanceBetween(
        grant.getLatitude(),   grant.getLongitude(),
        request.getLatitude(), request.getLongitude()
    );

    return spec(grant).containsChannel(spec(request)) &&
           (request.getPolarization() == 0 || grant.getPolarization() == request.getPolarization()) &&
           (request.getMaxLocationDiff() <= 0 || distanceBetween <= request.getMaxLocationDiff());
  }

  public static String toString(HostId.Reader host) {
    return host.getHostname().toString() + ":" + host.getPort();
  }

}
