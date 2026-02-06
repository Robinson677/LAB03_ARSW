package edu.eci.arsw.pc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public final class PCApp {
  private PCApp() {
  }

  public static void main(String[] args) throws Exception {
    String mode = System.getProperty("mode", "monitor"); // monitor|spin
    int producers = Integer.getInteger("producers", 1);
    int consumers = Integer.getInteger("consumers", 1);
    int capacity = Integer.getInteger("capacity", 16);
    long prodDelay = Long.getLong("prodDelayMs", 10L);
    long consDelay = Long.getLong("consDelayMs", 10L);
    int duration = Integer.getInteger("durationSec", 20);

    System.out.printf(
        "PCApp mode=%s producers=%d consumers=%d capacity=%d prodDelay=%dms consDelay=%dms duration=%ds%n",
        mode, producers, consumers, capacity, prodDelay, consDelay, duration);

    Object queue;
    if ("spin".equalsIgnoreCase(mode))
      queue = new BusySpinQueue<Long>(capacity);
    else
      queue = new BoundedBuffer<Long>(capacity);

    var exec = Executors.newVirtualThreadPerTaskExecutor();
    List<Producer> prodList = new ArrayList<>();
    List<Consumer> consList = new ArrayList<>();
    AtomicLong produced = new AtomicLong();
    AtomicLong consumed = new AtomicLong();

    for (int i = 0; i < producers; i++) {
      var p = new Producer(queue, produced, prodDelay);
      prodList.add(p);
      exec.submit(p);
    }
    for (int i = 0; i < consumers; i++) {
      var c = new Consumer(queue, consumed, consDelay);
      consList.add(c);
      exec.submit(c);
    }

    Thread.sleep(duration * 1000L);

    prodList.forEach(Producer::stop);
    consList.forEach(Consumer::stop);
    exec.close();

    System.out.printf("Produced=%d Consumed=%d QueueSize=%d%n",
        produced.get(), consumed.get(),
        queue instanceof BusySpinQueue<?> sp ? sp.size() : ((BoundedBuffer<?>) queue).size());

    System.out.println("TIP: Compare CPU with VisualVM: spin (busy-wait) vs monitor (wait/notify).");
  }
}
