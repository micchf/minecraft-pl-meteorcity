package ita.micc.meteorcity.commands.usercommands.city;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.enums.MemberRole;
import ita.micc.meteorcity.message.Message;
import ita.micc.meteorcity.playercity.PlayerCity;
import ita.micc.meteorcity.playercity.PlayerCityInvite;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * /city invite <playerName>
 * @author Codeh
 */
public record CityInviteCommand(MeteorCity plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        String playerUUID = player.getUniqueId().toString();
        /* check if command is /city invite <playerName> */
        if (args.length != 1) {
            Message.BAD_SYNTAX.send(player);
            return false;
        }
        /* check if player has a city */
        if (!plugin.getCities().containsKey(playerUUID)) {
            Message.CITY_PLAYER_HASNT_A_CITY.send(player);
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
        /* check if target has already an invite */
        if (plugin.getInvites().containsKey(targetUUID)) {
            PlayerCityInvite invite = plugin.getInvites().get(targetUUID);
            /* check if invite is expired */
            if (invite.expired()) {
                plugin.getInvites().remove(targetUUID);
            } else {
                Message.TARGET_HAS_ALREADY_AN_INVITE.send(player);
                return false;
            }
        }
        /* check if target has a city */
        if (plugin.getCities().containsKey(targetUUID)) {
            Message.TARGET_HAS_A_CITY.send(player);
            return false;
        }
        PlayerCity playerCity = plugin.getCities().get(playerUUID);
        /* check if player is presidente or funzionario */
        MemberRole playerRole = EnumUtils.getEnum(MemberRole.class, playerCity.getMemberByUUID(playerUUID).getRole());
        if (!(playerRole == MemberRole.PRESIDENTE || playerRole == MemberRole.FUNZIONARIO)) {
            Message.CITY_PLAYER_NOT_ROLE.send(player);
            return false;
        }

        /* send invite */
        PlayerCityInvite invite = new PlayerCityInvite(System.currentTimeMillis(), playerCity);
        Message.TARGET_SEND_INVITE.send(player);
        plugin.getInvites().put(targetUUID, invite);
        target.sendMessage(Message.TARGET_MESSAGE_TO_TARGET.valueReplaced("%player%", player.getName()));
        return false;
    }
}
