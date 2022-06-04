package ita.micc.meteorcity.commands.admincommands.cityadmin;

import ita.micc.meteorcity.commands.Command;
import ita.micc.meteorcity.message.Message;
import org.bukkit.command.CommandSender;

/**
 * /cityadmin
 * @author Codeh
 */
public class CityAdminCommand extends Command {

    /**
     * @param permissionRequired permission required for execute all leaves commands.
     * @param onlyPlayer         if only player can execute all leaves commands.
     */
    public CityAdminCommand(String permissionRequired, boolean onlyPlayer) {
        super(permissionRequired, onlyPlayer);
    }

    @Override
    public boolean executeCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        Message.BAD_SYNTAX.send(sender);
        return false;
    }
}
