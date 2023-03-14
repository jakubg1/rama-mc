package skycat.ramamc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class BigMealManager {
    public ArrayList<BigMeal> mealList = new ArrayList<>();

    public boolean isNearBigMeal(PlayerEntity player) {
        return getMealInRange(player) != null;
    }

    @Nullable
    public BigMeal getMealInRange(BlockPos pos) {
        for (BigMeal meal : mealList) {
            if (pos.isWithinDistance(meal.pos, RamaMc.CONFIG.MEAL_RANGE)) {
                return meal;
            }
        }
        return null;
    }

    @Nullable
    public BigMeal getMealInRange(PlayerEntity player) {
        return getMealInRange(player.getBlockPos());
    }

    /**
     * Starts and records a new meal at the given location. Does not add any participants.
     * @param pos The place to start the meal at
     * @return The new meal
     */
    public BigMeal startMeal(BlockPos pos) {
        RamaMc.LOGGER.info("Started meal");
        BigMeal meal = new BigMeal(pos);
        mealList.add(meal);
        ((BigMealTimerAccess) RamaMc.world).mystical_setMealTimer(RamaMc.CONFIG.MEAL_LENGTH, meal);
        return meal;
    }

    public void endMeal(BigMeal meal) {
        RamaMc.LOGGER.info("Big meal finished.");
        for (PlayerEntity player : meal.participants) {
            RamaMc.LOGGER.info("Participant: " + player.getName().getString());
        }
        int mealSize = meal.participants.size();
        if (mealSize >= RamaMc.CONFIG.MIN_MEAL_SIZE) {
            for (PlayerEntity player : meal.participants) {
                float absorptionAmount = Math.min(RamaMc.CONFIG.MAX_MEAL_ABSORPTION_AMOUNT, player.getAbsorptionAmount() + RamaMc.CONFIG.MEAL_ABSORPTION_AMOUNT);
                player.setAbsorptionAmount(absorptionAmount); // Give extra absorption
                long effectTime = RamaMc.CONFIG.MEAL_ABSORPTION_LENGTH + ((mealSize - RamaMc.CONFIG.MIN_MEAL_SIZE) * RamaMc.CONFIG.MEAL_ABSORPTION_LENGTH_BONUS);
                ((AbsorptionTimerAccess)RamaMc.world).set(effectTime, player, absorptionAmount);
                ((RunnableTimerAccess)RamaMc.world).rama_mc_setRunnableTimer(()-> {
                    player.sendMessage(Text.of("Your big meal bonus will run out soon."));
                }, Math.max(effectTime - RamaMc.CONFIG.MEAL_EXPIRATION_WARNING, 0));
            }
        }
        mealList.remove(meal);
    }

    public static class BigMeal {
        public ArrayList<PlayerEntity> participants = new ArrayList<>();
        public BlockPos pos;

        public BigMeal(BlockPos pos) {
            this.pos = pos;
        }

        public void addParticipant(PlayerEntity player) {
            if (!participants.contains(player)){
                participants.add(player);
            }
        }
    }
}
