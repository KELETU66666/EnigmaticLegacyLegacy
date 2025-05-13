package keletu.enigmaticlegacy.command;

import keletu.enigmaticlegacy.api.cap.IPlaytimeCounter;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandSetNoRingTime extends CommandBase {
    @Override
    public String getName() {
        return "setnoringtime";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/setnoringtime time player";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length <= 0)
        {
            throw new WrongUsageException("commands.setnoringtime.usage");
        }

        long ticks = parseInt(args[0]);
        EntityPlayerMP player;

        if (args.length > 1 && server.getPlayerList().getPlayerByUsername(args[1]) != null) {
            player = server.getPlayerList().getPlayerByUsername(args[1]);
        } else {
            player = getCommandSenderAsPlayer(sender);
        }

        IPlaytimeCounter counter = IPlaytimeCounter.get(player);
        String name = player.getName();

        counter.setTimeWithoutCurses(ticks);
        ticks = counter.getTimeWithoutCurses();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
}
