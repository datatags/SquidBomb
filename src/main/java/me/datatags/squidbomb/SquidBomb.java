package me.datatags.squidbomb;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SquidBomb extends JavaPlugin {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
    }
}
