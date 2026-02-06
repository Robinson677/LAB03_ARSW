package edu.eci.arsw.app;

import edu.eci.arsw.demos.DeadlockDemo;
import edu.eci.arsw.demos.OrderedTransferDemo;
import edu.eci.arsw.demos.TryLockTransferDemo;

public final class Main {
  private Main() {}
  public static void main(String[] args) throws Exception {
    String mode = System.getProperty("mode", "ui");
    switch (mode) {
      case "demos" -> {
        String demo = System.getProperty("demo", "2");
        switch (demo) {
          case "1" -> DeadlockDemo.run();
          case "2" -> OrderedTransferDemo.run();
          case "3" -> TryLockTransferDemo.run();
          default -> System.out.println("Use -Ddemo=1|2|3");
        }
      }
      case "immortals", "ui" -> {
        int n = Integer.getInteger("count", 8);
        String fight = System.getProperty("fight", "ordered");
        javax.swing.SwingUtilities.invokeLater(
          () -> new edu.eci.arsw.highlandersim.ControlFrame(n, fight)
        );
      }
      default -> System.out.println("Use -Dmode=immortals|demos|ui");
    }
  }
}
