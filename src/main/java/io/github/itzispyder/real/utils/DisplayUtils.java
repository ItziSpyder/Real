package io.github.itzispyder.real.utils;

import io.github.itzispyder.pdk.Global;
import io.github.itzispyder.pdk.utils.misc.Randomizer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class DisplayUtils implements Global {

    public static final Function<Particle, Consumer<Location>> PARTICLE_FACTORY = particle -> l -> l.getWorld().spawnParticle(particle, l, 1, 0, 0, 0, 0);
    public static final BiFunction<Color, Float, Consumer<Location>> DUST_PARTICLE_FACTORY = (color, thickness) -> {
        Particle.DustOptions dust = new Particle.DustOptions(color, thickness);
        return l -> l.getWorld().spawnParticle(Particle.REDSTONE, l, 1, 0, 0, 0, 0, dust);
    };
    public static final Function<Boolean, Consumer<Location>> FLAME_PARTICLE_FACTORY = soul -> {
        Particle flame = soul ? Particle.SOUL_FIRE_FLAME : Particle.FLAME;
        return l -> l.getWorld().spawnParticle(flame, l, 1, 0, 0, 0, 0);
    };

    public static void ring(Location loc, double radius, Color color, float thickness) {
        ring(loc, radius, DUST_PARTICLE_FACTORY.apply(color, thickness));
    }

    public static void wave(Location loc, double radius, Color color, float thickness, double gap) {
        wave(loc, radius, DUST_PARTICLE_FACTORY.apply(color, thickness), gap);
    }

    public static void ring(Location loc, double radius, Consumer<Location> action) {
        for (int theta = 0; theta < 360; theta += 3) {
            double x = Math.cos(Math.toRadians(theta)) * radius;
            double z = Math.sin(Math.toRadians(theta)) * radius;
            Location newLoc = loc.clone().add(x, 0, z);
            action.accept(newLoc);
        }
    }

    public static void wave(Location loc, double radius, Consumer<Location> action, double gap) {
        AtomicReference<Double> i = new AtomicReference<>(gap);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance.getPlugin(), () -> {
            if (i.get() >= radius) {
                return;
            }
            ring(loc, i.get(), action);
            i.set(i.get() + gap);
        }, 0, 1);
    }

    public static void disc(Location loc, double radius, Consumer<Location> action, double gap) {
        for (double i = gap; i < radius; i += gap) {
            ring(loc, i, action);
        }
    }

    public static void arc(Location loc, double radius, int angleFrom, int angleTo, Consumer<Location> action) {
        for (int theta = angleFrom; theta < angleTo; theta += 3) {
            double x = Math.cos(Math.toRadians(theta)) * radius;
            double z = Math.sin(Math.toRadians(theta)) * radius;
            Location newLoc = loc.clone().add(x, 0, z);
            action.accept(newLoc);
        }
    }

    public static void fan(Location loc, double radius, int angleFrom, int angleTo, Consumer<Location> action, double gap) {
        for (double i = gap; i < radius; i += gap) {
            arc(loc, i, angleFrom, angleTo, action);
        }
    }

    public static void fanWave(Location loc, double radius, int sections, Consumer<Location> action, double gap) {
        double arcLength = 360.0 / sections;
        AtomicReference<Double> i = new AtomicReference<>(0.0);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance.getPlugin(), () -> {
            if (i.get() >= 360) {
                return;
            }
            double start = i.get();
            fan(loc, radius, (int)start, (int)(start + arcLength), action, gap);
            i.set(i.get() + arcLength);
        }, 0, 5);
    }

    public static void fanWaveRandom(Location loc, double radius, int sections, Consumer<Location> action, double gap) {
        double arcLength = 360.0 / sections;
        List<Double> ints = new ArrayList<>();
        for (double start = 0; start < 360; start += arcLength) {
            ints.add(start);
        }

        AtomicInteger i = new AtomicInteger(0);
        Randomizer random = new Randomizer();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance.getPlugin(), () -> {
            if (i.get() >= sections) {
                return;
            }
            double start = random.getRandomElement(ints);
            ints.remove(start);
            fan(loc, radius, (int)start, (int)(start + arcLength), action, gap);
            i.getAndIncrement();
        }, 0, 5);
    }
}
