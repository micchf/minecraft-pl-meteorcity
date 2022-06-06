package ita.micc.meteorcity.playercity.blockpaste;

import ita.micc.meteorcity.enums.BuildType;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
    private final @Getter ItemStack itemStack;

    /**
     * Constructor
     * @param displayName of block
     * @param loreFromConfig of block
     * @param buildType type
     */
    public BlockPaste(String displayName, List<String> loreFromConfig, BuildType buildType, Material material) {
        lore = new ArrayList<>();
        this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        loreFromConfig.forEach(lore -> this.lore.add(ChatColor.translateAlternateColorCodes('&', lore)));
        this.type = buildType;

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(this.displayName);
        itemMeta.setLore(this.lore);
        itemStack.setItemMeta(itemMeta);
        this.itemStack = itemStack;
    }
}
