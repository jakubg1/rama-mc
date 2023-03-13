package skycat.ramamc;

public class RamaMcConstants {
    public static final float MONSTER_DAMAGE_MULTIPLIER_DAY = 1.5f; // Deal (x) times more damage
    public static final float PLAYER_RESISTANCE_PERCENT_NIGHT = 0.25f; // Resist (x) of damage
    public static final float PLAYER_DAMAGE_MULTIPLIER_DAY = 1.3f; // (x) times attack
    public static final float HUNGER_MULTIPLIER_DAY = 0.3f; // (x) times hunger gain
    public static final int SITTING_MULTIPLIER = 2; // (x) times faster
    public static final long STANDING_REST_TIME = 60000; // (x) ms
    public static final int STANDING_SATURATION_CHANCE = 60; // 1 in (x)
    public static final int STANDING_REGENERATION_CHANCE = 100; // 1 in (X)
    public static final double MEAL_RANGE = 20.0d; // (x) blocks
    public static final long MEAL_LENGTH = 200; // (x) ticks
    public static final int MEAL_ABSORPTION_AMOUNT = 10; // (x)/2 hearts (base)
    public static final long MEAL_ABSORPTION_LENGTH = 80; // 4800 is 1/5 of a day
    public static final int MIN_MEAL_SIZE = 4; // (x) players
    public static final long MEAL_ABSORPTION_LENGTH_BONUS = 240000; // (x) ticks
    public static final float MAX_MEAL_ABSORPTION_AMOUNT = 20; // (x)/2 hearts
    public static final int MEAL_EXPIRATION_WARNING = 300; // (x) ticks
    public static final long SLEEPING_COOLDOWN = 24000L * 3; // (x) ticks
}
