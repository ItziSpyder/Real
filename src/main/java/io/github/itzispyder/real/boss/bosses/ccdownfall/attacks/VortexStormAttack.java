package io.github.itzispyder.real.boss.bosses.ccdownfall.attacks;

import io.github.itzispyder.pdk.utils.misc.Randomizer;
import io.github.itzispyder.pdk.utils.misc.SoundPlayer;
import io.github.itzispyder.real.boss.Attack;
import io.github.itzispyder.real.boss.Boss;
import io.github.itzispyder.real.boss.Stage;
import io.github.itzispyder.real.utils.DisplayUtils;
import io.github.itzispyder.real.utils.SchedulerUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class VortexStormAttack implements Attack {

    @Override
    public void perform(Boss boss, World world, Location loc, List<Player> targets) {
        if (targets.isEmpty()) {
            return;
        }

        Stage stage = boss.getCurrentStage();
        Randomizer random = stage.getRandom();
        SoundPlayer sound = new SoundPlayer(loc, Sound.ENTITY_WARDEN_SONIC_BOOM, 10, 1);

        SchedulerUtils.loop(10, 5, () -> {
            double x = random.getRandomDouble(-10, 10);
            double z = random.getRandomDouble(-10, 10);
            Location l = loc.clone().add(x, 0, z);
            var particle = DisplayUtils.PARTICLE_FACTORY.apply(Particle.DRIPPING_OBSIDIAN_TEAR);

            DisplayUtils.wave(loc.clone().add(0, 6.9, 0), 10, DisplayUtils.PARTICLE_FACTORY.apply(Particle.SOUL), 1);
            DisplayUtils.vortex(l, 0.1, particle, 0.005, 0.03, 10);
            DisplayUtils.beam(l, DisplayUtils.FLAME_PARTICLE_FACTORY.apply(true), 0.5, 10);

            sound.playWithin(64);
            targets.forEach(p -> p.damage(2, boss.getEntity()));
        });
    }
}
