package archives.tater.hitstop.mixin;

import archives.tater.hitstop.HitStop;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "hurtServer", at = @At("TAIL"))
    private void afterDamage(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, @Local(name = "originalDamage") float originalDamage, @Local(name = "blocked") boolean blocked) {
        HitStop.onDamage((LivingEntity) (Object) this, source, originalDamage, amount, blocked);
    }
}
