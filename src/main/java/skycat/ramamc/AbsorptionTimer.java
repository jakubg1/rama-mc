package skycat.ramamc;

import net.minecraft.entity.player.PlayerEntity;

public class AbsorptionTimer {
    public long ticks;
    public PlayerEntity player;
    public float health;

    public AbsorptionTimer(long ticks, PlayerEntity player, float health) {
        this.ticks = ticks;
        this.player = player;
        this.health = health;
    }

    public void expire() {
        if (player.isRemoved()) {
            float maxHealthToRemove = health;
            if (RamaMc.removeAbsorptionMap.containsKey(player.getUuid())) {
                maxHealthToRemove += RamaMc.removeAbsorptionMap.get(player.getUuid());
            }
            RamaMc.removeAbsorptionMap.put(player.getUuid(), maxHealthToRemove);
            return;
        }
        if (player.getAbsorptionAmount() > 0) {
            player.setAbsorptionAmount(player.getAbsorptionAmount() - Math.min(health, player.getAbsorptionAmount())); // Remove their absorption, taking either the number of hearts or all of them if they don't have enough.
        }
    }
}
