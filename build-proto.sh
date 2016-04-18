#!/bin/bash

PROTO_INCLUDE_DIR=$CAPNPROTO_JAVA/compiler/src/main/schema
PROTO_SRC_DIR=src/main/proto
JAVA_SRC_DIR=src/main/java/org/anhonesteffort/chnlzr/capnp

capnp compile -I$PROTO_INCLUDE_DIR --src-prefix=$PROTO_SRC_DIR -o$CAPNPROTO_JAVA/capnpc-java:$JAVA_SRC_DIR $PROTO_SRC_DIR/chnlzr.capnp
