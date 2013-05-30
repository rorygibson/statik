package statik.util;

import org.apache.commons.lang3.StringUtils;

public class UsernameValidator {
    public boolean validUsername(String username) {
        return StringUtils.isNotBlank(username) && username.matches("\\S+@\\S+");
    }
}
