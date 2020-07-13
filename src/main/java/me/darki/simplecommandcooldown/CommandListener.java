package me.darki.simplecommandcooldown;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class CommandListener implements Listener {

    final private ConcurrentHashMap<AbstractMap.SimpleEntry<UUID, String>, Long> cooldownMap;
    final private ReentrantLock mutex;

    public CommandListener() {
        cooldownMap = new ConcurrentHashMap<>();
        mutex = new ReentrantLock();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();
        String commandName = event.getMessage().split(" ")[0];

        AbstractMap.SimpleEntry<UUID, String> pair = new AbstractMap.SimpleEntry<>(player.getUniqueId(), commandName);

        try {

            mutex.lock();

            if (cooldownMap.containsKey(pair)) {
                if (System.currentTimeMillis() - cooldownMap.get(pair) > SimpleCommandCooldown.cooldown) {
                    cooldownMap.remove(pair);
                } else {
                    player.sendMessage(ChatColor.RED + "Please wait a bit before using this command again!");
                    event.setCancelled(true);
                }
            } else {
                cooldownMap.put(pair, System.currentTimeMillis());
            }

        } finally {
            mutex.unlock();
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        cooldownMap.forEach((data, timeout) -> {
            if (data.getKey().equals(event.getPlayer().getUniqueId()))
                cooldownMap.remove(data);
        });
    }

}
