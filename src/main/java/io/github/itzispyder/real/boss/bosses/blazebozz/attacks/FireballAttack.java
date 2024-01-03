package io.github.itzispyder.real.boss.bosses.blazebozz.attacks;

import io.github.itzispyder.pdk.utils.misc.Randomizer;
import io.github.itzispyder.pdk.utils.misc.SoundPlayer;
import io.github.itzispyder.real.boss.Attack;
import io.github.itzispyder.real.boss.Boss;
import io.github.itzispyder.real.boss.Stage;
import io.github.itzispyder.real.utils.SchedulerUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class FireballAttack implements Attack {

    @Override
    public void perform(Boss boss, World world, Location loc, List<Player> targets) {
        Stage stage = boss.getCurrentStage();
        Randomizer random = stage.getRandom();

        if (targets.isEmpty()) {
            return;
        }

        Player target = random.getRandomElement(targets);
        LivingEntity livingBoss = (LivingEntity) boss.getEntity();
        Location bossEyes = livingBoss.getEyeLocation();
        SoundPlayer sound = new SoundPlayer(bossEyes, Sound.ENTITY_BLAZE_SHOOT, 10, 0.1F);

        SchedulerUtils.loop(5, 5, () -> {
            if (!boss.exists()) {
                return;
            }
            Fireball shoot = boss.spawnMinion(bossEyes, Fireball.class);
            shoot.setShooter(livingBoss);
            shoot.setVelocity(target.getLocation().toVector().subtract(bossEyes.toVector()).normalize().multiply(0.2));
            shoot.setYield(0);
            sound.playWithin(64);
        });
    }
}
