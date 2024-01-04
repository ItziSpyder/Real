package io.github.itzispyder.real.boss.common;

import io.github.itzispyder.pdk.utils.misc.Randomizer;
import io.github.itzispyder.real.boss.Attack;
import io.github.itzispyder.real.boss.Boss;
import io.github.itzispyder.real.boss.Stage;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class DashAttack implements Attack {

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
        Vector vec = target.getLocation().toVector().subtract(bossEyes.toVector()).normalize();

        boss.getEntity().setVelocity(vec);
    }
}
