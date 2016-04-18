@0xd87c8398fe1111fe;

using Cxx = import "/capnp/c++.capnp";
$Cxx.namespace("chnlzr");

using Java = import "/capnp/java.capnp";
$Java.package("org.anhonesteffort.chnlzr.capnp");
$Java.outerClassname("Proto");

const polarizationVertical   :UInt32 = 1;
const polarizationHorizontal :UInt32 = 2;
const polarizationCircular   :UInt32 = 3;

struct BaseMessage {

  enum Type {
    error          @0;
    capabilities   @1;
    channelRequest @2;
    channelState   @3;
    samples        @4;
  }

  type           @0 :Type;
  error          @1 :Error;
  capabilities   @2 :Capabilities;
  channelRequest @3 :ChannelRequest;
  channelState   @4 :ChannelState;
  samples        @5 :Samples;

}

struct Error {

  const errorUnknown               :UInt32 = 1;
  const errorIncapable             :UInt32 = 2;
  const errorProcessingUnavailable :UInt32 = 3;
  const errorBandwidthUnavailable  :UInt32 = 4;
  const errorOverflow              :UInt32 = 5;

  code @0 :UInt32;

}

struct Capabilities {

  latitude       @0 :Float64;
  longitude      @1 :Float64;
  polarization   @2 :UInt32;
  minFrequency   @3 :Float64;
  maxFrequency   @4 :Float64;
  maxChannelRate @5 :UInt64;

}

struct ChannelRequest {

  centerFrequency @0 :Float64;
  bandwidth       @1 :Float64;
  sampleRate      @2 :UInt64;
  maxRateDiff     @3 :UInt64;

}

struct ChannelState {

  centerFrequency @0 :Float64;
  sampleRate      @1 :UInt64;

}

struct Samples {

  samples @0 :Data;

}