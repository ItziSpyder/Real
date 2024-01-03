package io.github.itzispyder.real.boss;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public interface Attack {

    void perform(Boss boss, World world, Location loc, List<Player> targets);
}
