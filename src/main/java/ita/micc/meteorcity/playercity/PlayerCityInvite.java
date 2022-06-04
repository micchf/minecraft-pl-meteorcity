package ita.micc.meteorcity.playercity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Class for playerCity's invite
 * @author Codeh
 */
@Getter
@AllArgsConstructor
public class PlayerCityInvite {

    private Long millisSend;
    private PlayerCity playerCity;

    /**
     * Check if invite is expired
     * @return true if is expired, false if not.
     */
    public boolean expired() {
        return ((System.currentTimeMillis() - millisSend) >= 20000);
    }
}
