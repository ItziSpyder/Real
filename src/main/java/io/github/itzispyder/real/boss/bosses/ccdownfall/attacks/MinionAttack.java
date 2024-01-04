package io.github.itzispyder.real.boss.bosses.ccdownfall.attacks;

import io.github.itzispyder.pdk.utils.misc.Randomizer;
import io.github.itzispyder.pdk.utils.misc.SoundPlayer;
import io.github.itzispyder.real.boss.Attack;
import io.github.itzispyder.real.boss.Boss;
import io.github.itzispyder.real.boss.Stage;
import io.github.itzispyder.real.utils.SchedulerUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public class MinionAttack implements Attack {

    @Override
    @SuppressWarnings("all")
    public void perform(Boss boss, World world, Location loc, List<Player> targets) {
        if (targets.isEmpty()) {
            return;
        }

        Stage stage = boss.getCurrentStage();
        Randomizer random = stage.getRandom();
        int count = random.getRandomInt(2, 5);
        SoundPlayer sound = new SoundPlayer(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);

        for (int i = 0; i < count; i++) {
            double x = random.getRandomDouble(-1, 1);
            double z = random.getRandomDouble(-1, 1);

            Skeleton minion = boss.spawnMinion(loc, Skeleton.class);
            minion.getEquipment().setHelmet(new ItemStack(Material.SKELETON_SKULL));
            minion.setCustomName(boss.getDisplayName());
            minion.setCustomNameVisible(true);
            minion.setVelocity(new Vector(x, 0, z));
            minion.setTarget(random.getRandomElement(targets));
        }

        if (Math.random() < 0.69420) {
            SchedulerUtils.later(60, () -> {
                boss.getMinions().forEach(minion -> {
                    if (minion.isDead()) {
                        return;
                    }
                    minion.setInvulnerable(true);
                    minion.teleport(boss.getLocation());
                    SchedulerUtils.later(10, minion::remove);
                });
                sound.playWithin(64);
            });
        }

        sound.playWithin(64);
    }
}
