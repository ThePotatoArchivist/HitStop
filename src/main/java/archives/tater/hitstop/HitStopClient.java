package archives.tater.hitstop;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class HitStopClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(HitStop.MOD_ID, "hitstop_overlay"), (graphics, deltaTracker) -> {
            if (Minecraft.getInstance().level.tickRateManager().isFrozen())
                graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), 0xBfffffff);
        });
    }
}
