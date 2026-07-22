package com.riprod.adminly;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.events.RemoveWorldEvent;
import com.hypixel.hytale.server.core.util.Config;
import com.riprod.adminly.restart.AutoRestartConfig;
import com.riprod.adminly.restart.WorldRestartService;

public final class AdminlyPlugin extends JavaPlugin {

    private final Config<AutoRestartConfig> config = withConfig(AutoRestartConfig.CODEC);

    public AdminlyPlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        WorldRestartService service = new WorldRestartService(config);
        getEventRegistry().registerGlobal(RemoveWorldEvent.class, service::onWorldRemoved);
    }
}
