package me.darki.simplecommandcooldown;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleCommandCooldown extends JavaPlugin {

    public static long cooldown = 0L;

    public void onEnable() {

        getConfig().addDefault("cooldown", 3000);
        getConfig().options().copyDefaults(true);
        saveConfig();

        Bukkit.getServer().getPluginManager().registerEvents(new CommandListener(), this);
        cooldown = getConfig().getLong("cooldown");

    }

}
