package ita.micc.meteorcity.commands.usercommands.city;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.commands.Command;
import ita.micc.meteorcity.message.Message;
import org.bukkit.command.CommandSender;

/**
 * /city
 * @author Codeh
 */
public class CityCommand extends Command {

    /**
     * @param permissionRequired permission required for execute all leaves commands.
     * @param onlyPlayer         if only player can execute all leaves commands.
     */
    public CityCommand(String permissionRequired, boolean onlyPlayer, MeteorCity plugin) {
        super(permissionRequired, onlyPlayer);

        /* register sub command arg */
        register("new", new CityNewCommand(plugin));
        register("go", new CityGoCommand(plugin));
        register("disband", new CityDisbandCommand(plugin));
        register("setspawn", new CitySetSpawnCommand(plugin));
        register("setrole", new CitySetRoleCommand(plugin));
        register("invite", new CityInviteCommand(plugin));
        register("acceptinvite", new CityAcceptInviteCommand(plugin));
    }

    @Override
    public boolean executeCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        Message.BAD_SYNTAX.send(sender);
        return false;
    }
}
