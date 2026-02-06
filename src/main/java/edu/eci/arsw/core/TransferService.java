package edu.eci.arsw.core;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public final class TransferService {
  public static void transferNaive(BankAccount from, BankAccount to, long amount) {
    Objects.requireNonNull(from); Objects.requireNonNull(to);
    var a = from.lock(); var b = to.lock();
    a.lock();
    try {
      sleepALittle();
      b.lock();
      try { withdrawDeposit(from, to, amount); }
      finally { b.unlock(); }
    } finally { a.unlock(); }
  }
  public static void transferOrdered(BankAccount from, BankAccount to, long amount) {
    Objects.requireNonNull(from); Objects.requireNonNull(to);
    BankAccount first = from.id() < to.id() ? from : to;
    BankAccount second = from.id() < to.id() ? to : from;
    first.lock().lock();
    try {
      second.lock().lock();
      try { withdrawDeposit(from, to, amount); }
      finally { second.lock().unlock(); }
    } finally { first.lock().unlock(); }
  }
  public static void transferTryLock(BankAccount from, BankAccount to, long amount, Duration maxWait) throws InterruptedException {
    Objects.requireNonNull(from); Objects.requireNonNull(to);
    ReentrantLock a = from.lock(); ReentrantLock b = to.lock();
    long deadline = System.nanoTime() + maxWait.toNanos();
    while (System.nanoTime() < deadline) {
      if (a.tryLock(10, TimeUnit.MILLISECONDS)) {
        try {
          if (b.tryLock(10, TimeUnit.MILLISECONDS)) {
            try { withdrawDeposit(from, to, amount); return; }
            finally { b.unlock(); }
          }
        } finally { a.unlock(); }
      }
      Thread.sleep(ThreadLocalRandom.current().nextInt(1, 5));
    }
    throw new InterruptedException("transferTryLock timed out");
  }
  private static void withdrawDeposit(BankAccount from, BankAccount to, long amount) {
    if (from.balance() < amount) throw new IllegalArgumentException("Insufficient funds");
    from.withdrawInternal(amount); to.depositInternal(amount);
  }
  private static void sleepALittle() { try { Thread.sleep(5); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); } }
}
