package ita.micc.meteorcity.listener;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.database.bindclass.LocationZone;
import ita.micc.meteorcity.enums.BuildType;
import ita.micc.meteorcity.message.Message;
import ita.micc.meteorcity.playercity.PlayerCity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public record EventsOnClickBuild(MeteorCity plugin) implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        String playerUUID = player.getUniqueId().toString();

        if (!player.getLocation().getWorld().getName().equals(plugin.getCityWorldName())) {
            return;
        }
        if (!plugin.getCities().containsKey(playerUUID)) {
            return;
        }
        if (block == null) {
            return;
        }
        if (block.getType() == Material.VOID_AIR) {
            return;
        }
        PlayerCity playerCity = plugin.getCities().get(playerUUID);
        LocationZone locationZone = playerCity.locationInAZone(block.getLocation());
        if (locationZone == null) {
            return;
        }

        if (locationZone.toEnumType() == BuildType.TOWN_HALL) {
            /* open town_Hall gui */
            return;
        }
        if (locationZone.toEnumType() == BuildType.EMPTY) {
            Message.CITY_PLAYER_ZONE_IS_EMPTY.send(player);
            return;
        }
        if (locationZone.toEnumType() == BuildType.CATASTO) {
            /* open catasto gui */
            return;
        }
        if (locationZone.toEnumType() == BuildType.PENTAGONO) {
            /* open pentagono gui */
            return;
        }
        if (locationZone.toEnumType() == BuildType.BROKER) {
            /* open broker gui */
            return;
        }
    }
}
