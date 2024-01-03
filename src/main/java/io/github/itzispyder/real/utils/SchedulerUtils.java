package io.github.itzispyder.real.utils;

import io.github.itzispyder.pdk.Global;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.atomic.AtomicInteger;

public class SchedulerUtils implements Global {

    public static BukkitScheduler sch() {
        return Bukkit.getScheduler();
    }

    public static void later(long delay, Runnable task) {
        sch().runTaskLater(instance.getPlugin(), task, delay);
    }

    public static void repeatLater(long delay, long period, Runnable task) {
        sch().scheduleSyncRepeatingTask(instance.getPlugin(), task, delay, period);
    }

    public static void repeat(long period, Runnable task) {
        repeatLater(0, period, task);
    }

    public static void loopLater(long delay, long period, int iterations, Runnable task) {
        AtomicInteger i = new AtomicInteger(0);
        AtomicInteger id = new AtomicInteger();

        id.set(sch().scheduleSyncRepeatingTask(instance.getPlugin(), () -> {
            if (i.get() >= iterations) {
                sch().cancelTask(id.get());
                return;
            }
            task.run();
            i.getAndIncrement();
        }, delay, period));
    }

    public static void loop(long period, int iterations, Runnable task) {
        loopLater(0, period, iterations, task);
    }
}
