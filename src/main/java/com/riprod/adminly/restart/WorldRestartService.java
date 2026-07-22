package com.riprod.adminly.restart;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.events.RemoveWorldEvent;
import com.hypixel.hytale.server.core.util.Config;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public final class WorldRestartService {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private final Config<AutoRestartConfig> config;
    private final Map<String, AtomicInteger> attempts = new ConcurrentHashMap<>();

    public WorldRestartService(Config<AutoRestartConfig> config) {
        this.config = config;
    }

    public void onWorldRemoved(RemoveWorldEvent event) {
        if (event.getRemovalReason() != RemoveWorldEvent.RemovalReason.EXCEPTIONAL) return;

        AutoRestartConfig cfg = config.get();
        if (!cfg.isEnabled()) return;

        World world = event.getWorld();
        String name = world.getName();
        // universe maps are keyed by lowercased name, so track strikes the same way
        String key = name.toLowerCase(Locale.ROOT);
        Object cause = world.getPossibleFailureCause();

        int count = attempts.computeIfAbsent(key, k -> new AtomicInteger()).incrementAndGet();
        if (count > cfg.getMaxRetries()) {
            LOGGER.at(Level.SEVERE).log("World %s reached max auto-restart attempts (%d); giving up (cause: %s)", name, cfg.getMaxRetries(), cause);
            return;
        }

        LOGGER.at(Level.WARNING).log("World %s crashed (attempt %d/%d, cause: %s); restarting in %ds", name, count, cfg.getMaxRetries(), cause, cfg.getRestartDelaySeconds());
        // the crash event fires before the universe removes the world from its map, so the
        // reload has to be deferred until removal completes; the delay doubles as the timeout
        HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> reload(name, key), cfg.getRestartDelaySeconds(), TimeUnit.SECONDS);
    }

    private void reload(String name, String key) {
        try {
            Universe.get().loadWorld(name).whenComplete((world, error) -> {
                if (error != null) {
                    LOGGER.at(Level.SEVERE).withCause(error).log("Failed to auto-restart world %s", name);
                } else {
                    LOGGER.at(Level.INFO).log("Auto-restarted world %s", name);
                    scheduleHealthReset(key);
                }
            });
        } catch (IllegalArgumentException e) {
            LOGGER.at(Level.WARNING).log("Cannot auto-restart world %s: %s", name, e.getMessage());
        }
    }

    private void scheduleHealthReset(String key) {
        long window = config.get().getRetryWindowSeconds();
        HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> {
            World world = Universe.get().getWorld(key);
            if (world != null && world.isAlive()) {
                attempts.remove(key);
            }
        }, window, TimeUnit.SECONDS);
    }
}
