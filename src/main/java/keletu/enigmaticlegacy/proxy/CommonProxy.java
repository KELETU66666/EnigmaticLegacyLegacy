package keletu.enigmaticlegacy.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommonProxy {
    public void spawnBonemealParticles(World world, BlockPos pos, int data) {
        // NO-OP
    }

    public EntityPlayer getClientPlayer() {
        return null;
    }

    public void displayReviveAnimation(int entityID, int reviveType) {

    }


    public void renderEntities() {
    }
}