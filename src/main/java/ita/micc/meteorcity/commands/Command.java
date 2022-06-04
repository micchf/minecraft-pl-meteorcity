package ita.micc.meteorcity.commands;

import ita.micc.meteorcity.message.Message;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command root class
 * @author Codeh.
 */
@Getter
public abstract class Command implements CommandExecutor, TabCompleter {

    private final Map<String, CommandExecutor> subCommands;
    private final String permissionRequired;
    private final boolean onlyPlayer;

    /**
     *
     * @param permissionRequired permission required for execute all leaves commands.
     * @param onlyPlayer if only player can execute all leaves commands.
     */
    public Command(String permissionRequired, boolean onlyPlayer) {
        subCommands = new HashMap<>();
        this.permissionRequired = permissionRequired;
        this.onlyPlayer = onlyPlayer;
    }

    /**
     * Register command sub.
     * @param name sub command arg.
     * @param subCommandExecutor SubCommand's CommandExecutor interface.
     */
    public void register(String name, CommandExecutor subCommandExecutor) {
        subCommands.put(name.toLowerCase(), subCommandExecutor);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, String[] args) {
        /* Check if for execute all commands is required a common permission */
        if (permissionRequired != null && !sender.hasPermission(permissionRequired)) {
            Message.NO_PERM.send(sender);
            return false;
        }
        /* Check if only player can execute all commands */
        if (onlyPlayer && !(sender instanceof Player)) {
            Message.ONLY_PLAYER.send(sender);
            return false;
        }
        if (args.length == 0) {
            return executeCommand(sender, command, label, args);
        }

        CommandExecutor subCommandExecutor = subCommands.get(args[0].toLowerCase());
        return subCommandExecutor == null ? executeCommand(sender, command, label, args) : subCommandExecutor.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String alias, @NotNull String[] args) {
        /* Check if for tab all commands is required a common permission */
        if (permissionRequired != null && !sender.hasPermission(permissionRequired)) {
            return null;
        }
        /* Check if only player can tab all commands */
        if (onlyPlayer && !(sender instanceof Player)) {
            return null;
        }

        return tabComplete(sender, command, alias, args);
    }

    public abstract boolean executeCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args);

    public abstract List<String> tabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args);
}
