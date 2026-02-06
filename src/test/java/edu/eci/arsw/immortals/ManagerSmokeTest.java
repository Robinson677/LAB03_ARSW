package edu.eci.arsw.immortals;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ManagerSmokeTest {
  @Test void startsAndStops() throws Exception {
    var m = new ImmortalManager(8, "ordered", 100, 10);
    m.start();
    Thread.sleep(50);
    m.pause();
    long sum = m.totalHealth();
    m.resume();
    m.stop();
    assertTrue(sum > 0);
  }
}
