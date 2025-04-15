package kz.muhammadzahid.eem.util;

import kz.muhammadzahid.eem.entity.Role;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RoleUtil {

    public String getAvailableRolesMessage() {
        StringBuilder message = new StringBuilder("Available roles are: ");
        Role.RoleName[] availableRoles = Role.RoleName.values();
        for (int i = 0; i < availableRoles.length; i++) {
            message.append(availableRoles[i].name());
            if (i < availableRoles.length - 1) {
                message.append(", ");
            }
        }
        return message.toString();
    }
}
