package statik.util;

public class PasswordValidator {

    private static final int MIN_LENGTH = 8;


    public boolean validPasswords(String password, String passwordAgain) {
        boolean isNull = (password == null || passwordAgain == null);
        if (isNull) {
            return false;
        }

        boolean matching = password.equals(passwordAgain);
        if (!matching) {
            return false;
        }

        boolean nonBlank = !password.equals("");
        boolean longEnough = password.length() >= MIN_LENGTH;

        return nonBlank && longEnough && containsNoCharacterBlocksLongerThan(2, password);
    }

    protected boolean containsNoCharacterBlocksLongerThan(int maxBlockLength, String password) {
        for (int i=0; i < password.length() - maxBlockLength; i++) {
            String sub = password.substring(i, i + maxBlockLength + 1);
            if (sub.matches("\\d+")) {
                return false;
            }
        }
        return true;
    }
}