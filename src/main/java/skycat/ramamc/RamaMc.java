package skycat.ramamc;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.SERVER)
public class RamaMc implements DedicatedServerModInitializer, ServerWorldEvents.Load {
    public static ServerWorld world = null;
    public static final Logger LOGGER = LoggerFactory.getLogger("rama-mc");
    @Override
    public void onInitializeServer() {
        ServerWorldEvents.LOAD.register(this);
    }

    @Override
    public void onWorldLoad(MinecraftServer server, ServerWorld world) {
        RamaMc.world = world;
    }

    public static boolean isDay() {
        return world.isDay();
    }
}
