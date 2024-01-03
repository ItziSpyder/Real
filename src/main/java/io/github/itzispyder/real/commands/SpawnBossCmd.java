package io.github.itzispyder.real.commands;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.itzispyder.real.boss.bosses.blazebozz.BlazeBoss;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Player;

@CommandRegistry(value = "spawnboss", usage = "/spawnboss", permission = @Permission("real.cmd.boss"), playersOnly = true)
public class SpawnBossCmd implements CustomCommand {

    @Override
    public void dispatchCommand(CommandSender sender, Args args) {
        Player player = (Player) sender;
        BlazeBoss boss = new BlazeBoss();
        boss.spawn(Blaze.class, player.getLocation());
    }

    @Override
    public void dispatchCompletions(CompletionBuilder b) {

    }
}
