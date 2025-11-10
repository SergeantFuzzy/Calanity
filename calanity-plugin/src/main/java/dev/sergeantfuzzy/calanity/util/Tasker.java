package dev.sergeantfuzzy.calanity.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;

/** Simple async/sync task helper. */
public final class Tasker {

    private final Plugin plugin;

    public Tasker(Plugin plugin) {
        this.plugin = plugin;
    }

    public void runSync(Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public <T> CompletableFuture<T> supplyAsync(java.util.concurrent.Callable<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();
        runAsync(() -> {
            try {
                future.complete(supplier.call());
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });
        return future;
    }
}
