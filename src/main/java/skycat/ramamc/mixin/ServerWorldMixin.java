package skycat.ramamc.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skycat.ramamc.*;

import java.util.ArrayList;
import java.util.Iterator;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements BigMealTimerAccess, AbsorptionTimerAccess {
    @Unique
    public ArrayList<MealTimer> mealTimers = new ArrayList<>();
    @Unique
    public ArrayList<AbsorptionTimer> absorptionTimers = new ArrayList<>();

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (!mealTimers.isEmpty()) {
            Iterator<MealTimer> iterator = mealTimers.iterator();
            while (iterator.hasNext()) {
                MealTimer next = iterator.next();
                if (--next.timeLeft == 0) {
                    RamaMc.BIG_MEAL_MANAGER.endMeal(next.meal);
                    iterator.remove();
                }
            }
        }
        if (!absorptionTimers.isEmpty()) {
            Iterator<AbsorptionTimer> iterator = absorptionTimers.iterator();
            while (iterator.hasNext()) {
                AbsorptionTimer next = iterator.next();
                if (--next.ticks == 0) {
                    next.expire();
                    iterator.remove();
                }
            }
        }

    }

    @Override
    public void mystical_setMealTimer(long timerLength, BigMealManager.BigMeal meal) {
        mealTimers.add(new MealTimer(timerLength, meal));
    }

    @Override
    public void set(long ticks, PlayerEntity player, float health) {
        absorptionTimers.add(new AbsorptionTimer(ticks, player, health));
    }
}
