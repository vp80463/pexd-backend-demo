package com.a1stream.common.utils;

import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 *  To improve the performance of method {@link System#currentTimeMillis()} in Unix OS. <br>
 *  It can provide higher accuracy.  More detail please see
 *  <a href="http://pzemtsov.github.io/2017/07/23/the-slow-currenttimemillis.html">The performance problems about 'System.currentTimeMillis'</a>
 *
 * @author YMSLX
 * @version 3.2
 *
 */
public class SystemRealTimeClock {

  private final long period;
  private final AtomicLong now;

  private SystemRealTimeClock(long period) {
      this.period = period;
      this.now = new AtomicLong(System.currentTimeMillis());
      scheduleClockUpdating();
  }

  private static SystemRealTimeClock instance() {
      return InstanceHolder.INSTANCE;
  }

  public static long now() {
      return instance().currentTimeMillis();
  }

  public static String nowDate() {
      return new Timestamp(instance().currentTimeMillis()).toString();
  }

  private void scheduleClockUpdating() {
      ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
          Thread thread = new Thread(runnable, "System Real-time clock");
          thread.setDaemon(true);
          return thread;
      });
      scheduler.scheduleAtFixedRate(() -> now.set(System.currentTimeMillis()), period, period, TimeUnit.MILLISECONDS);
  }

  private long currentTimeMillis() {
      return now.get();
  }

  private static class InstanceHolder {
      public static final SystemRealTimeClock INSTANCE = new SystemRealTimeClock(1);
  }
}
