package edu.eci.arsw.demos;

import edu.eci.arsw.core.BankAccount;
import edu.eci.arsw.core.TransferService;
import java.time.Duration;
import java.util.concurrent.Executors;

public final class TryLockTransferDemo {
  private TryLockTransferDemo() {}
  public static void run() throws Exception {
    var a = new BankAccount(1, 1000);
    var b = new BankAccount(2, 1000);
    try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
      for (int i=0;i<1000;i++) {
        exec.submit(() -> { try { TransferService.transferTryLock(a, b, 1, Duration.ofSeconds(5)); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } });
        exec.submit(() -> { try { TransferService.transferTryLock(b, a, 1, Duration.ofSeconds(5)); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } });
      }
    }
    System.out.println("TryLockTransferDemo finished without deadlock (may retry under contention).");
  }
}
