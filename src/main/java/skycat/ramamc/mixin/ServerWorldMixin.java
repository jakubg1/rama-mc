package skycat.ramamc.mixin;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skycat.ramamc.BigMealManager;
import skycat.ramamc.BigMealTimerAccess;
import skycat.ramamc.MealTimer;
import skycat.ramamc.RamaMc;

import java.util.ArrayList;
import java.util.Iterator;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements BigMealTimerAccess {
    @Unique
    public ArrayList<MealTimer> timers = new ArrayList<>();

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        Iterator<MealTimer> iterator = timers.iterator();
        while (iterator.hasNext()) {
            MealTimer next = iterator.next();
            if (--next.timeLeft == 0) {
                RamaMc.BIG_MEAL_MANAGER.endMeal(next.meal);
                iterator.remove();
            }
        }
    }

    @Override
    public void mystical_setMealTimer(long timerLength, BigMealManager.BigMeal meal) {
        timers.add(new MealTimer(timerLength, meal));
    }

}
