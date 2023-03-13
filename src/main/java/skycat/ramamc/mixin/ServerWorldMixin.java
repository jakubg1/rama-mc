package skycat.ramamc.mixin;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skycat.ramamc.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements BigMealTimerAccess, AbsorptionTimerAccess, RunnableTimerAccess {
    @Shadow @Final
    List<ServerPlayerEntity> players;

    @Shadow public abstract List<ServerPlayerEntity> getPlayers();

    @Unique
    public ArrayList<MealTimer> mealTimers = new ArrayList<>();
    @Unique
    public ArrayList<AbsorptionTimer> absorptionTimers = new ArrayList<>();
    @Unique
    public ArrayList<RunnableTimer> runnableTimers = new ArrayList<>();

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        for (ServerPlayerEntity player : getPlayers()) {
            // 1 in 10 chance every tick
            if (Util.getMeasuringTimeMs() - player.getLastActionTime() > 10000 && RamaMc.RANDOM.nextInt(10) == 0) { // It's been 10 seconds since the last player action
                if (player.getHungerManager().getFoodLevel() < HungerConstants.FULL_FOOD_LEVEL/2) {
                    if (player.hasVehicle()) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 2, 0));
                    } else {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 1, 0));
                    }
                }
            }
        }
        if (!runnableTimers.isEmpty()) {
            ArrayList<RunnableTimer> removeTimers = new ArrayList<>();
            // This may be a funny way, but it should work and allow new timers to be set.
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < runnableTimers.size(); i++) {
                RunnableTimer timer = runnableTimers.get(i);
                if (--timer.ticks == 0) {
                    timer.expire();
                    removeTimers.add(timer);
                }
            }
            runnableTimers.removeAll(removeTimers);
        }
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

    @ModifyVariable(method = "setWeather", at = @At("HEAD"), ordinal = 1)
    private int modifyRainTime(int rainDuration) {
        if (rainDuration > 0) {
            RamaMc.LOGGER.info("Rain length changed"); // WARN: Debug
            return 12000;
        }
        return rainDuration;
    }

    @Override
    public void mystical_setMealTimer(long timerLength, BigMealManager.BigMeal meal) {
        mealTimers.add(new MealTimer(timerLength, meal));
    }

    @Override
    public void set(long ticks, PlayerEntity player, float health) {
        absorptionTimers.add(new AbsorptionTimer(ticks, player, health));
    }

    @Override
    public void rama_mc_setRunnableTimer(RunnableTimer timer) {
        runnableTimers.add(timer);
    }

    @Override
    public void rama_mc_setRunnableTimer(Runnable runnable, long ticks) {
        runnableTimers.add(new RunnableTimer(runnable, ticks));
    }
}
