package me.darki.simplecommandcooldown;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class CommandListener implements Listener {

    final private ConcurrentHashMap<AbstractMap.SimpleEntry<Player, String>, Long> cooldownMap;
    final private ReentrantLock mutex;

    public CommandListener() {
        cooldownMap = new ConcurrentHashMap<>();
        mutex = new ReentrantLock();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();
        String commandName = event.getMessage().split(" ")[0];

        AbstractMap.SimpleEntry<Player, String> pair = new AbstractMap.SimpleEntry<>(player, commandName);

        try {
            this.mutex.lock();

            if (this.cooldownMap.containsKey(pair)) {
                if (System.currentTimeMillis() - this.cooldownMap.get(pair) > SimpleCommandCooldown.cooldown) {
                    this.cooldownMap.remove(pair);
                } else {
                    player.sendMessage(ChatColor.RED + "Please wait a bit before using this command again!");
                    event.setCancelled(true);
                }
            } else {
                this.cooldownMap.put(pair, System.currentTimeMillis());
            }
        } finally {
            this.mutex.unlock();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        for (Map.Entry<AbstractMap.SimpleEntry<Player, String>, Long> entry : this.cooldownMap.entrySet()) {
            if (entry.getKey().getKey() == event.getPlayer())
                this.cooldownMap.remove(entry.getKey());
        }
    }

}
