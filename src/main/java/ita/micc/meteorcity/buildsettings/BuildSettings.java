package ita.micc.meteorcity.buildsettings;

import ita.micc.meteorcity.buildsettings.blockpaste.BlockPaste;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Build settings
 * @author Codeh.
 */
@Getter
public class BuildSettings {

    private final BlockPaste blockPaste;
    private final String guiName;

    /**
     * Constructor
     * @param blockPaste paste
     * @param guiName for open gui
     */
    public BuildSettings(BlockPaste blockPaste, String guiName) {
        this.blockPaste = blockPaste;
        this.guiName = guiName;
    }

    /**
     * open build gui info
     * @param player player
     */
    public void openInfoGUI(Player player) {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        Bukkit.dispatchCommand(console, "dm open " + guiName + " " + player.getName());
    }
}
