package skycat.ramamc;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

@Environment(EnvType.SERVER)
public class RamaMc implements DedicatedServerModInitializer, ServerWorldEvents.Load {
    public static World world = null;
    @Override
    public void onInitializeServer() {

    }

    @Override
    public void onWorldLoad(MinecraftServer server, ServerWorld world) {
        RamaMc.world = world;
    }
}
