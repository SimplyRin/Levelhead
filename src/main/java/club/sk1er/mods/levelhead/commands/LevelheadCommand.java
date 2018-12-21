package club.sk1er.mods.levelhead.commands;

import club.sk1er.mods.levelhead.Levelhead;
import club.sk1er.mods.levelhead.guis.NewLevelheadGui;
import club.sk1er.mods.levelhead.utils.ChatColor;
import club.sk1er.mods.levelhead.utils.Sk1erMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

/**
 * Created by Mitchell Katz on 5/8/2017.
 */
public class LevelheadCommand extends CommandBase {

	private Levelhead levelhead;

	public LevelheadCommand(Levelhead levelhead) {
		this.levelhead = levelhead;
	}

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "levelhead";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        //TODO update
        if (args.length == 1) {
            Sk1erMod instance = Sk1erMod.getInstance();
            if (args[0].equalsIgnoreCase("limit")) {
                instance.sendMessage(ChatColor.RED + "Count: " + levelhead.count);
                instance.sendMessage(ChatColor.RED + "Wait: " + levelhead.wait);
                instance.sendMessage(ChatColor.RED + "Hypixel: " + instance.isHypixel());
                instance.sendMessage(ChatColor.RED + "Remote Status: " + instance.isEnabled());
                instance.sendMessage(ChatColor.RED + "Local Stats: " + levelhead.getSk1erMod().isHypixel());
                instance.sendMessage(ChatColor.RED + "Callback: " + instance.getResponse());
                instance.sendMessage(ChatColor.RED + "Callback_types: " + levelhead.getTypes());
                //TODO add more debug
                return;
            } else if (args[0].equalsIgnoreCase("dumpcache")) {
            	levelhead.getDisplayManager().clearCache();
                instance.sendMessage(ChatColor.RED + "Cleared Cache");
                return;
            }
        }
        new NewLevelheadGui(levelhead).display();
    }
}
