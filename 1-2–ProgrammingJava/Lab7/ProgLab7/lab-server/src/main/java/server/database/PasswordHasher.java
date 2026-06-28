package server.database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Класс {@code PasswordHasher} используется для хэширования пароля пользователя для последующего хранения в БД
 *
 * @author mikhailkramar
 */
public class PasswordHasher {
    //"перец" - строка, которая известна только нашему приложению Server
    private static final String PEPPER = "*6@3$21*@0?/!7)+";

    /**
     * Метод, который хэширует пароль пользователя при помощи алгоритма SHA-1
     * @param userPassword пароль пользователя
     * @return хэш
     */
    public static String hashSHA1(String userPassword, String SALT) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            String finalPassword = SALT + userPassword + PEPPER;

            md.update(finalPassword.getBytes());
            byte[] digest = md.digest();

            return bytesToHex(digest);

        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();

        for (var b : bytes) {
            stringBuilder.append(String.format("%02x", b & 0xff));
        }

        return stringBuilder.toString();
    }

}
