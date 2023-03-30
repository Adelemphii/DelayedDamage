package io.github.adelemphii.delayeddamage.objects;

import io.github.adelemphii.delayeddamage.DelayedDamage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DelayedDamageRunnable extends BukkitRunnable {

    private final Map<LivingEntity, Double> entityDamages = new HashMap<>();
    private final Map<UUID, Double> playerDamages = new HashMap<>();
    private boolean damaging = false;

    private final Set<LivingEntity> playerDeaths = new HashSet<>();

    private final long secondsOriginal;
    private long secondsEdit;

    private BossBarRunnable bossBarRunnable;

    public DelayedDamageRunnable(long secondsOriginal, boolean bossBar) {
        this.secondsOriginal = secondsOriginal;
        secondsEdit = secondsOriginal;

        if(bossBar) {
            bossBarRunnable = new BossBarRunnable(this);
            bossBarRunnable.runTaskTimer(DelayedDamage.getInstance(), 0, 1);
        } else {
            bossBarRunnable = null;
        }
    }

    @Override
    public void run() {
        if(secondsEdit == 0) {
            for(LivingEntity entity : entityDamages.keySet()) {
                double damage = entityDamages.get(entity);
                damageEntity(entity, damage);
            }

            for(UUID uuid : playerDamages.keySet()) {
                double damage = playerDamages.get(uuid);

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                if(!offlinePlayer.isOnline() || offlinePlayer.getPlayer() == null) {
                    continue;
                }

                Player player = offlinePlayer.getPlayer();
                this.damagePlayer(player, damage);
            }

            secondsEdit = secondsOriginal;
        }
        secondsEdit--;
    }

    public void addDamage(LivingEntity entity, double damage) {
        double toAdd = entityDamages.getOrDefault(entity, 0d) + damage;
        entityDamages.put(entity, toAdd);
    }

    public void addDamage(Player player, double damage) {
        double toAdd = playerDamages.getOrDefault(player.getUniqueId(), 0d) + damage;
        playerDamages.put(player.getUniqueId(), toAdd);
    }

    public boolean isDamaging() {
        return damaging;
    }

    private void damageEntity(LivingEntity livingEntity, double damage) {
        damaging = true;

        livingEntity.damage(damage);
        entityDamages.remove(livingEntity);

        damaging = false;
    }

    private void damagePlayer(Player player, double damage) {
        damaging = true;
        if(willDie(player, damage)) {
            playerDeaths.add(player);
        }

        player.damage(damage);
        playerDamages.remove(player.getUniqueId());

        damaging = false;
    }

    private boolean willDie(LivingEntity livingEntity, double damage) {
        double maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

        return maxHealth - damage <= 0;
    }

    public Set<LivingEntity> getPlayerDeaths() {
        return playerDeaths;
    }

    public void removePlayerDeath(LivingEntity livingEntity) {
        playerDeaths.remove(livingEntity);
    }

    public void removeDamage(LivingEntity livingEntity) {
        this.entityDamages.remove(livingEntity);
    }

    public void removeDamage(Player player) {
        this.playerDamages.remove(player.getUniqueId());
    }

    public long getSecondsEdit() {
        return secondsEdit;
    }

    public long getSecondsOriginal() {
        return secondsOriginal;
    }

    public Map<LivingEntity, Double> getEntityDamages() {
        return entityDamages;
    }

    public Map<UUID, Double> getPlayerDamages() {
        return playerDamages;
    }
}
