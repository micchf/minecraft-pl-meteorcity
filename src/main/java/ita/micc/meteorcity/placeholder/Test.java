package ita.micc.meteorcity.placeholder;

import ita.micc.meteorcity.MeteorCity;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Test extends PlaceholderExpansion {
    private MeteorCity plugin;

    public Test(MeteorCity plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equals("prova")) {
            String playerUUID = player.getUniqueId().toString();
            return String.valueOf(plugin.getCities().get(playerUUID).getTownHall().getLevel());
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
        return "1.0.0";
    }
}
