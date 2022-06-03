package ita.micc.meteorcity.commands.usercommands;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.citytemplate.CityTemplate;
import ita.micc.meteorcity.database.DatabaseInstance;
import ita.micc.meteorcity.database.bindclass.SpawnPoint;
import ita.micc.meteorcity.message.Message;
import ita.micc.meteorcity.playercity.PlayerCity;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

/**
 * /city new <templateName>
 * @author Codeh
 */
public record CityNewCommand(MeteorCity plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        String playerUUID = player.getUniqueId().toString();
        /* check if command is /city new <templateName> */
        if (args.length != 1) {
            Message.BAD_SYNTAX.send(player);
            return false;
        }
        /* check if player has a city */
        if (plugin.getCities().containsKey(playerUUID)) {
            Message.CITY_PLAYER_HAS_ALREADY_A_CITY.send(player);
            return false;
        }
        /* check if player has already a city in build */
        if (player.hasMetadata("city_in_build")) {
            Message.CITY_PLAYER_HAS_ALREADY_A_CITY_IN_BUILD.send(player);
            return false;
        }
        /* check if player has already a city in load from database */
        if (player.hasMetadata("city_in_load")) {
            Message.CITY_PLAYER_HAS_A_CITY_IN_LOAD_FROM_DATABASE.send(player);
            return false;
        }
        /* check if template exist */
        if (!plugin.getCityTemplates().containsKey(args[0].toUpperCase())) {
            Message.TEMPLATE_DONT_EXIST.send(player);
            return false;
        }
        CityTemplate cityTemplate = plugin.getCityTemplates().get(args[0].toUpperCase());
        /* check if player can use template */
        if (!player.hasPermission(cityTemplate.getPermissionRequired())) {
            Message.TEMPLATE_NO_PERM.send(player);
            return false;
        }

        /* start city's build */
        Message.CITY_START_CREATION.send(player);
        SpawnPoint lastPoint = plugin.getLastPoint().clone();
        /* check if lastpoint can be increased, if not return */
        if (!plugin.increaseLastPoint()) {
            Message.CITY_ERROR_CREATION.send(player);
            return false;
        }

        player.setMetadata("city_in_build", new FixedMetadataValue(plugin, true));
        /* Async task (another thread) */
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            DatabaseInstance databaseInstance = plugin.getDatabaseInstance();
            PlayerCity playerCity = new PlayerCity(cityTemplate, lastPoint, playerUUID);
            /* check if the city can be pasted and saved into database */
            if (!playerCity.insertCityIntoDatabase(databaseInstance) || !playerCity.pasteCitySchematic()) {
                Message.CITY_ERROR_CREATION.send(player);
                /* Sync task (bukkit thread) */
                Bukkit.getScheduler().runTask(plugin, () -> player.removeMetadata("city_in_build", plugin)); /* Sync task called after if, "city_in_build" cancelled */
                return;
            }
            /* Sync task (bukkit thread) */
            Bukkit.getScheduler().runTask(plugin, () -> {
                /* add city into hashmap, then remove "city_in_build" */
                plugin.getCities().put(playerUUID, playerCity); /*  */
                player.removeMetadata("city_in_build", plugin);
                Message.CITY_SUCCESS_CREATION.send(player);
            });
        });
        return false;
    }
}
