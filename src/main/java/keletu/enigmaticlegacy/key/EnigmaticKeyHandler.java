package keletu.enigmaticlegacy.key;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import keletu.enigmaticlegacy.item.ItemAngelBlessing;
import keletu.enigmaticlegacy.packet.PacketEnderRingKey;
import keletu.enigmaticlegacy.packet.PacketSpellstoneKey;
import keletu.enigmaticlegacy.util.interfaces.ISpellstone;
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
public class EnigmaticKeyHandler {

    public static KeyBinding enderRingKey;
    public static KeyBinding spellstoneAbilityKey;
    public static KeyBinding xpScrollKey;
    private static boolean space_down = false;

    @SideOnly(Side.CLIENT)
    public static void registerKeybinds() {
        enderRingKey = new KeyBinding("key.enderRing", Keyboard.KEY_I, "key.categories.enigmaticlegacy");
        spellstoneAbilityKey = new KeyBinding("key.spellstoneAbility", Keyboard.KEY_K, "key.categories.enigmaticlegacy");
        xpScrollKey = new KeyBinding("key.xpScroll", Keyboard.KEY_J, "key.categories.enigmaticlegacy");

        ClientRegistry.registerKeyBinding(enderRingKey);
        ClientRegistry.registerKeyBinding(spellstoneAbilityKey);
        ClientRegistry.registerKeyBinding(xpScrollKey);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onKeyInput(TickEvent.ClientTickEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (event.phase != TickEvent.Phase.START || Minecraft.getMinecraft().isGamePaused() || player == null)
            return;

        boolean spaceDown = Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
        boolean jumpClicked = false;

        if (space_down != spaceDown) {
            space_down = spaceDown;
            if (spaceDown) {
                jumpClicked = true;
            }
        }

        if (Minecraft.getMinecraft().player.isElytraFlying()) {
            jumpClicked = Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
        }

        //if (!OmniconfigHandler.angelBlessingDoubleJump.getValue()) {
        //    jumpClicked = false;
        //}

        if (enderRingKey.isPressed()) {
            if (!Minecraft.getMinecraft().isGamePaused()) {
                EnigmaticLegacy.packetInstance.sendToServer(new PacketEnderRingKey(true));
            }
        }

        if (spellstoneAbilityKey.isPressed() && SuperpositionHandler.getAdvancedBaubles(Minecraft.getMinecraft().player).getItem() instanceof ISpellstone) {
            EnigmaticLegacy.packetInstance.sendToServer(new PacketSpellstoneKey(true));
        } else if (jumpClicked && !player.isInWater() && !player.onGround && SuperpositionHandler.getAdvancedBaubles(Minecraft.getMinecraft().player).getItem() instanceof ItemAngelBlessing) {
            EnigmaticLegacy.packetInstance.sendToServer(new PacketSpellstoneKey(true));
        }
    }
}
