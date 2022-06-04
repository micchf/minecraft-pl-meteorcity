package ita.micc.meteorcity.commands.usercommands.city;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.commands.Command;
import ita.micc.meteorcity.enums.MemberRole;
import ita.micc.meteorcity.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Override
    public List<String> tabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        /* if command is /city <arg 1> */
        /* <arg 1> is all city command's sub */
        if (args.length == 1) {
            return new ArrayList<>(getSubCommands().keySet());
        }

        /* if command is /city setrole <playerName> <roleType> */
        /* return member role's enum */
        if (args.length == 3 && args[0].equalsIgnoreCase("setrole")) {
            return Stream.of(MemberRole.values())
                    .map(MemberRole::value).toList().stream()
                    .map(String::toLowerCase).collect(Collectors.toList());
        }

        return null;
    }
}
