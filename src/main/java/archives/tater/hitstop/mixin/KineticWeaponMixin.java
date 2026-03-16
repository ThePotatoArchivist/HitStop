package archives.tater.hitstop.mixin;

import archives.tater.hitstop.HitStop;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.KineticWeapon;

@Mixin(KineticWeapon.class)
public class KineticWeaponMixin {
    @ModifyExpressionValue(
            method = "damageEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;stabAttack(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/Entity;FZZZ)Z")
    )
    private boolean spearHitstop(boolean original, ItemStack stack, int ticksRemaining, LivingEntity entity, @Local(name = "relativeSpeed") double relativeSpeed) {
        if (!original) return false;
        if (!(entity instanceof Player) || !(entity.level() instanceof ServerLevel level)) return true;

        HitStop.runHitStop(level.getServer(), (int) relativeSpeed, 0);

        return true;
    }
}
