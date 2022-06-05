package ita.micc.meteorcity.commands.usercommands.city;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.enums.MemberRole;
import ita.micc.meteorcity.message.Message;
import ita.micc.meteorcity.playercity.PlayerCity;
import ita.micc.meteorcity.playercity.PlayerCityInvite;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * /city acceptinvite
 * @author Codeh.
 */
public record CityAcceptInviteCommand(MeteorCity plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        String playerUUID = player.getUniqueId().toString();
        /* Check if the command is ./city acceptinvite */
        if (args.length != 0) {
            Message.BAD_SYNTAX.send(player);
            return false;
        }
        /* check if player has a city */
        if (plugin.getCities().containsKey(playerUUID)) {
            Message.CITY_PLAYER_HAS_ALREADY_A_CITY.send(player);
            return false;
        }
        /* Check if player has an invite */
        if (!plugin.getInvites().containsKey(playerUUID)) {
            Message.INVITE_YOU_HAVENT.send(player);
            return false;
        }
        PlayerCityInvite invite = plugin.getInvites().get(playerUUID);
        /* check if invite is expired */
        if (invite.expired()) {
            Message.INVITE_EXPIRED.send(player);
            plugin.getInvites().remove(playerUUID);
            return false;
        }
        PlayerCity playerCity = invite.getPlayerCity();
        
        /* player accept invite */
        playerCity.sendMessageAllMembers(Message.INVITE_SUCCESS_JOIN.valueReplaced("%player%", player.getName()));
        Message.CITY_PLAYER_JOIN_CITY.send(player);
        playerCity.addMember(playerUUID, MemberRole.CITTADINO);
        plugin.getCities().put(playerUUID, playerCity);
        plugin.getInvites().remove(playerUUID);
        return false;
    }
}
