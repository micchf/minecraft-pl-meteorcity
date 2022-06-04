package ita.micc.meteorcity.commands.admincommands.cityadmin;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.commands.Command;
import ita.micc.meteorcity.message.Message;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * /cityadmin
 * @author Codeh
 */
public class CityAdminCommand extends Command {

    /**
     * @param permissionRequired permission required for execute all leaves commands.
     * @param onlyPlayer         if only player can execute all leaves commands.
     */
    public CityAdminCommand(String permissionRequired, boolean onlyPlayer, MeteorCity plugin) {
        super(permissionRequired, onlyPlayer);

        /* register sub command arg */
        register("refreshtemplates", new CityAdminRefreshTemplates(plugin));
    }

    @Override
    public boolean executeCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        Message.BAD_SYNTAX.send(sender);
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        /* if command is /cityadmin <arg 1> */
        /* <arg 1> is all city admin command's sub */
        if (args.length == 1) {
            return new ArrayList<>(getSubCommands().keySet());
        }
        return null;
    }
}
