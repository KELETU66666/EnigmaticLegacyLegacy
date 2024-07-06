package keletu.enigmaticlegacy.key;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.packet.PacketEnderRingKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber
public class EnderChestRingHandler {

    public static KeyBinding enderRingKey;
    @SideOnly(Side.CLIENT)
    public static void registerKeybinds() {
        enderRingKey = new KeyBinding("key.enderRing", Keyboard.KEY_I, "key.categories.enigmaticlegacy");

        ClientRegistry.registerKeyBinding(enderRingKey);
    }
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onKeyInput(TickEvent.ClientTickEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (event.phase != TickEvent.Phase.START || Minecraft.getMinecraft().isGamePaused() || player == null)
            return;

        if (enderRingKey.isPressed()) {
            if (!Minecraft.getMinecraft().isGamePaused()) {
                EnigmaticLegacy.packetInstance.sendToServer(new PacketEnderRingKey(true));
            }
        }
    }
}
