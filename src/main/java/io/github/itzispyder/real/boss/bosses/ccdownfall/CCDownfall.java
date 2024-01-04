package io.github.itzispyder.real.boss.bosses.ccdownfall;

import io.github.itzispyder.pdk.utils.misc.SoundPlayer;
import io.github.itzispyder.real.boss.Attack;
import io.github.itzispyder.real.boss.Boss;
import io.github.itzispyder.real.boss.Stage;
import io.github.itzispyder.real.boss.bosses.ccdownfall.attacks.ArrowRainAttack;
import io.github.itzispyder.real.boss.bosses.ccdownfall.attacks.MinionAttack;
import io.github.itzispyder.real.boss.bosses.ccdownfall.attacks.PotionRainAttack;
import io.github.itzispyder.real.boss.bosses.ccdownfall.attacks.VortexStormAttack;
import io.github.itzispyder.real.boss.common.DashAttack;
import io.github.itzispyder.real.utils.DisplayUtils;
import io.github.itzispyder.real.utils.SchedulerUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class CCDownfall extends Boss {

    public CCDownfall() {
        super("&bC&3C's &7Downfall", 300);
    }

    @Override
    @SuppressWarnings("all")
    public void onSpawn(Boss boss, Entity entity) {
        LivingEntity living = (LivingEntity) entity;
        living.getEquipment().setHelmet(new ItemStack(Material.SKELETON_SKULL));
        living.setInvulnerable(true);
        living.setAI(false);
        living.setNoDamageTicks(20);
        living.setMaximumNoDamageTicks(20);
        living.setSilent(true);

        Location loc = entity.getLocation();
        SoundPlayer sound = new SoundPlayer(loc, Sound.ENTITY_SKELETON_HURT, 10, 1);

        SchedulerUtils.later(20 * 5, () -> {
            living.setInvulnerable(false);
            living.setAI(true);

            sound.setSound(Sound.ENTITY_WITHER_SPAWN);
            sound.playWithin(64);

            var particle = DisplayUtils.PARTICLE_FACTORY.apply(Particle.END_ROD);
            DisplayUtils.vortex(loc, 1, particle, 0.001, 0.03, 10);
        });

        SchedulerUtils.loop(10, 10, () -> {
            sound.playWithin(32);
            DisplayUtils.wave(loc.clone().add(0, 0.1, 0), 3, DisplayUtils.FLAME_PARTICLE_FACTORY.apply(true), 0.69420);
        });

        this.initStages();
    }

    public void initStages() {
        Stage stage3 = new Stage("Redemption stage", 1200, 0.3F);
        stage3.registerAttack(Attack.MAKE_SOUND.create(Sound.ENTITY_SKELETON_HURT, 10F, 0.1F, 64.0));
        stage3.registerAttack(new DashAttack());
        stage3.registerAttack(new VortexStormAttack());
        stage3.registerAttack(new VortexStormAttack());
        stage3.registerAttack(new VortexStormAttack());
        stage3.registerAttack(new PotionRainAttack());
        stage3.registerAttack(new PotionRainAttack());
        stage3.registerAttack(new PotionRainAttack());
        stage3.registerAttack(new PotionRainAttack());
        stage3.registerAttack(new PotionRainAttack());
        stage3.registerAttack(new ArrowRainAttack());
        stage3.registerAttack(new MinionAttack());
        stage3.registerAttack(new MinionAttack());
        stage3.registerAttack(new MinionAttack());
        queueNextStage(stage3);

        Stage stage2 = new Stage("Rage stage", 450, 0.3F);
        stage2.registerAttack(Attack.MAKE_SOUND.create(Sound.ENTITY_SKELETON_HURT, 10F, 0.1F, 64.0));
        stage3.registerAttack(new VortexStormAttack());
        stage3.registerAttack(new PotionRainAttack());
        stage3.registerAttack(new PotionRainAttack());
        stage2.registerAttack(new DashAttack());
        stage2.registerAttack(new DashAttack());
        stage2.registerAttack(new MinionAttack());
        stage2.registerAttack(new MinionAttack());
        stage2.registerAttack(new MinionAttack());
        stage2.registerAttack(new ArrowRainAttack());
        queueNextStage(stage2);

        Stage stage1 = getCurrentStage();
        stage1.registerAttack(Attack.MAKE_SOUND.create(Sound.ENTITY_SKELETON_HURT, 10F, 0.1F, 64.0));
        stage1.registerAttack(new ArrowRainAttack());
        stage1.registerAttack(new ArrowRainAttack());
        stage1.registerAttack(new DashAttack());
    }

    @Override
    public void onHurt(Boss boss) {
        SoundPlayer sound = new SoundPlayer(boss.getLocation(), Sound.ENTITY_WITHER_SKELETON_HURT, 10, 0.1F);
        sound.playWithin(64);
    }

    @Override
    public void onAttack(Boss boss, Attack attack) {

    }

    @Override
    public void onDeath(Boss boss) {
        SoundPlayer sound = new SoundPlayer(boss.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 10, 0.1F);
        sound.playWithin(64);

        boss.fakeChat("That... was good. I'll give it to you");
        boss.spawnMinion(LightningStrike.class);
    }

    @Override
    public void onElevate(Boss boss, Stage before, Stage after) {
        boss.fakeChat(color("Dang it, progressing from &7%s&f to &7%s&f").formatted(before.getName(), after.getName()));
    }
}
