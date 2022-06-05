package ita.micc.meteorcity.commands.usercommands.city;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.enums.MemberRole;
import ita.micc.meteorcity.message.Message;
import ita.micc.meteorcity.playercity.PlayerCity;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * /city kick <playerName>
 * @author Codeh
 */
public record CityKickCommand(MeteorCity plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        String playerUUID = player.getUniqueId().toString();
        /* Check if the command is ./city kick <playerName> */
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
        /* check if target is online */
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Message.TARGET_NOT_ONLINE.send(player);
            return false;
        }
        /* check if target is a city's member */
        String targetUUID = target.getUniqueId().toString();
        PlayerCity playerCity = plugin.getCities().get(playerUUID);
        if (!playerCity.isMember(targetUUID)) {
            Message.TARGET_IS_NOT_MEMBER_CITY.send(player);
            return false;
        }
        /* check if player is city's owner */
        if (!(playerCity.getMemberRole(playerUUID) == MemberRole.PRESIDENTE)) {
            Message.CITY_PLAYER_NOT_ROLE.send(player);
            return false;
        }

        /* kick player */
        playerCity.removeMember(targetUUID, plugin);
        playerCity.sendMessageAllMembers(Message.CITY_PLAYER_KICK_MEMBER.valueReplaced("%player%", target.getName()));
        target.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        Message.CITY_PLAYER_YOU_ARE_KICKED.send(target);
        return false;
    }
}