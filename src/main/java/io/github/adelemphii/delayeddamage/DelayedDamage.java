package io.github.adelemphii.delayeddamage;

import io.github.adelemphii.delayeddamage.listeners.DamageListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class DelayedDamage extends JavaPlugin {

    private static DelayedDamage instance;

    @Override
    public void onEnable() {
        instance = this;

        getServer().getPluginManager().registerEvents(new DamageListener(this), this);
    }

    @Override
    public void onDisable() {
    }

    public static DelayedDamage getInstance() {
        return instance;
    }
}
