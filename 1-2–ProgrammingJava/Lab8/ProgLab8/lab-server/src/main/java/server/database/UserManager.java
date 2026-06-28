package server.database;

import common.transport.Request;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс {@code UserManager} отвечает за валидацию введённых данных пользователя с помощью отправки запросов к БД
 *
 * @author mikhailkramar
 */
public class UserManager {
    private static final Logger LOGGER = Logger.getLogger(UserManager.class.getName());
    private final ConnectionPool connectionPool;

    /**
     * Конструктор класса UserManager
     * @param connectionPool объект класса ConnectionPool
     */
    public UserManager(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    /**
     * Метод, отвечающий за валидацию введённых логина и пароля
     * @return true, если валидация успешна, иначе - false
     */
    public boolean authorizeUser(String userLogin, String userPassword) throws SQLException {
        String query = "SELECT password_hash, salt FROM proglab7_users WHERE login = ?;";
        Connection connection = null;

        try {
            connection = connectionPool.getConnection(); //выбрасываемое исключение

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setString(1, userLogin);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) { //если нашлась такая строка - значит пользователь с таким именем есть
                        String databaseHash = rs.getString("password_hash");
                        String salt = rs.getString("salt");
                        String clientHash = PasswordHasher.hashSHA1(userPassword, salt); //хэшируем пароль пользователя, который пришел к нам в чистом виде
                        return databaseHash.equals(clientHash); //true, если хэши равны
                    }
                }

            }
        }
        finally {
            connectionPool.releaseConnection(connection); //возвращаем "соединение" обратно в пул в качестве свободного
        }

        return false; //если ошибка не с подключением к БД, а с самим логином
        //если вылетит ошибка с подключением -> кидаем в команду LoginCommand на обработку
    }


    /**
     * Метод для регистрации пользователя
     * @return true, если регистрация успешна, иначе - false
     */
    public boolean registerUser(String userLogin, String userPassword) throws SQLException {
        String query = "INSERT INTO proglab7_users (login, password_hash, salt) VALUES (?, ?, ?);";
        String salt = java.util.UUID.randomUUID().toString(); //тут генерируем случайный набор символов в качестве 'соли'
        Connection connection = null;

        try {
            connection = connectionPool.getConnection();

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setString(1, userLogin);
                ps.setString(2, PasswordHasher.hashSHA1(userPassword, salt)); //хэширую уже с солью и перцем
                ps.setString(3, salt);

                ps.executeUpdate();
                return true;

            }

        } catch (SQLException ex) {
            if ("23505".equals(ex.getSQLState())) {
                // ^ это код ошибки, который может появиться, если мы вставляем в колонку с UNIQUE значение, которое уже есть среди кортежей
                LOGGER.log(Level.WARNING, "Логин " + userLogin + " уже занят!");
                return false;
            }
            throw ex; //иначе пробрасываем исключение дальше (в RegisterCommand), так как ошибка уже не связана с занятым логином
        }
        finally {
            connectionPool.releaseConnection(connection);
        }
    }

    /**
     * Важнейший метод для получения ID пользователя в базе данных по его логину
     * @return идентификационный номер пользователя в БД
     */
    public Long getUserByLogin(String userLogin) {
        String query = "SELECT id FROM proglab7_users WHERE login = ?;";
        Connection connection = null;

        try {
            connection = connectionPool.getConnection();

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setString(1, userLogin);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong("id");
                    }
                }
                return null; //если ошибки не выскочили и запрос не нашел ID в таблице, значит пользователя реально нет
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Ошибка SQL при получении ID пользователя: " + ex.getMessage());
            return -1L; //так помечена техническая ошибка
        }
        finally {
            connectionPool.releaseConnection(connection);
        }
    }


}
