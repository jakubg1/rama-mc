package skycat.ramamc;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

@Environment(EnvType.SERVER)
public class RamaMc implements DedicatedServerModInitializer, ServerWorldEvents.Load, ServerEntityCombatEvents.AfterKilledOtherEntity {
    public static ServerWorld world = null;
    public static MinecraftServer server = null;
    public static final Logger LOGGER = LoggerFactory.getLogger("rama-mc");
    public static final BigMealManager BIG_MEAL_MANAGER = new BigMealManager();
    public static final Random RANDOM = new Random();

    @Override
    public void afterKilledOtherEntity(ServerWorld world, Entity entity, LivingEntity killedEntity) {
        if (entity.isPlayer() && isDay()) {
            if (killedEntity instanceof Monster) {
                ((PlayerEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 40, 0));
            }
        }
    }

    @Override
    public void onInitializeServer() {
        ServerWorldEvents.LOAD.register(this);
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(this);
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
