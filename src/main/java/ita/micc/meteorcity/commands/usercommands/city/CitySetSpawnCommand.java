package ita.micc.meteorcity.commands.usercommands.city;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.enums.MemberRole;
import ita.micc.meteorcity.message.Message;
import ita.micc.meteorcity.playercity.PlayerCity;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * /city setspawn
 * @author Codeh
 */
public record CitySetSpawnCommand(MeteorCity plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        String playerUUID = player.getUniqueId().toString();
        /* check if command is /city go */
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
        /* check if player is presidente or funzionario */
        MemberRole playerRole = playerCity.getMemberRole(playerUUID);
        if (!(playerRole == MemberRole.PRESIDENTE || playerRole == MemberRole.FUNZIONARIO)) {
            Message.CITY_PLAYER_NOT_ROLE.send(player);
            return false;
        }
        /* check if player is in the city */
        if (!playerCity.getMain().contains(player.getLocation())) {
            Message.CITY_PLAYER_NOT_IN_THE_CITY.send(player);
            return false;
        }

        /* update city spawn */
        playerCity.setNewPlayerSpawn(player.getLocation());
        Message.CITY_PLAYER_SPAWN_UPDATE.send(player);
        return false;
    }
}
