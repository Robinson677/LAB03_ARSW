package edu.eci.arsw.core;

import java.util.concurrent.locks.ReentrantLock;

public final class BankAccount {
  private final long id;
  private long balance;
  private final ReentrantLock lock = new ReentrantLock();

  public BankAccount(long id, long initial) { this.id = id; this.balance = initial; }
  public long id() { return id; }
  public long balance() { return balance; }
  public ReentrantLock lock() { return lock; }
  void depositInternal(long amount) { balance += amount; }
  void withdrawInternal(long amount) { balance -= amount; }
}
