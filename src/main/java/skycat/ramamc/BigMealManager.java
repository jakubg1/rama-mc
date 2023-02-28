package skycat.ramamc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class BigMealManager {
    public static final double MEAL_RANGE = 20.0d;
    public static final long MEAL_LENGTH = 200;
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
        BigMeal meal = new BigMeal(pos);
        mealList.add(meal);
        ((BigMealTimerAccess) RamaMc.world).mystical_setMealTimer(MEAL_LENGTH, meal);
        return meal;
    }

    public void endMeal(BigMeal meal) {
        RamaMc.LOGGER.info("Big meal finished.");
        for (PlayerEntity player : meal.participants) {
            RamaMc.LOGGER.info("Participant: " + player.getName().getString());
        }
        // TODO big meal logic
    }

    public static class BigMeal {
        public ArrayList<PlayerEntity> participants = new ArrayList<>();
        public BlockPos pos;

        public BigMeal(BlockPos pos) {
            this.pos = pos;
        }

        public void addParticipant(PlayerEntity player) {
            participants.add(player);
        }
    }
}
