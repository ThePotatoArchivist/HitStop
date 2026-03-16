package archives.tater.hitstop.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;

import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.player.Player;

@Mixin(TickRateManager.class)
public class TickRateManagerMixin {
	@WrapOperation(
			method = "isEntityFrozen",
			constant = @Constant(classValue = Player.class)
	)
	private boolean freezePlayers(Object object, Operation<Boolean> original) {
		return false;
	}

	@ModifyExpressionValue(
			method = "isEntityFrozen",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;countPlayerPassengers()I")
	)
	private int freezeMounts(int original) {
		return 0;
	}
}