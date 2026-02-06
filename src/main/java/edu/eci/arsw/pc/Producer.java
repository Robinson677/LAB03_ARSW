package edu.eci.arsw.pc;

import java.util.concurrent.atomic.AtomicLong;

public final class Producer implements Runnable {
  private final AtomicLong counter;
  private final long delayMs;
  private final Object queue; // BusySpinQueue<?> or BoundedBuffer<?>
  private volatile boolean running = true;

  public Producer(Object queue, AtomicLong counter, long delayMs) {
    this.queue = queue;
    this.counter = counter;
    this.delayMs = delayMs;
  }

  public void stop() {
    running = false;
  }

  @Override
  public void run() {
    long i = 0;
    try {
      while (running) {
        if (queue instanceof BusySpinQueue<?> sp) {
          @SuppressWarnings("unchecked")
          BusySpinQueue<Long> q = (BusySpinQueue<Long>) sp;
          q.put(i);
        } else if (queue instanceof BoundedBuffer<?> bb) {
          @SuppressWarnings("unchecked")
          BoundedBuffer<Long> q = (BoundedBuffer<Long>) bb;
          q.put(i);
        }
        counter.incrementAndGet();
        if (delayMs > 0)
          Thread.sleep(delayMs);
        i++;
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
  }
}
