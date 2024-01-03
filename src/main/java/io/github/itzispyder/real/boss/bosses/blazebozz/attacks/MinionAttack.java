package io.github.itzispyder.real.boss.bosses.blazebozz.attacks;

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
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;

import java.util.List;

public class MinionAttack implements Attack {

    @Override
    public void perform(Boss boss, World world, Location loc, List<Player> targets) {
        Stage stage = boss.getCurrentStage();
        Randomizer random = stage.getRandom();
        int radius = 15;
        int sections = 10;
        Location up = loc.clone().add(0, 5, 0);
        SoundPlayer sound = new SoundPlayer(loc, Sound.ENTITY_WITHER_SPAWN, 10, 1);

        sound.playWithin(64);
        DisplayUtils.wave(up, radius, DisplayUtils.FLAME_PARTICLE_FACTORY.apply(false), 0.5);

        sound.changePlayer(up, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 10, 0.1F);
        sound.repeatAll(64, sections, 5);
        DisplayUtils.fanWaveRandom(up, radius, sections, DisplayUtils.PARTICLE_FACTORY.apply(Particle.SOUL), 0.5);

        SchedulerUtils.loop(5, sections, () -> {
            if (!boss.exists()) {
                return;
            }
            double x = random.getRandomDouble(-radius, radius);
            double z = random.getRandomDouble(-radius, radius);
            Location spawnLoc = up.clone().add(x, 0, z);
            Location spawnLocFloor = loc.clone().add(x, 0, z);

            boss.spawnMinion(spawnLoc, WitherSkeleton.class);
            boss.spawnMinion(spawnLocFloor, LightningStrike.class);
        });
    }
}
