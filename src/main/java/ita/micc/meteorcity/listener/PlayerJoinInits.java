package ita.micc.meteorcity.listener;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.database.DatabaseInstance;
import ita.micc.meteorcity.database.bindclass.IDCity;
import ita.micc.meteorcity.database.query.QueryInfo;
import ita.micc.meteorcity.playercity.PlayerCity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.sql.SQLException;
import java.util.HashSet;

/** Events when player join
 * @author Codeh
 */
public record PlayerJoinInits(MeteorCity plugin) implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();
        /* check if player has already a city */
        if (plugin.getCities().containsKey(playerUUID)) {
            return;
        }
        /* check if a member has already city loaded into hashmap */
        for (PlayerCity playerCity : new HashSet<>(plugin.getCities().values())) {
            if (playerCity.getMembersMap().containsKey(playerUUID)) {
                plugin.getCities().put(playerUUID, playerCity);
                return;
            }
        }

        player.setMetadata("city_in_load", new FixedMetadataValue(plugin, true));
        /* async task (another thread) */
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            DatabaseInstance databaseInstance = plugin.getDatabaseInstance();
            try {
                /* check if player has already a city saved into database */
                if (!databaseInstance.existRow("SELECT * FROM members WHERE UUID = '" + playerUUID + "'")) {
                    /* sync task (bukkit thread) */
                    Bukkit.getScheduler().runTask(plugin, () -> player.removeMetadata("city_in_load", plugin)); /* if no exist, cancel city load */
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                /* sync task (bukkit thread) */
                Bukkit.getScheduler().runTask(plugin, () -> player.removeMetadata("city_in_load", plugin)); /* if exception, cancel city load */
                return;
            }

            /* for get city's id, query IDCity from members table  */
            QueryInfo queryInfo = new QueryInfo("SELECT IDCity FROM members WHERE UUID = :UUID", null);
            queryInfo.addParameter("UUID", playerUUID);
            IDCity IDCity = databaseInstance.fetchClassData(IDCity.class, queryInfo).get(0);
            PlayerCity playerCity = new PlayerCity(IDCity.getIDCity(), plugin);
            /* sync task (bukkit thread) */
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.removeMetadata("city_in_load", plugin); /* city load complete, can be removed */
                plugin.getCities().put(playerUUID, playerCity); /* put city loaded into hashmap (cache) */
            });
        });
    }
}
