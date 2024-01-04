package io.github.itzispyder.real.boss.bosses.ccdownfall.attacks;

import io.github.itzispyder.pdk.utils.misc.SoundPlayer;
import io.github.itzispyder.real.boss.Attack;
import io.github.itzispyder.real.boss.Boss;
import io.github.itzispyder.real.utils.SchedulerUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class ArrowRainAttack implements Attack {

    @Override
    public void perform(Boss boss, World world, Location loc, List<Player> targets) {
        if (targets.isEmpty()) {
            return;
        }

        SoundPlayer sound = new SoundPlayer(loc, Sound.ENTITY_SKELETON_SHOOT, 10, 1);

        SchedulerUtils.loop(10, 3, () -> {
            for (int theta = 0; theta < 360; theta += 30) {
                Location bossLoc = boss.getLocation();
                double targetX = bossLoc.getX() + Math.cos(Math.toRadians(theta));
                double targetZ = bossLoc.getZ() + Math.sin(Math.toRadians(theta));
                double targetY = boss.getLivingEntity().getEyeLocation().getY();
                Vector target = new Vector(targetX, targetY, targetZ);
                Vector vec = target.subtract(boss.getLivingEntity().getEyeLocation().toVector()).normalize().multiply(3);

                Arrow arrow = world.spawn(boss.getLivingEntity().getEyeLocation(), Arrow.class);
                arrow.setShooter(boss.getLivingEntity());
                arrow.setDamage(6.9420);
                arrow.setVelocity(vec);
            }
            sound.playWithin(64);
        });
    }
}
