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

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;

public class CloseOnFutureErrorListener implements ChannelFutureListener {

  private final Logger log;
  private final String errorMessage;

  // todo: when is it appropriate to use this? can't use for every context.write(), right? :|
  public CloseOnFutureErrorListener(Logger log, String errorMessage) {
    this.log          = log;
    this.errorMessage = errorMessage;
  }

  @Override
  public void operationComplete(ChannelFuture future) throws Exception {
    if (!future.isSuccess()) {
      log.warn(errorMessage, future.cause());
      future.channel().close();
    }
  }

}
