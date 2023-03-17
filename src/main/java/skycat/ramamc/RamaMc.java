package skycat.ramamc;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.item.PotionItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.include.com.google.gson.Gson;
import org.spongepowered.include.com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

@Environment(EnvType.SERVER)
public class RamaMc implements DedicatedServerModInitializer,
        ServerWorldEvents.Load,
        ServerEntityCombatEvents.AfterKilledOtherEntity,
        EntitySleepEvents.AllowResettingTime,
        EntitySleepEvents.StopSleeping,
        ServerPlayConnectionEvents.Join,
        ServerLifecycleEvents.ServerStopping,
        UseItemCallback {
    public static ServerWorld world = null;
    public static MinecraftServer server = null;
    public static final Logger LOGGER = LoggerFactory.getLogger("rama-mc");
    public static final BigMealManager BIG_MEAL_MANAGER = new BigMealManager();
    public static final Random RANDOM = new Random();
    public static boolean allowSleep = true;
    public static final HashMap<UUID, Float> removeAbsorptionMap = new HashMap<>();
    public static final Gson GSON = new GsonBuilder().create();
    public static final RamaMcConfig CONFIG = RamaMcConfig.load();


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
        // Stop players from drinking potions/milk during the day.
        ItemStack stack = player.getStackInHand(hand);
        if (RamaMc.isDay() && ((stack.getItem().getClass().equals(PotionItem.class)) || stack.getItem().getClass().equals(MilkBucketItem.class))) {
            return TypedActionResult.fail(stack);
        }
        return TypedActionResult.pass(stack);
    }

    @Override
    public void onInitializeServer() {
        ServerWorldEvents.LOAD.register(this);
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(this);
        EntitySleepEvents.STOP_SLEEPING.register(this);
        EntitySleepEvents.ALLOW_RESETTING_TIME.register(this);
        ServerPlayConnectionEvents.JOIN.register(this);
        ServerLifecycleEvents.SERVER_STOPPING.register(this);
        UseItemCallback.EVENT.register(this);
    }

    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        // LOGGER.info("login check");
        ServerPlayerEntity player = handler.getPlayer();
        UUID uuid = player.getUuid();
        if (removeAbsorptionMap.containsKey(uuid)) { // If they logged off with absorption from a big meal that has expired, remove it
            player.setAbsorptionAmount(player.getAbsorptionAmount() - Math.min(removeAbsorptionMap.get(uuid), player.getAbsorptionAmount()));
            // LOGGER.info("removed absorption");
            player.sendMessage(Text.of("Your big meal bonus ran out while you were away."));
            removeAbsorptionMap.remove(uuid);
        }
    }

    @Override
    public void onServerStopping(MinecraftServer server) {
        CONFIG.save();
    }

    @Override
    public void onStopSleeping(LivingEntity entity, BlockPos sleepingPos) {
        if (entity.isPlayer() && allowSleep) {
            allowSleep = false;
            ((RunnableTimerAccess) world).rama_mc_setRunnableTimer(() -> RamaMc.allowSleep = true, RamaMc.CONFIG.SLEEPING_COOLDOWN);
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
