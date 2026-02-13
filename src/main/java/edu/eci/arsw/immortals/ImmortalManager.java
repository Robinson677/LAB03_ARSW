package edu.eci.arsw.immortals;

import edu.eci.arsw.concurrency.PauseController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public final class ImmortalManager implements AutoCloseable {

    private final List<Future<?>> futures = new ArrayList<>();
    private final PauseController controller = new PauseController();
    private final ScoreBoard scoreBoard = new ScoreBoard();

    private ExecutorService exec;
    private final ConcurrentLinkedQueue<Immortal> population = new ConcurrentLinkedQueue<>();

    private final String fightMode;
    private final int initialHealth;
    private final int damage;

    public ImmortalManager(int n, String fightMode,
                           int initialHealth, int damage) {

        this.fightMode = fightMode;
        this.initialHealth = initialHealth;
        this.damage = damage;

        for (int i = 0; i < n; i++) {
            population.add(
                    new Immortal("Immortal-" + i, initialHealth, damage, population, scoreBoard, controller)
            );
        }
    }

    public synchronized void start() {
        if (exec != null) stop();

        exec = Executors.newVirtualThreadPerTaskExecutor();

        for (Immortal im : population) {
            futures.add(exec.submit(im));
        }
    }

    public void pause() { controller.pause(); }

    public void resume() { controller.resume(); }

    public void stop() {
        for (Immortal im : population) im.stop();

        if (exec != null) {
            exec.shutdown();
            try {
                if (!exec.awaitTermination(5, TimeUnit.SECONDS)) {
                    exec.shutdownNow();
                }
            } catch (InterruptedException e) {
                exec.shutdownNow();
                Thread.currentThread().interrupt();
            }
            exec = null;
        }

        futures.clear();
    }

    public int aliveCount() {
        int count = 0;
        for (Immortal im : population) {
            if (im.isAlive()) count++;
        }
        return count;
    }

    public long totalHealth() {
        long sum = 0;

        for (Immortal im : population) {
            sum += im.getHealth();
        }

        return sum;
    }

    public List<Immortal> populationSnapshot() {
        return Collections.unmodifiableList(new ArrayList<>(population));
    }

    public ScoreBoard scoreBoard() { return scoreBoard; }

    public PauseController controller() { return controller; }

    @Override
    public void close() {
        stop();
    }
}
