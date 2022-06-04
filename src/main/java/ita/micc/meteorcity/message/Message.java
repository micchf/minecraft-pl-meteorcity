package ita.micc.meteorcity.message;

import ita.micc.meteorcity.MeteorCity;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** All messages
 * @author Codeh
 */
public enum Message {
    NO_PERM("no-perm"),
    BAD_SYNTAX("bad-syntax"),
    ONLY_PLAYER("only-player"),
    NOT_ONLINE("not-online"),

    CITY_ADMIN_REFRESHTEMPLATES("city-admin-refreshtemplates"),

    CITY_PLAYER_HAS_ALREADY_A_CITY("city-player-has-already-a-city"),
    CITY_PLAYER_HAS_ALREADY_A_CITY_IN_BUILD("city-player-has-already-a-city-in-build"),
    CITY_PLAYER_HASNT_A_CITY("city-player-hasnt-a-city"),
    CITY_PLAYER_HAS_A_CITY_IN_LOAD_FROM_DATABASE("city-player-has-a-city-in-load-from-database"),
    CITY_PLAYER_NOT_OWNER("city-player-not-owner"),
    CITY_PLAYER_CITY_IN_DISBAND("city-player-city-in-disband"),
    CITY_PLAYER_NOT_ROLE("city-player-not-role"),
    CITY_PLAYER_NOT_IN_THE_CITY("city-player-not-in-the-city"),
    CITY_PLAYER_SPAWN_UPDATE("city-player-spawn-update"),
    CITY_PLAYER_JOIN_CITY("city-player-join-city"),
    CITY_PLAYER_TARGET_ROLE_UPDATE("city-player-target-role-update"),

    INVALID_ROLE("invalid-role"),

    CITY_ERROR_DURING_DISBAND("city-error-during-disband"),
    CITY_DISBAND_SUCCESS("city-disband-success"),
    CITY_IN_DISBAND("city-in-disband"),

    TARGET_IS_NOT_MEMBER_CITY("target-is-not-member-city"),
    TARGET_ROLE_UPDATE("target-role-update"),
    TARGET_NOT_ONLINE("target-not-online"),
    TARGET_YOU_ARE("target-you-are"),
    TARGET_HAS_A_CITY("target-has-a-city"),
    TARGET_SEND_INVITE("target-send-invite"),
    TARGET_MESSAGE_TO_TARGET("target-message-to-target"),
    TARGET_HAS_ALREADY_AN_INVITE("target-has-already-an-invite"),

    INVITE_YOU_HAVENT("invite-you-havent"),
    INVITE_EXPIRED("invite-expired"),
    INVITE_SUCCESS_JOIN("invite-success-join"),

    CITY_START_CREATION("city-start-creation"),
    CITY_SUCCESS_CREATION("city-success-creation"),
    CITY_ERROR_CREATION("city-error-creation"),

    TEMPLATE_DONT_EXIST("template-dont-exist"),
    TEMPLATE_NO_PERM("template-no-perm");

    private final String text;
    private static final Map<String, String> messages = new HashMap<>();

    Message(final String text) {
        this.text = text;
    }

    /**
     * Send a message from config to specific sender
     * @param sender who will be sent message
     */
    public void send(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.get(text)));
    }

    /**
     * Send a message with a message's replace
     * @param sender who will be sent message
     * @param s string to be replaced
     * @param replace text that will be replaced
     */
    public void sendWithReplace(CommandSender sender, String s, String replace) {
        String msg = messages.get(text).replaceAll(s, replace);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    static {
        MeteorCity plugin = MeteorCity.getPlugin(MeteorCity.class);
        for(String key : Objects.requireNonNull(plugin.getConfig().getConfigurationSection("messages")).getKeys(false)) {
            messages.put(key, plugin.getConfig().getString("messages." + key));
        }
    }
}