package skycat.ramamc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class BigMealManager {
    public static final double MEAL_RANGE = 20.0d;
    public static final long MEAL_LENGTH = 200;
    public static final int ABSORPTION_AMOUNT = 10;
    public static final long ABSORPTION_LENGTH = 80;
    public static final int MIN_MEAL_SIZE = 1;
    public static final long LENGTH_BONUS = 240000;
    public static final float MAX_ABSORPTION_AMOUNT = 20;
    public ArrayList<BigMeal> mealList = new ArrayList<>();

    public boolean isNearBigMeal(PlayerEntity player) {
        return getMealInRange(player) != null;
    }

    @Nullable
    public BigMeal getMealInRange(BlockPos pos) {
        for (BigMeal meal : mealList) {
            if (pos.isWithinDistance(meal.pos, MEAL_RANGE)) {
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
        RamaMc.LOGGER.info("Started meal"); // WARN Debug
        BigMeal meal = new BigMeal(pos);
        mealList.add(meal);
        ((BigMealTimerAccess) RamaMc.world).mystical_setMealTimer(MEAL_LENGTH, meal);
        return meal;
    }

    public void endMeal(BigMeal meal) {
        RamaMc.LOGGER.info("Big meal finished."); // WARN debug
        for (PlayerEntity player : meal.participants) {
            RamaMc.LOGGER.info("Participant: " + player.getName().getString()); // WARN debug
        }
        int mealSize = meal.participants.size();
        if (mealSize >= MIN_MEAL_SIZE) {
            for (PlayerEntity player : meal.participants) {
                float absorptionAmount = Math.min(MAX_ABSORPTION_AMOUNT, player.getAbsorptionAmount() + ABSORPTION_AMOUNT);
                player.setAbsorptionAmount(absorptionAmount); // Give extra absorption
                ((AbsorptionTimerAccess)RamaMc.world).set(ABSORPTION_LENGTH + ((mealSize - MIN_MEAL_SIZE) * LENGTH_BONUS), player, absorptionAmount); // 4800 is 1/5 of a day
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
