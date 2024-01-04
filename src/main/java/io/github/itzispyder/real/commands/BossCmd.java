package io.github.itzispyder.real.commands;

import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.Permission;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.itzispyder.real.boss.Boss;
import io.github.itzispyder.real.boss.bosses.blazebozz.BlazeBoss;
import io.github.itzispyder.real.boss.bosses.ccdownfall.CCDownfall;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;

@CommandRegistry(value = "boss", usage = "/boss", permission = @Permission("real.cmd.boss"), playersOnly = true)
public class BossCmd implements CustomCommand {

    @Override
    public void dispatchCommand(CommandSender sender, Args args) {
        Player player = (Player) sender;
        Location loc = player.getLocation();

        if (args.match(0, "killall")) {
            Boss.getCurrentBosses().forEach(Boss::remove);
        }
        else if (args.match(0, "spawn")) {
            switch (args.get(1).toString()) {
                case "blazebozz" -> new BlazeBoss().spawn(Blaze.class, loc);
                case "ccdownfall" -> new CCDownfall().spawn(Skeleton.class, loc);
                default -> error(sender, "Boss not found.");
            }
        }
        else {
            error(sender, "Command not found.");
        }
    }

    @Override
    public void dispatchCompletions(CompletionBuilder b) {
        b.then(b.arg("spawn")
                .then(b.arg("blazebozz", "ccdownfall")));
        b.then(b.arg("killall"));
    }
}
