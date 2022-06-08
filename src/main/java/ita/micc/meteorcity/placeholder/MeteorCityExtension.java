package ita.micc.meteorcity.placeholder;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.playercity.PlayerCity;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MeteorCityExtension extends PlaceholderExpansion {

    private final MeteorCity plugin;

    public MeteorCityExtension(MeteorCity plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        String playerUUID = player.getUniqueId().toString();
        PlayerCity playerCity = plugin.getCities().get(playerUUID);
        if (playerCity == null) {
            return null;
        }

        if (params.equals("townHall_level")) {
            return String.valueOf(playerCity.getTownHall().getLevel());
        }
        if (params.equals("city_rank")) {
            return playerCity.getMemberByUUID(playerUUID).getRole();
        }
        return null;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "meteorcity";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Codeh";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }
}
