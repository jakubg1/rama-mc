package skycat.ramamc;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class RamaMcConstants {
    public final float MONSTER_DAMAGE_MULTIPLIER_DAY = 1.5f; // Deal (x) times more damage
    public final float PLAYER_RESISTANCE_PERCENT_NIGHT = 0.25f; // Resist (x) of damage
    public final float PLAYER_DAMAGE_MULTIPLIER_DAY = 1.3f; // (x) times attack
    public final float HUNGER_MULTIPLIER_DAY = 0.3f; // (x) times hunger gain
    public final int SITTING_MULTIPLIER = 2; // (x) times faster
    public final long STANDING_REST_TIME = 60000; // (x) ms
    public final int STANDING_SATURATION_CHANCE = 60; // 1 in (x)
    public final int STANDING_REGENERATION_CHANCE = 100; // 1 in (X)
    public final double MEAL_RANGE = 20.0d; // (x) blocks
    public final long MEAL_LENGTH = 200; // (x) ticks
    public final int MEAL_ABSORPTION_AMOUNT = 10; // (x)/2 hearts (base)
    public final long MEAL_ABSORPTION_LENGTH = 80; // 4800 is 1/5 of a day
    public final int MIN_MEAL_SIZE = 4; // (x) players
    public final long MEAL_ABSORPTION_LENGTH_BONUS = 240000; // (x) ticks
    public final float MAX_MEAL_ABSORPTION_AMOUNT = 20; // (x)/2 hearts
    public final int MEAL_EXPIRATION_WARNING = 300; // (x) ticks
    public final long SLEEPING_COOLDOWN = 24000L * 3; // (x) ticks
    public static final String SAVE_PATH = "config/rama-mc.json";

    public static RamaMcConstants load() {
        try (FileReader reader = new FileReader(SAVE_PATH)) {
            return RamaMc.GSON.fromJson(reader, RamaMcConstants.class);
        } catch (IOException e) {
            return new RamaMcConstants();
        }
    }

    public void save() {
        try (PrintWriter writer = new PrintWriter(SAVE_PATH)) {
            RamaMc.GSON.toJson(this, writer);
        } catch (IOException e) {
            RamaMc.LOGGER.info("Could not save config.");
        }
    }
}
