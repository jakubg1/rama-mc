package skycat.ramamc.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import skycat.ramamc.RamaMc;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin {
    @Inject(method = "tryEat", at = @At("RETURN"))
    private static void tryEat(WorldAccess world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue().equals(ActionResult.PASS) && !RamaMc.canPlayerConsumeFood(player)) { // Likely we blocked the event, so we need to let the client know
            if (player instanceof ServerPlayerEntity) {
                HungerManager hungerManager = player.getHungerManager();
                ((ServerPlayerEntity) player).networkHandler.sendPacket(new HealthUpdateS2CPacket(player.getHealth(), hungerManager.getFoodLevel(), hungerManager.getSaturationLevel()));
            }
        }
    }
}
