package io.github.itzispyder.real.boss;

import io.github.itzispyder.pdk.utils.misc.SoundPlayer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public interface Attack {

    SoundDefinition<Sound, Float, Float, Double> MAKE_SOUND = (sound, volume, pitch, range) -> (boss, world, loc, targets) -> {
        SoundPlayer soundPlayer = new SoundPlayer(loc, sound, volume, pitch);
        soundPlayer.playWithin(range);
    };

    void perform(Boss boss, World world, Location loc, List<Player> targets);

    @FunctionalInterface
    interface SoundDefinition<S extends Sound, V extends Float, P extends Float, R extends Double> {
        Attack create(S sound, V volume, P pitch, R range);
    }
}
