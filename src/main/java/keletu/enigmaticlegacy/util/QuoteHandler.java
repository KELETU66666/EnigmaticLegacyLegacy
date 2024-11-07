package keletu.enigmaticlegacy.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class QuoteHandler {
	public static final QuoteHandler INSTANCE = new QuoteHandler();
	private Quote currentQuote = null;
	private long startedPlaying = -1;
	private int delayTicks = -1;
	private boolean shownExperimentalInfo = false;

	private QuoteHandler() {
		// NO-OP
	}

	private double getPlayTime() {
		long millis = System.currentTimeMillis() - this.startedPlaying;
		return ((double)millis) / 1000;
	}

	public void playQuote(Quote quote, int delayTicks) {
		if (this.currentQuote == null) {
			this.currentQuote = quote;
			this.delayTicks = delayTicks;
		}
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.player == Minecraft.getMinecraft().player) {
			if (this.delayTicks > 0) {
				this.delayTicks--;

				if (this.delayTicks == 0) {
					ISound instance = new PositionedSoundRecord(this.currentQuote.getSound().getSoundName(), SoundCategory.VOICE, 0.7F, 1F, false, 0, ISound.AttenuationType.NONE, 0, 0, 0);

					Minecraft.getMinecraft().getSoundHandler().playSound(instance);

					this.startedPlaying = System.currentTimeMillis();
					currentQuote = null;
				}
			}
		}
	}
}