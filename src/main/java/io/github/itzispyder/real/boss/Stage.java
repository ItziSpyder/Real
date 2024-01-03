package io.github.itzispyder.real.boss;

import io.github.itzispyder.pdk.Global;
import io.github.itzispyder.pdk.utils.MathUtils;
import io.github.itzispyder.pdk.utils.misc.Randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Stage {

    private final String name;
    private final int maxHealth;
    private final float progressTrigger;
    private final List<Attack> attacks;
    private final Randomizer random;

    public Stage(String name, int maxHealth, float progressTrigger, List<Attack> attacks) {
        this.maxHealth = maxHealth;
        this.attacks = attacks;
        this.random = new Randomizer();
        this.name = name;
        this.progressTrigger = (float)MathUtils.clamp(progressTrigger, 0.0, 1.0);
    }

    public Stage(String name, int maxHealth, float progressTrigger, Attack... attacks) {
        this(name, maxHealth, progressTrigger, new ArrayList<>(Arrays.asList(attacks)));
    }

    public void performRandomAttack(Boss boss) {
        if (attacks.isEmpty()) {
            return;
        }
        Attack attack = random.getRandomElement(attacks);
        attack.perform(boss, boss.getWorld(), boss.getLocation(), boss.getNearbyTargets(16));
        boss.onAttack(boss, attack);
    }

    public void registerAttack(Attack attack) {
        attacks.add(attack);
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public List<Attack> getAttacks() {
        return attacks;
    }

    public Randomizer getRandom() {
        return random;
    }

    public String getName() {
        return Global.instance.color(name);
    }

    public float getProgressTrigger() {
        return progressTrigger;
    }
}
