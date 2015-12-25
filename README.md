# chnlzr-common

Resample RF spectrum and stream it across the internet.

## Build and Install
```
$ mvn package install
```

## Development
chnlzr-common uses [Cap'n Proto](https://capnproto.org/). If you care to modify
`chnlzr.proto` and regenerate sources using the `build-proto.sh` script you
must define the environment variable `CAPNPROTO_JAVA` and have it point to a
local copy of [capnproto-java](https://github.com/dwrensha/capnproto-java/).

## License

Copyright 2015 An Honest Effort LLC

Licensed under the GPLv3: http://www.gnu.org/licenses/gpl-3.0.html
