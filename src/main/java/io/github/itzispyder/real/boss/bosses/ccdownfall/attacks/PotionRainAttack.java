package io.github.itzispyder.real.boss.bosses.ccdownfall.attacks;

import io.github.itzispyder.pdk.utils.misc.Randomizer;
import io.github.itzispyder.pdk.utils.misc.SoundPlayer;
import io.github.itzispyder.real.boss.Attack;
import io.github.itzispyder.real.boss.Boss;
import io.github.itzispyder.real.boss.Stage;
import io.github.itzispyder.real.utils.SchedulerUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

public class PotionRainAttack implements Attack {

    @Override
    public void perform(Boss boss, World world, Location loc, List<Player> targets) {
        if (targets.isEmpty()) {
            return;
        }

        Stage stage = boss.getCurrentStage();
        Randomizer random = stage.getRandom();
        SoundPlayer sound = new SoundPlayer(loc, Sound.ENTITY_SNOWBALL_THROW, 10, 0.1F);

        SchedulerUtils.loop(3, 20, () -> {
            if (!boss.exists()) {
                return;
            }

            Location eyes = boss.getLivingEntity().getEyeLocation();
            double x = random.getRandomDouble(-0.3, 0.3);
            double z = random.getRandomDouble(-0.3, 0.3);
            ThrownPotion potion = boss.spawnMinion(eyes, ThrownPotion.class);
            PotionMeta meta = potion.getPotionMeta();
            PotionEffect harm = new PotionEffect(PotionEffectType.HARM, 20, 2, false, true);

            meta.addCustomEffect(harm, true);
            meta.setColor(Color.BLACK);
            potion.setPotionMeta(meta);
            potion.setShooter(boss.getLivingEntity());
            potion.setVelocity(new Vector(x, 0.69420, z));

            world.spawnParticle(Particle.SWEEP_ATTACK, eyes.clone().add(x, 0, z), 1, 0, 0, 0, 0);
            sound.playWithin(64);
        });
    }
}
