package me.darki.simplecommandcooldown;

import javafx.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class CommandListener implements Listener {
    final private ConcurrentHashMap<Pair<Player, String>, Long> cooldownMap;
    final private ReentrantLock mutex = new ReentrantLock();

    public CommandListener() {
        cooldownMap = new ConcurrentHashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String commandName = event.getMessage().split(" ")[0];

        Pair<Player, String> pair = new Pair<>(player, commandName);

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
        Player player = event.getPlayer();

        for (Map.Entry<Pair<Player, String>, Long> entry : this.cooldownMap.entrySet()) {
            if (entry.getKey().getKey() == player)
                this.cooldownMap.remove(entry.getKey());
        }
    }
}
