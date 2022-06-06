package ita.micc.meteorcity.playercity.blockpaste;

import ita.micc.meteorcity.enums.BuildType;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * BlockPaste
 * @author Codeh.
 */
public class BlockPaste {

    private final @Getter String displayName;
    private final @Getter List<String> lore;
    private final @Getter BuildType type;

    /**
     * Constructor
     * @param displayName of block
     * @param loreFromConfig of block
     * @param buildType type
     */
    public BlockPaste(String displayName, List<String> loreFromConfig, BuildType buildType) {
        lore = new ArrayList<>();
        this.displayName = displayName;
        loreFromConfig.forEach(lore -> this.lore.add(ChatColor.translateAlternateColorCodes('&', lore)));
        this.type = buildType;
    }
}
