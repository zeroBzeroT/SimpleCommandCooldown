package me.darki.simplecommandcooldown;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleCommandCooldown extends JavaPlugin {

    public static long cooldown = 0L;
    public static String message = "";

    public void onEnable() {
        initConfig();
        Bukkit.getServer().getPluginManager().registerEvents(new CommandListener(), this);
    }

    private void initConfig() {
        getConfig().addDefault("cooldown", 3000);
        getConfig().addDefault("message", "Please wait a bit before using this command again!");
        getConfig().options().copyDefaults(true);
        saveConfig();
        cooldown = getConfig().getLong("cooldown");
        message = getConfig().getString("message");
    }

}
