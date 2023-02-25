package skycat.ramamc.mixin;

import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import skycat.ramamc.RamaMc;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
    @Shadow private float exhaustion;

    /**
     * @author skycatminepokie
     * @reason Allow for changing exhaustion amount
     */
    @Overwrite
    public void addExhaustion(float exhaustion) {
        if (RamaMc.isDay()){
            exhaustion *= 0.3;
        }
        this.exhaustion = Math.min(this.exhaustion + exhaustion, 40.0F);
    }
}
