package keletu.enigmaticlegacy.key;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.packet.PacketOpenExtendedBaublesInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber
public class ExtraBaublesKey {

    public static KeyBinding extraBaubles;

    @SideOnly(Side.CLIENT)
    public static void registerKeybinds() {
        extraBaubles = new KeyBinding("key.baubleEnigmatic", Keyboard.KEY_V, "key.categories.enigmaticlegacy");

        ClientRegistry.registerKeyBinding(extraBaubles);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onKeyInput(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.side == Side.CLIENT) {

            if (extraBaubles.isPressed() && FMLClientHandler.instance().getClient().inGameHasFocus) {
                EnigmaticLegacy.packetInstance.sendToServer(new PacketOpenExtendedBaublesInventory());
            }
        }
    }
}
