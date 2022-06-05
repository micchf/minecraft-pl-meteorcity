package ita.micc.meteorcity.commands.usercommands.city;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.database.bindclass.Member;
import ita.micc.meteorcity.enums.MemberRole;
import ita.micc.meteorcity.message.Message;
import ita.micc.meteorcity.playercity.PlayerCity;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * /city leave
 * @author Codeh
 */
public record CityLeaveCommand(MeteorCity plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        String playerUUID = player.getUniqueId().toString();
        /* Check if the command is ./city leave */
        if (args.length != 0) {
            Message.BAD_SYNTAX.send(player);
            return false;
        }
        /* check if player has a city */
        if (!plugin.getCities().containsKey(playerUUID)) {
            Message.CITY_PLAYER_HASNT_A_CITY.send(player);
            return false;
        }

        PlayerCity playerCity = plugin.getCities().get(playerUUID);
        /* check if player is city's owner */
        if (playerCity.getMemberRole(playerUUID) == MemberRole.PRESIDENTE) {
            /* check if owner is only city's member */
            if (playerCity.getMembers().size() == 1) {
                Message.CITY_PLAYER_YOU_ARE_ONLY_PLAYER.send(player);
                return false;
            }

            /* if owner, set new owner, random member */
            Member member = playerCity.getRandomMemberExcludeUUID(playerUUID);
            playerCity.updateMemberRole(member.getUUID(), MemberRole.PRESIDENTE);
            String playerNewOwnerName = Bukkit.getOfflinePlayer(UUID.fromString(member.getUUID())).getName();
            Message.CITY_PLAYER_REMOVE_FROM_OWMER_ROLE.send(player);
            playerCity.sendMessageAllMembers(Message.CITY_PLAYER_NEW_OWNER.valueReplaced("{player}", playerNewOwnerName));
        }

        playerCity.removeMember(playerUUID);
        plugin.getCities().remove(playerUUID);
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        Message.CITY_PLAYER_YOU_LEAVE.send(player);
        return false;
    }
}
