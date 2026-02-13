package edu.eci.arsw.immortals;

import edu.eci.arsw.concurrency.PauseController;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public final class Immortal implements Runnable {

    private final String name;
    private int health;
    private final int damage;
    private final ConcurrentLinkedQueue<Immortal> population;
    private final ScoreBoard scoreBoard;
    private final PauseController controller;

    private volatile boolean running = true;
    private final ReentrantLock lock = new ReentrantLock();

    public Immortal(String name, int health, int damage,
                    ConcurrentLinkedQueue<Immortal> population,
                    ScoreBoard scoreBoard,
                    PauseController controller) {

        this.name = Objects.requireNonNull(name);
        this.health = health;
        this.damage = damage;
        this.population = Objects.requireNonNull(population);
        this.scoreBoard = Objects.requireNonNull(scoreBoard);
        this.controller = Objects.requireNonNull(controller);
    }

    public String name() { return name; }

    public int getHealth() {
        lock.lock();
        try {
            return health;
        } finally {
            lock.unlock();
        }
    }

    public boolean isAlive() {
        return getHealth() > 0 && running;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running) {

                controller.awaitIfPaused();
                if (!running) break;

                Immortal opponent = pickOpponent();
                if (opponent == null || !opponent.isAlive()) continue;

                fightOrdered(opponent);

                if (getHealth() <= 0) {
                    running = false;
                    population.remove(this);
                    break;
                }

                Thread.sleep(2);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Immortal pickOpponent() {
        if (population.size() <= 1) return null;

        Immortal other;
        do {
            int index = ThreadLocalRandom.current().nextInt(population.size());
            other = population.stream().skip(index).findFirst().orElse(null);
        } while (other == null || other == this);

        return other;
    }

    private void fightOrdered(Immortal other) throws InterruptedException {

        Immortal first = this.name.compareTo(other.name) < 0 ? this : other;
        Immortal second = this.name.compareTo(other.name) < 0 ? other : this;

        boolean success = false;

        while (!success && running) {

            if (first.lock.tryLock(100, TimeUnit.MILLISECONDS)) {
                try {
                    if (second.lock.tryLock(100, TimeUnit.MILLISECONDS)) {
                        try {

                            if (this.health <= 0 || other.health <= 0) return;

                            other.health -= this.damage;
                            this.health += this.damage / 2;

                            scoreBoard.recordFight();
                            success = true;

                        } finally {
                            second.lock.unlock();
                        }
                    }
                } finally {
                    first.lock.unlock();
                }
            }

            if (!success) Thread.sleep(5);
        }
    }
}
