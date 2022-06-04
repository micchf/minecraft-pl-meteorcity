package ita.micc.meteorcity.commands.admincommands.cityadmin;

import ita.micc.meteorcity.MeteorCity;
import ita.micc.meteorcity.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * /cityadmin refreshtemplates
 * @author Codeh
 */
public record CityAdminRefreshTemplates(MeteorCity plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        /* Check if command is /cityadmin refreshtemplates */
        if(args.length != 0) {
            Message.BAD_SYNTAX.send(sender);
            return false;
        }

        plugin.reloadTemplates();
        Message.CITY_ADMIN_REFRESHTEMPLATES.send(sender);
        return false;
    }
}
