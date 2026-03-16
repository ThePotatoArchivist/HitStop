package archives.tater.hitstop;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;

public class HitStop implements ModInitializer {
	public static final String MOD_ID = "hitstop";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static float preHitstopTickRate = 0;
	public static int hitstopTicks = 0;

	public static void runHitStop(MinecraftServer server, int ticks, int rate) {
		if (ticks <= 0 || rate == 0 && ticks < 5) return;
		if (hitstopTicks > 0)
			return;
		preHitstopTickRate = server.tickRateManager().tickrate();
		hitstopTicks = ticks;
		if (rate > 0)
			server.tickRateManager().setTickRate(rate);
		else
			server.tickRateManager().setFrozen(true);
	}

	private static int getDamageHitstopTicks(LivingEntity entity, DamageSource source, float originalDamage) {
		if (source.is(DamageTypes.MACE_SMASH)) return max(5, (int) (originalDamage / 6));
//		if (source.is(DamageTypes.SPEAR) && source.getEntity() instanceof LivingEntity livingEntity && livingEntity.isUsingItem()) return 0;
//		if (entity instanceof Enemy) return min(20, (int) ((originalDamage / max(20, entity.getMaxHealth()) - 1) * 15));
		return 0;
	}

	public static void onDamage(LivingEntity entity, DamageSource source, float originalDamage, float amount, boolean blocked) {
        if (!(source.getEntity() instanceof Player)) return;
		runHitStop(
				requireNonNull(entity.level().getServer()),
				getDamageHitstopTicks(entity, source, originalDamage),
				0
		);
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
			if (entity instanceof Player && killedEntity instanceof Enemy)
                runHitStop(level.getServer(), min(10, (int) (killedEntity.getMaxHealth() - 50) / 10), 3);
		});
	}
}