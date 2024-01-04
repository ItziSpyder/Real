package io.github.itzispyder.real.boss;

import io.github.itzispyder.pdk.Global;
import io.github.itzispyder.pdk.utils.MathUtils;
import io.github.itzispyder.pdk.utils.ServerUtils;
import io.github.itzispyder.pdk.utils.raytracers.CustomDisplayRaytracer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Boss implements Global {

    private static final Predicate<Entity> IS_TARGET = ent -> ent instanceof Player p && !p.isDead() && (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE);
    private static final Set<Boss> currentBosses = new HashSet<>();
    private final Stack<Stage> stages;
    private final List<Entity> minions;
    private BossBar bossbar;
    private Stage currentStage;
    private String displayName;
    private Entity entity;
    private int progressions;

    protected Boss(String displayName, int maxHealth) {
        this.displayName = displayName;
        this.stages = new Stack<>();
        this.minions = new ArrayList<>();
        this.currentStage = new Stage("Primal Stage", maxHealth, 1.0F, new ArrayList<>());
        this.stages.push(currentStage);
    }

    public static Set<Boss> getCurrentBosses() {
        return currentBosses;
    }
    
    public static Boss getBoss(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return null;
        }
        for (Boss boss : currentBosses) {
            if (boss.getEntity() == entity) {
                return boss;
            }
        }
        return null;
    }
    
    public static boolean isBoss(Entity entity) {
        return getBoss(entity) != null;
    }

    public static Boss getMinionsBoss(Entity entity) {
        if (entity == null) {
            return null;
        }
        for (Boss boss : currentBosses) {
            if (boss.getMinions().contains(entity)) {
                return boss;
            }
        }
        return null;
    }

    public static boolean isMinion(Entity entity) {
        return getMinionsBoss(entity) != null;
    }

    public abstract void onSpawn(Boss boss, Entity entity);

    public abstract void onHurt(Boss boss);

    public abstract void onAttack(Boss boss, Attack attack);

    public abstract void onDeath(Boss boss);

    public abstract void onElevate(Boss boss, Stage before, Stage after);

    @SuppressWarnings("all")
    public <T extends LivingEntity> void spawn(Class<T> entityClass, Location location, Consumer<T> action) {
        if (this.exists()) {
            return;
        }

        T entity = location.getWorld().spawn(location, entityClass);
        entity.registerAttribute(Attribute.GENERIC_MAX_HEALTH);
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(getMaxHealth());
        entity.setHealth(getMaxHealth());
        action.accept(entity);
        entity.setCustomName(color(displayName));
        entity.setCustomNameVisible(true);
        this.entity = entity;
        this.onSpawn(this, entity);

        BossBar bar = BossBar.bossBar(Component.text(color(displayName)), (float)getHealthRatio(), BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);
        bar.addListener(new BossBarListener());
        this.bossbar = bar;

        updateBossBar();
        currentBosses.add(this);
    }

    public <T extends LivingEntity> void spawn(Class<T> entityClass, Location location) {
        this.spawn(entityClass, location, ent -> {});
    }

    public World getWorld() {
        return entity.getWorld();
    }

    public Location getLocation() {
        return entity.getLocation();
    }

    public double getHealth() {
        return entity instanceof LivingEntity le ? le.getHealth() : 0.0;
    }

    public double getMaxHealth() {
        return currentStage == null ? 0.0 : currentStage.getMaxHealth();
    }

    public double getHealthRatio() {
        return MathUtils.clamp(getHealth() / getMaxHealth(), 0.0, 1.0);
    }

    public CustomDisplayRaytracer.Point getPoint() {
        return CustomDisplayRaytracer.blocksInFrontOf(entity.getLocation(), entity.getLocation().getDirection(), 0, true);
    }

    public List<Player> getNearbyTargets(int range) {
        return getPoint().getNearbyEntities(getEntity(), range, false, IS_TARGET).stream().map(ent -> (Player) ent).toList();
    }

    public Entity getEntity() {
        return entity;
    }

    public LivingEntity getLivingEntity() {
        return (LivingEntity) entity;
    }

    public boolean hasSpawned() {
        return entity != null;
    }

    public Stack<Stage> getStages() {
        return stages;
    }

    /**
     * Queues for the next stage
     * @param stage next stage
     */
    public void queueNextStage(Stage stage) {
        Stage peek = null;
        boolean wasEmpty = stages.isEmpty();

        if (!wasEmpty) {
            peek = stages.peek();
            stages.pop();
        }

        stages.push(stage);

        if (!wasEmpty) {
            stages.push(peek);
        }
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public boolean isLastStage() {
        return getStages().size() <= 1;
    }

    public int getProgressions() {
        return progressions;
    }

    @SuppressWarnings("all")
    public void progressStage() {
        if (isLastStage()) {
            return;
        }

        Stage before = currentStage;
        if (currentStage == stages.peek()) {
            stages.pop();
        }
        currentStage = stages.peek();
        progressions++;
        onElevate(this, before, currentStage);

        LivingEntity living = (LivingEntity) entity;
        living.registerAttribute(Attribute.GENERIC_MAX_HEALTH);
        living.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(getMaxHealth());
        living.setHealth(getMaxHealth());
    }

    public void tryProgressStage() {
        if (isLastStage()) {
            return;
        }

        Stage next = stages.get(stages.size() - 2);
        if (getHealthRatio() <= next.getProgressTrigger()) {
            progressStage();
        }
    }

    public String getDisplayName() {
        return color(displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public BossBar getBossbar() {
        return bossbar;
    }

    public void updateBossBar() {
        if (!hasSpawned() || bossbar == null) {
            return;
        }
        bossbar.progress((float)getHealthRatio());
        for (Player player : getNearbyTargets(32)) {
            bossbar.addViewer(player);
        }
    }

    public void removeBossBar() {
        if (!hasSpawned() || bossbar == null) {
            return;
        }
        ServerUtils.forEachPlayer(p -> bossbar.removeViewer(p));
    }

    public <T extends Entity> T spawnMinion(Location loc, Class<T> entityClass) {
        T minion = loc.getWorld().spawn(loc, entityClass);
        minions.add(minion);
        return minion;
    }

    public <T extends Entity> T spawnMinion(Class<T> entityClass) {
        return spawnMinion(getLocation(), entityClass);
    }

    public List<Entity> getMinions() {
        return minions;
    }

    public void removeMinions() {
        minions.stream().filter(Objects::nonNull).forEach(Entity::remove);
    }

    public void remove() {
        removeMinions();

        if (entity != null && !entity.isDead()) {
            entity.remove();
        }

        removeBossBar();
        currentBosses.remove(this);
    }

    public boolean exists() {
        return hasSpawned() && !entity.isDead() && currentBosses.contains(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Boss boss)) {
            return false;
        }
        if (!boss.hasSpawned() || !this.hasSpawned()) {
            return false;
        }
        return boss.getEntity() == this.getEntity();
    }

    public void fakeChat(String msg) {
        ServerUtils.dmEachPlayer("§f<%s§f> %s".formatted(getDisplayName(), msg));
    }

    private class BossBarListener implements BossBar.Listener {
        @Override
        public void bossBarNameChanged(@NotNull BossBar bar, @NotNull Component oldName, @NotNull Component newName) {
            updateBossBar();
        }

        @Override
        public void bossBarProgressChanged(@NotNull BossBar bar, float oldProgress, float newProgress) {
            updateBossBar();
        }

        @Override
        public void bossBarColorChanged(@NotNull BossBar bar, BossBar.@NotNull Color oldColor, BossBar.@NotNull Color newColor) {
            updateBossBar();
        }

        @Override
        public void bossBarOverlayChanged(@NotNull BossBar bar, BossBar.@NotNull Overlay oldOverlay, BossBar.@NotNull Overlay newOverlay) {
            updateBossBar();
        }

        @Override
        public void bossBarFlagsChanged(@NotNull BossBar bar, @NotNull Set<BossBar.Flag> flagsAdded, @NotNull Set<BossBar.Flag> flagsRemoved) {
            updateBossBar();
        }
    }
}
