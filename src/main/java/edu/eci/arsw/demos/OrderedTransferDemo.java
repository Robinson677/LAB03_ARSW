package edu.eci.arsw.demos;

import edu.eci.arsw.core.BankAccount;
import edu.eci.arsw.core.TransferService;
import java.util.concurrent.Executors;

public final class OrderedTransferDemo {
  private OrderedTransferDemo() {}
  public static void run() throws Exception {
    var a = new BankAccount(1, 1000);
    var b = new BankAccount(2, 1000);
    try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
      for (int i=0;i<1000;i++) {
        exec.submit(() -> TransferService.transferOrdered(a, b, 1));
        exec.submit(() -> TransferService.transferOrdered(b, a, 1));
      }
    }
    System.out.println("OrderedTransferDemo finished without deadlock.");
  }
}
