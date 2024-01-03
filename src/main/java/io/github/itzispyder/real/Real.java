package io.github.itzispyder.real;

import io.github.itzispyder.pdk.PDK;
import io.github.itzispyder.real.boss.Boss;
import io.github.itzispyder.real.commands.SpawnBossCmd;
import io.github.itzispyder.real.events.BossEventListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Real extends JavaPlugin {

    @Override
    public void onEnable() {
        PDK.init(this);

        // events
        new BossEventListener().register();

        // commands
        new SpawnBossCmd().register();
    }

    @Override
    public void onDisable() {
        Boss.getCurrentBosses().forEach(Boss::remove);
    }
}
