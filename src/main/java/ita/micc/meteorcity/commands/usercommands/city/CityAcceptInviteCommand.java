package ita.micc.meteorcity.commands.usercommands.city;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.database.bindclass.Member;
import ita.micc.meteorcity.enums.MemberRole;
import ita.micc.meteorcity.message.Message;
import ita.micc.meteorcity.playercity.PlayerCity;
import ita.micc.meteorcity.playercity.PlayerCityInvite;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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
        /* Check if player has an invite */
        if (!plugin.getInvites().containsKey(playerUUID)) {
            Message.INVITE_YOU_HAVENT.send(player);
            return false;
        }
        PlayerCityInvite invite = plugin.getInvites().get(playerUUID);
        /* check if player has a city */
        if (plugin.getCities().containsKey(playerUUID)) {
            Message.CITY_PLAYER_HAS_ALREADY_A_CITY.send(player);
            plugin.getInvites().remove(playerUUID);
            return false;
        }
        if (invite.expired()) {
            Message.INVITE_EXPIRED.send(player);
            return false;
        }

        /* player accept invite */
        PlayerCity playerCity = invite.getPlayerCity();
        playerCity.addMember(playerUUID, MemberRole.CITTADINO);
        for (Member member : playerCity.getMembers()) {
            Player playerMember = Bukkit.getPlayer(UUID.fromString(member.getUUID()));
            if (playerMember != null) {
                Message.INVITE_SUCCESS_JOIN.sendWithReplace(playerMember, "{player}", player.getName());
            }
        }
        return false;
    }
}
