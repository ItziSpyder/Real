package io.github.itzispyder.real.events;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import io.github.itzispyder.pdk.events.CustomListener;
import io.github.itzispyder.real.boss.Boss;
import io.github.itzispyder.real.boss.Stage;
import io.github.itzispyder.real.utils.SchedulerUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class BossEventListener implements CustomListener {

    @Override
    public CustomListener register() {
        SchedulerUtils.repeat(1, () -> {
            if (Math.random() > 0.05) {
                return;
            }
            for (Boss boss : Boss.getCurrentBosses()) {
                if (Math.random() > 0.45) {
                    continue;
                }
                if (boss.getNearbyTargets(16).isEmpty()) {
                    continue;
                }
                Stage stage = boss.getCurrentStage();
                stage.performRandomAttack(boss);
            }
        });
        return CustomListener.super.register();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        Entity ent = e.getEntity();
        Boss boss = Boss.getBoss(ent);

        if (boss == null) {
            return;
        }

        boss.onHurt(boss);
        boss.updateBossBar();
        boss.tryProgressStage();
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        Entity ent = e.getEntity();
        Boss boss = Boss.getBoss(ent);

        if (boss == null) {
            return;
        }

        boss.onDeath(boss);
        boss.remove();
    }

    @EventHandler
    public void onRemove(EntityRemoveFromWorldEvent e) {
        Entity ent = e.getEntity();
        Boss boss = Boss.getBoss(ent);

        if (boss == null) {
            return;
        }

        boss.remove();
    }

    @EventHandler
    public void onLandProjectTile(ProjectileHitEvent e) {
        Entity ent = e.getEntity();
        Boss boss = Boss.getMinionsBoss(ent);

        if (boss == null) {
            return;
        }

        Location loc = ent.getLocation();
        World world = ent.getWorld();

        if (ent instanceof Fireball) {
            world.createExplosion(loc, 3, true, false, boss.getEntity());
        }
    }
}
