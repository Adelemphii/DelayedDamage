package io.github.adelemphii.delayeddamage.objects;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BossBarRunnable extends BukkitRunnable {

    private final DelayedDamageRunnable delayedDamageRunnable;

    private final BossBar bossBar;

    public BossBarRunnable(DelayedDamageRunnable delayedDamageRunnable) {
        this.delayedDamageRunnable = delayedDamageRunnable;

        float progress = (float) delayedDamageRunnable.getSecondsEdit() / (float) delayedDamageRunnable.getSecondsOriginal();

        this.bossBar = BossBar.bossBar(Component.text(""),
                progress,
                BossBar.Color.RED,
                BossBar.Overlay.NOTCHED_10);
    }

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.showBossBar(bossBar);
        }

        float progress = (float) delayedDamageRunnable.getSecondsEdit() / (float) delayedDamageRunnable.getSecondsOriginal();
        bossBar.progress(progress);
    }
}
