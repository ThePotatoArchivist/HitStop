package archives.tater.hitstop;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HitStop implements ModInitializer {
	public static final String MOD_ID = "hitstop";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static float preHitstopTickRate = 0;
	public static int hitstopTicks = 0;

	public static void runHitStop(MinecraftServer server, int ticks, int rate) {
		if (hitstopTicks > 0)
			return;
		preHitstopTickRate = server.tickRateManager().tickrate();
		hitstopTicks = ticks;
		if (rate > 0)
			server.tickRateManager().setTickRate(rate);
		else
			server.tickRateManager().setFrozen(true);
	}

	private static void onDamage(LivingEntity entity, DamageSource source) {
		if(source.getEntity() instanceof Player && (source.is(DamageTypes.MACE_SMASH) || source.is(DamageTypes.SPEAR))) {
			runHitStop(entity.level().getServer(), 8, 0);
		}
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (hitstopTicks > 0) {
				hitstopTicks--;
				if (hitstopTicks <= 0) {
					server.tickRateManager().setTickRate(preHitstopTickRate);
					server.tickRateManager().setFrozen(false);
					preHitstopTickRate = 0;
				}
			}
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(_ -> {
			preHitstopTickRate = 0;
			hitstopTicks = 0;
		});

		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((level, entity, killedEntity, damageSource) -> {
			if (entity instanceof Player && killedEntity.getMaxHealth() > 50) {
				runHitStop(level.getServer(), 5, 3);
			}
		});

		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, _, _, blocked) -> {
			if (!blocked) onDamage(entity, source);
		});
		ServerLivingEntityEvents.AFTER_DEATH.register(HitStop::onDamage);
	}
}