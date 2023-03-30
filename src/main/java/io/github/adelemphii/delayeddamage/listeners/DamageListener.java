package io.github.adelemphii.delayeddamage.listeners;

import io.github.adelemphii.delayeddamage.DelayedDamage;
import io.github.adelemphii.delayeddamage.objects.DelayedDamageRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DamageListener implements Listener {

    private final List<Component> deathMessages = new ArrayList<>(List.of(
            Component.text("Oh? %s thought the server was lagging? No."),
            Component.text("%s has been disconnected from the game of life."),
            Component.text("RIP in pieces, %s."),
            Component.text("Looks like %s reached the end of their journey."),
            Component.text("Oops, looks like %s's health bar was too short."),
            Component.text("Don't worry, %s will respawn in a better place."),
            Component.text("%s died, but at least %s will be remembered...maybe."),
            Component.text("Looks like %s couldn't handle the heat of the game."),
            Component.text("Looks like %s needs to work on their survival skills."),
            Component.text("Looks like %s's time on this server has expired."),
            Component.text("Another one bites the dust, and it's %s this time."),
            Component.text("Looks like %s got a taste of their own medicine."),
            Component.text("That was a real heartbreaker, %s."),
            Component.text("Looks like %s couldn't dodge that one.")
    ));

    private final DelayedDamageRunnable runnable;

    public DamageListener(DelayedDamage plugin) {
        long timeInSeconds = 10;
        this.runnable = new DelayedDamageRunnable(timeInSeconds, true);

        runnable.runTaskTimer(plugin, timeInSeconds * 20, 20);
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        double damage = event.getFinalDamage();
        Entity entity = event.getEntity();
        if(!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        if(!runnable.isDamaging()) {
            if(entity instanceof Player player) {
                runnable.addDamage(player, damage);
            } else {
                runnable.addDamage(livingEntity, damage);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if(runnable.getPlayerDeaths().contains(player)) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            Component component = deathMessages.get(random.nextInt(deathMessages.size())).color(NamedTextColor.RED);
            component = component.replaceText(builder -> {
                builder.matchLiteral("%s");
                builder.replacement(Component.text(player.getName()).color(NamedTextColor.AQUA));
            });

            event.deathMessage(component);
            runnable.removePlayerDeath(player);
            runnable.removeDamage(player);
        }
    }
}
