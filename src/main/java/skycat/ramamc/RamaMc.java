package skycat.ramamc;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

@Environment(EnvType.SERVER)
public class RamaMc implements DedicatedServerModInitializer, ServerWorldEvents.Load, ServerEntityCombatEvents.AfterKilledOtherEntity, UseItemCallback, EntitySleepEvents.AllowResettingTime, EntitySleepEvents.StopSleeping {
    private static final int PORK_PUNISHMENT_TIME = 24000;
    public static ServerWorld world = null;
    public static MinecraftServer server = null;
    public static final Logger LOGGER = LoggerFactory.getLogger("rama-mc");
    public static final BigMealManager BIG_MEAL_MANAGER = new BigMealManager();
    public static final Random RANDOM = new Random();
    public static boolean allowSleep = true;

    @Override
    public void afterKilledOtherEntity(ServerWorld world, Entity entity, LivingEntity killedEntity) {
        if (entity.isPlayer() && isDay()) {
            if (killedEntity instanceof Monster) {
                ((PlayerEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 40, 0));
            }
        }
    }

    @Override
    public boolean allowResettingTime(PlayerEntity player) {
        return allowSleep;
    }

    @Override
    public TypedActionResult<ItemStack> interact(PlayerEntity player, World world, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isFood()) {
            if (stack.isOf(Items.PORKCHOP) || stack.isOf(Items.COOKED_PORKCHOP)) {
                StatusEffectInstance weakness = player.getStatusEffect(StatusEffects.WEAKNESS);
                StatusEffectInstance miningFatigue = player.getStatusEffect(StatusEffects.MINING_FATIGUE);
                int newWeaknessAmplifier = 0;
                if (weakness != null) {
                    newWeaknessAmplifier = Math.min(weakness.getAmplifier() + 1, 2);
                }
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, (1 + newWeaknessAmplifier) * PORK_PUNISHMENT_TIME, newWeaknessAmplifier));
                int newMiningFatigueAmplifier = 0;
                if (miningFatigue != null) {
                    newMiningFatigueAmplifier = Math.min(miningFatigue.getAmplifier() + 1, 2);
                }
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, (1+newMiningFatigueAmplifier) * PORK_PUNISHMENT_TIME, newMiningFatigueAmplifier));
            }
        }
        return TypedActionResult.pass(stack); // TODO make sure this don't cause problems
    }

    @Override
    public void onInitializeServer() {
        ServerWorldEvents.LOAD.register(this);
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(this);
        UseItemCallback.EVENT.register(this);
        EntitySleepEvents.STOP_SLEEPING.register(this);
        EntitySleepEvents.ALLOW_RESETTING_TIME.register(this);
    }

    @Override
    public void onStopSleeping(LivingEntity entity, BlockPos sleepingPos) {
        if (entity.isPlayer() && allowSleep) {
                allowSleep = false;
                ((RunnableTimerAccess) world).rama_mc_setRunnableTimer(()-> RamaMc.allowSleep = true, 24000L * 3); // Allow sleeping in three days
        }
    }

    @Override
    public void onWorldLoad(MinecraftServer server, ServerWorld world) {
        RamaMc.world = world;
        RamaMc.server = server;
    }

    public static boolean isDay() {
        return server.getOverworld().isDay();
    }
}
