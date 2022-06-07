package ita.micc.meteorcity.listener;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.database.bindclass.IDCity;
import ita.micc.meteorcity.database.query.QueryInfo;
import ita.micc.meteorcity.playercity.PlayerCity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
        /* check if player has already a city in build */
        if (player.hasMetadata("city_in_build")) {
            return;
        }
        /* check if player has already a city in load */
        if (player.hasMetadata("city_in_load")) {
            return;
        }
        /* check if a member has already city loaded into hashmap */
        for (PlayerCity playerCity : new HashSet<>(plugin.getCities().values())) {
            if (playerCity.getMembers().containsKey(playerUUID)) {
                plugin.getCities().put(playerUUID, playerCity);
                return;
            }
        }

        player.removeMetadata("city_in_load", plugin);
        /* async task */
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            /* check if player has already a city saved into database */
            try {
                if (!plugin.getDatabaseInstance().existRow("SELECT * FROM members WHERE UUID = '" + playerUUID + "'")) {
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            } finally {
                player.removeMetadata("city_in_load", plugin);
            }

            /* for get city's id, query IDCity from members table  */
            QueryInfo queryInfo = new QueryInfo("SELECT IDCity FROM members WHERE UUID = :UUID", null);
            queryInfo.addParameter("UUID", playerUUID);
            IDCity IDCity = plugin.getDatabaseInstance().fetchClassData(IDCity.class, queryInfo).get(0);
            PlayerCity playerCity = new PlayerCity(IDCity.getIDCity(), plugin);
            player.removeMetadata("city_in_load", plugin);
            /* sync task (bukkit thread) */
            Bukkit.getScheduler().runTask(plugin, () -> plugin.getCities().put(playerUUID, playerCity));
        });
    }
}
