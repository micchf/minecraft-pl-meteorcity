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
        /* check if city is already in disband */
        PlayerCity playerCity = plugin.getCities().get(playerUUID);
        if (playerCity.isDisband()) {
            Message.CITY_IN_DISBAND.send(player);
            return false;
        }
        /* check if player is city's owner */
        if (!(playerCity.getMemberByUUID(playerUUID) == MemberRole.PRESIDENTE)) {
            Message.CITY_PLAYER_NOT_OWNER.send(player);
            return false;
        }

        /* init city disband */
        playerCity.setDisband(true);
        for (Member member : playerCity.getMembers()) {
            Player memberPlayer = Bukkit.getPlayer(UUID.fromString(member.getUUID()));
            if (memberPlayer != null) {
                plugin.getCities().remove(member.getUUID());
                Message.CITY_PLAYER_CITY_IN_DISBAND.send(memberPlayer);
                /* check if anyone is in the city, then teleport to main spawn */
                if (playerCity.getMain().contains(memberPlayer.getLocation())) {
                    memberPlayer.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                }
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
