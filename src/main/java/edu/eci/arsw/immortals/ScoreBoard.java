package edu.eci.arsw.immortals;

import java.util.concurrent.atomic.AtomicLong;

public final class ScoreBoard {
  private final AtomicLong totalFights = new AtomicLong();
  public void recordFight() { totalFights.incrementAndGet(); }
  public long totalFights() { return totalFights.get(); }
}
