package ita.micc.meteorcity.commands.usercommands.city;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.enums.MemberRole;
import ita.micc.meteorcity.message.Message;
import ita.micc.meteorcity.playercity.PlayerCity;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * /city setrole <playerName> <roleType>
 * @author Codeh
 */
public record CitySetRoleCommand(MeteorCity plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        String playerUUID = player.getUniqueId().toString();
        /* check if command is /city setrole <playerName> <roleType> */
        if (args.length != 2) {
            Message.BAD_SYNTAX.send(player);
            return false;
        }
        /* check if target is itself */
        if (args[0].equalsIgnoreCase(player.getName())) {
            Message.TARGET_YOU_ARE.send(player);
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        /* check if target is online */
        if (target == null) {
            Message.TARGET_NOT_ONLINE.send(player);
            return false;
        }
        String targetUUID = target.getUniqueId().toString();
        /* check if player has a city */
        if (!plugin.getCities().containsKey(playerUUID)) {
            Message.CITY_PLAYER_HASNT_A_CITY.send(player);
            return false;
        }
        PlayerCity playerCity = plugin.getCities().get(playerUUID);
        /* check if player is presidente */
        MemberRole playerRole = playerCity.getMemberRole(playerUUID);
        if (!(playerRole == MemberRole.PRESIDENTE)) {
            Message.CITY_PLAYER_NOT_ROLE.send(player);
            return false;
        }
        /* check if is valid role */
        if (!EnumUtils.isValidEnum(MemberRole.class, args[1].toUpperCase())) {
            Message.INVALID_ROLE.send(player);
            return false;
        }
        /* check if target is a city's member */
        if (!playerCity.isMember(targetUUID)) {
            Message.TARGET_IS_NOT_MEMBER_CITY.send(player);
            return false;
        }

        /* update target role */
        /* check if player who update member role is presidente */
        if (EnumUtils.getEnumIgnoreCase(MemberRole.class, args[1].toUpperCase()) == MemberRole.PRESIDENTE) {
            playerCity.updateMemberRole(playerUUID, MemberRole.CITTADINO);
            Message.CITY_PLAYER_YOU_ARE_NO_LONGER_OWNER.send(player);
        }
        playerCity.updateMemberRole(targetUUID, MemberRole.valueOf(args[1].toUpperCase()));
        Message.CITY_PLAYER_TARGET_ROLE_UPDATE.send(player);
        Message.TARGET_ROLE_UPDATE.send(target);
        return false;
    }
}
