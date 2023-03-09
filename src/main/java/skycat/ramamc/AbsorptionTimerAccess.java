package skycat.ramamc;

import net.minecraft.entity.player.PlayerEntity;

public interface AbsorptionTimerAccess {
    void set(long ticks, PlayerEntity player, float health);
}
