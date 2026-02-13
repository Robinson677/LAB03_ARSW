package edu.eci.arsw.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public final class PauseController {
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition unpaused = lock.newCondition();
  private final Condition allPaused = lock.newCondition();
  
  private volatile boolean paused = false;
  private int pausedThreads = 0;
  private int totalThreads = 0;

  public void pause() { lock.lock(); try { paused = true; } finally { lock.unlock(); } }
  public void resume() { lock.lock(); try { paused = false; pausedThreads=0; unpaused.signalAll(); } finally { lock.unlock(); } }
  public boolean paused() { return paused; }
  public void setTotalThreads(int totalThreads) { 
    lock.lock();
    try { this.totalThreads = totalThreads; }
    finally { lock.unlock(); }
  }
  public void awaitIfPaused() throws InterruptedException {
    lock.lock();
    try { 
      while (paused){
        pausedThreads++;
        if (pausedThreads == totalThreads) {
          unpaused.signalAll();
      }
      unpaused.await();
      pausedThreads--;
      }
    }finally { lock.unlock(); }
  }
  public void awaitAllPaused() throws InterruptedException {
    lock.lock();
    try {
      while (pausedThreads < totalThreads) {
        allPaused.await();
      }
    } finally {
      lock.unlock();
    }
  }
}