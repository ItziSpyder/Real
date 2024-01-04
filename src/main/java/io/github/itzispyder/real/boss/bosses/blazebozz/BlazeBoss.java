package io.github.itzispyder.real.boss.bosses.blazebozz;

import io.github.itzispyder.pdk.utils.misc.SoundPlayer;
import io.github.itzispyder.real.boss.Attack;
import io.github.itzispyder.real.boss.Boss;
import io.github.itzispyder.real.boss.Stage;
import io.github.itzispyder.real.boss.bosses.blazebozz.attacks.FireballAttack;
import io.github.itzispyder.real.boss.bosses.blazebozz.attacks.MinionAttack;
import io.github.itzispyder.real.boss.common.DashAttack;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class BlazeBoss extends Boss {

    public BlazeBoss() {
        super("&6Blaze Bozz", 250);
    }

    @Override
    public void onSpawn(Boss boss, Entity entity) {
        LivingEntity living = (LivingEntity) entity;
        living.setInvulnerable(true);
        living.setAI(false);
        living.setNoDamageTicks(20);
        living.setMaximumNoDamageTicks(20);
        living.setSilent(true);

        SoundPlayer sound = new SoundPlayer(living.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 10, 0.1F);
        sound.playWithin(64);

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            living.setInvulnerable(false);
            living.setAI(true);

            sound.setSound(Sound.ENTITY_WITHER_SPAWN);
            sound.playWithin(64);
        }, 60);

        this.initStages();
    }

    private void initStages() {
        Stage stage1 = getCurrentStage();
        stage1.registerAttack(new DashAttack());
        stage1.registerAttack(new FireballAttack());

        Stage stage3 = new Stage("Final Stage", 500, 0.1F);
        stage3.registerAttack(new MinionAttack());
        stage3.registerAttack(new MinionAttack());
        stage3.registerAttack(new MinionAttack());
        stage3.registerAttack(new DashAttack());
        stage3.registerAttack(new DashAttack());
        stage3.registerAttack(new FireballAttack());
        stage3.registerAttack(new FireballAttack());
        stage3.registerAttack(new FireballAttack());
        stage3.registerAttack(new FireballAttack());
        queueNextStage(stage3);

        Stage stage2 = new Stage("Secondary Stage", 300, 0.3F);
        stage2.registerAttack(new MinionAttack());
        stage2.registerAttack(new DashAttack());
        stage2.registerAttack(new DashAttack());
        stage2.registerAttack(new DashAttack());
        stage2.registerAttack(new FireballAttack());
        stage2.registerAttack(new FireballAttack());
        queueNextStage(stage2);
    }

    @Override
    public void onHurt(Boss boss) {
        SoundPlayer sound = new SoundPlayer(boss.getLocation(), Sound.ENTITY_BLAZE_HURT, 10, 0.1F);
        sound.playWithin(64);
    }

    @Override
    public void onAttack(Boss boss, Attack attack) {

    }

    @Override
    public void onDeath(Boss boss) {
        SoundPlayer sound = new SoundPlayer(boss.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 10, 0.1F);
        sound.playWithin(64);

        boss.fakeChat("Noooooo... you BEAT ME- niggers niggas niggers");
    }

    @Override
    public void onElevate(Boss boss, Stage before, Stage after) {
        boss.fakeChat(color("Dang it, progressing from &7%s&f to &7%s&f").formatted(before.getName(), after.getName()));
    }
}
