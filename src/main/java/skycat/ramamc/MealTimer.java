package skycat.ramamc;

public class MealTimer {
    public long timeLeft;
    public final BigMealManager.BigMeal meal;

    public MealTimer(long timeLeft, BigMealManager.BigMeal meal) {
        this.timeLeft = timeLeft;
        this.meal = meal;
    }
}
