package ita.micc.meteorcity.commands.usercommands.city;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.commands.Command;
import ita.micc.meteorcity.enums.MemberRole;
import ita.micc.meteorcity.message.Message;
import ita.micc.meteorcity.playercity.PlayerCity;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        super(permissionRequired, onlyPlayer, plugin);

        /* register sub command arg */
        register("new", new CityNewCommand(plugin));
        register("go", new CityGoCommand(plugin));
        register("disband", new CityDisbandCommand(plugin));
        register("setspawn", new CitySetSpawnCommand(plugin));
        register("setrole", new CitySetRoleCommand(plugin));
        register("invite", new CityInviteCommand(plugin));
        register("acceptinvite", new CityAcceptInviteCommand(plugin));
        register("leave", new CityLeaveCommand(plugin));
        register("kick", new CityKickCommand(plugin));
    }

    @Override
    public boolean executeCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        Message.BAD_SYNTAX.send(sender);
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        Player player = (Player) sender;
        String playerUUID = player.getUniqueId().toString();
        /* if command is /city <arg 1> */
        /* <arg 1> is all city command's sub */
        if (args.length == 1) {
            /* check if player has a city */
            if (!getPlugin().getCities().containsKey(playerUUID)) {
                return new ArrayList<>(List.of("new"));
            }
            PlayerCity playerCity = getPlugin().getCities().get(playerUUID);
            /* check if is cittadino */
            if (playerCity.getMemberRole(playerUUID) == MemberRole.CITTADINO) {
                return new ArrayList<>(List.of("go", "leave"));
            }
            /* check if is funzionario */
            if (playerCity.getMemberRole(playerUUID) == MemberRole.FUNZIONARIO) {
                return new ArrayList<>(List.of("go", "leave", "setspawn", "invite"));
            }
            /* is presidente */
            return new ArrayList<>(List.of("go", "disband", "setspawn", "setrole", "invite", "leave", "kick"));
        }

        /* if command is /city setrole <playerName> <roleType> */
        /* return member role's enum */
        if (args.length == 3 && args[0].equalsIgnoreCase("setrole")) {
            /* check if player has a city */
            if (!getPlugin().getCities().containsKey(playerUUID)) {
                return null;
            }
            PlayerCity playerCity = getPlugin().getCities().get(playerUUID);
            /* check if is owner */
            if (!(playerCity.getMemberRole(playerUUID) == MemberRole.PRESIDENTE)) {
                return null;
            }
            return Stream.of(MemberRole.values())
                    .map(MemberRole::value).toList().stream()
                    .map(String::toLowerCase).collect(Collectors.toList());
        }
        return null;
    }
}
