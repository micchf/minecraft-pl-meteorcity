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

public record CityDisbandCommand(MeteorCity plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        String playerUUID = player.getUniqueId().toString();
        /* check if command is /city disband */
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
        MemberRole playerRole = playerCity.getMemberRole(playerUUID);
        if (!(playerRole == MemberRole.PRESIDENTE)) {
            Message.CITY_PLAYER_NOT_OWNER.send(player);
            return false;
        }

        /* init city disband */
        playerCity.setDisband(true);
        for (Player playerMember : playerCity.getAllMembersOnline()) {
            plugin.getCities().remove(playerMember.getUniqueId().toString());
            Message.CITY_PLAYER_CITY_IN_DISBAND.send(playerMember);
            if (playerCity.getMain().contains(playerMember.getLocation())) {
                playerMember.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            }
        }
        /* check if city is removed from database */
        if (!playerCity.removeCityFromDatabase(plugin.getDatabaseInstance())) {
            Message.CITY_ERROR_DURING_DISBAND.send(player);
            return false;
        }

        /* city's disband complete, send message to city's owner */
        Message.CITY_DISBAND_SUCCESS.send(player);
        return false;
    }
}
