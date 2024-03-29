package ita.micc.meteorcity.listener;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.buildsettings.BuildSettings;
import ita.micc.meteorcity.database.bindclass.LocationZone;
import ita.micc.meteorcity.enums.BuildType;
import ita.micc.meteorcity.message.Message;
import ita.micc.meteorcity.playercity.PlayerCity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

/** Class event for open GUI
 * @author Codeh
 */
public record EventsOnClickBuild(MeteorCity plugin) implements Listener {

    @EventHandler
    public void onClickBuild(PlayerInteractEvent event) {
        /* async task */
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            String playerUUID = player.getUniqueId().toString();

            /* check if player is in city's world */
            if (!Objects.requireNonNull(player.getLocation().getWorld()).getName().equals(plugin.getCityWorldName())) {
                return;
            }
            /* check if player has a city */
            if (!plugin.getCities().containsKey(playerUUID)) {
                return;
            }
            /* check if block is null */
            if (block == null) {
                return;
            }
            /* check if material's block is VOID */
            if (block.getType() == Material.VOID_AIR) {
                return;
            }
            PlayerCity playerCity = plugin.getCities().get(playerUUID);
            /* get location with player click */
            LocationZone locationZone = playerCity.locationInAZone(block.getLocation());
            /* check if is valid location */
            if (locationZone == null) {
                return;
            }
            /* check if location is EMPTY */
            if (locationZone.toEnumType() == BuildType.EMPTY) {
                Message.CITY_PLAYER_ZONE_IS_EMPTY.send(player);
                return;
            }
            /* check if location is WILD_ZONE */
            if (locationZone.toEnumType() == BuildType.WILD_ZONE) {
                return;
            }

            /* init location open gui */
            BuildSettings buildSettings = plugin.getBuildSettings().get(locationZone.toEnumType());
            Bukkit.getScheduler().runTask(plugin, () -> buildSettings.openInfoGUI(player));
        });
    }

    @EventHandler
    public void test(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Creeper)) {
            return;
        }
        if (!entity.getLocation().getWorld().getName().equals(plugin.getCityWorldName())) {
            return;
        }
        for (PlayerCity playerCity : plugin.getCities().values()) {
            if (!(playerCity.getWildZone().contains(entity.getLocation()))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void test1(BlockExplodeEvent event) {
        Block block = event.getBlock();
        if (!(block.getType() == Material.DIRT)) {
            return;
        }
        if (!block.getLocation().getWorld().getName().equals(plugin.getCityWorldName())) {
            return;
        }
        event.setCancelled(true);
    }
}
