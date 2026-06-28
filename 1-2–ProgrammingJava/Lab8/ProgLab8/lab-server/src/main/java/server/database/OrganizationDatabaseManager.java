package server.database;

import common.model.Address;
import common.model.Coordinates;
import common.model.Organization;
import common.model.OrganizationType;
import server.service.CollectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс {@code OrganizationDatabaseManager} отвечает за управление содержимым коллекции,
 * расположенном в базе данных
 *
 * @author mikhailkramar
 */
public class OrganizationDatabaseManager implements DatabaseManager {
    private static final Logger LOGGER = Logger.getLogger(OrganizationDatabaseManager.class.getName());
    private final ConnectionPool connectionPool;
    private final UserManager userManager;

    /**
     * Конструктор класса OrganizationDatabaseManager
     * @param connectionPool объект класса пула соединений
     * @param userManager объект класса управления пользователем
     */
    public OrganizationDatabaseManager(ConnectionPool connectionPool, UserManager userManager) {
        this.connectionPool = connectionPool;
        this.userManager = userManager;
    }

    /**
     * Метод, предназначенный для добавления организации в БД
     * @param userLogin логин пользователя
     * @param org объект организации, который необходимо добавить
     * @return ID, который присваивается добавленному объекту
     */
    @Override
    public long insert(String userLogin, Organization org)  {
        String query = "INSERT INTO proglab7_organizations (" +
                "name, " +
                "coordinates_x, " +
                "coordinates_y, " +
                "creation_date, " +
                "annual_turnover, " +
                "full_name, " +
                "type, " +
                "official_address_street, " +
                "owner_id" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        Long ownerId = userManager.getUserByLogin(userLogin);
        if (ownerId == null || ownerId == -1L) {
            LOGGER.log(Level.WARNING, "Вставка отклонена: Пользователь не найден или БД сейчас недоступна!");
            return -1L;
        }
        org.setOwnerId(ownerId); //устанавливаем объекту организации ID того, кто его добавил

        Connection connection = null;

        try {
            connection = connectionPool.getConnection();

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setString(1, org.getName());
                ps.setFloat(2, org.getCoordinates().getX());
                ps.setLong(3, org.getCoordinates().getY());
                ps.setTimestamp(4, Timestamp.valueOf(org.getCreationDate()));
                ps.setInt(5, org.getAnnualTurnover());
                ps.setString(6, org.getFullName());
                if (org.getType() == null) {
                    ps.setNull( 7, Types.VARCHAR); //записываем NULL в БД
                } else {
                    ps.setString(7, org.getType().name());
                }
                ps.setString(8, org.getOfficialAddress().getStreet());
                ps.setLong(9, org.getOwnerId());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong("id"); //возвращаем присвоенный объекту организации ID
                    }
                }
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Ошибка SQL при добавлении организации для пользователя: " + userLogin, ex);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return -1L;
    }

    /**
     * Метод, предназначенный для обновления элемента в БД по его ID
     * @param userLogin логин пользователя, который собирается обновить элемент
     * @param newOrg новая организация, которую необходимо поставить на место старой
     * @return true, если обновление успешно, иначе - false
     */
    @Override
    public boolean update(String userLogin, Long organizationId, Organization newOrg) {
        String query = "UPDATE proglab7_organizations SET " +
                "name = ?, coordinates_x = ?, coordinates_y = ?, " +
                "annual_turnover = ?, full_name = ?, type = ?, official_address_street = ? " +
                "WHERE id = ? AND owner_id = ?";

        Long ownerId = userManager.getUserByLogin(userLogin);
        if (ownerId == null || ownerId == -1L) {
            LOGGER.log(Level.WARNING, "Обновление отклонено: Пользователь не найден или БД сейчас недоступна!");
            return false;
        }

        Connection connection = null;

        try {
            connection = connectionPool.getConnection();
            if (connection == null) return false;

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, newOrg.getName());
                ps.setFloat(2, newOrg.getCoordinates().getX());
                ps.setLong(3, newOrg.getCoordinates().getY());
                ps.setInt(4, newOrg.getAnnualTurnover());
                ps.setString(5, newOrg.getFullName());

                if (newOrg.getType() == null) {
                    ps.setNull(6, Types.VARCHAR);
                } else {
                    ps.setString(6, newOrg.getType().name());
                }

                ps.setString(7, newOrg.getOfficialAddress().getStreet());

                ps.setLong(8, organizationId);
                ps.setLong(9, ownerId);

                int updatedRows = ps.executeUpdate();
                return updatedRows > 0;
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Ошибка SQL при обновлении организации ID: " + organizationId, ex);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return false;
    }

    /**
     * Метод, предназначенный для удаления объекта организации из БД по его ID
     * @param userLogin логин пользователя, который пытается удалить элемент
     * @param organizationId ID организации, которую пытаются удалить
     * @return true, если объект успешно удалён из БД, иначе - false
     */
    @Override
    public boolean removeById(String userLogin, Long organizationId) {
        String query = "DELETE FROM proglab7_organizations " +
                "WHERE id = ? AND owner_id = ?";

        Long ownerId = userManager.getUserByLogin(userLogin);
        if (ownerId == null || ownerId == -1L) {
            return false;
        }

        Connection connection = null;

        try {
            connection = connectionPool.getConnection();
            if (connection == null) return false;

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, organizationId);
                ps.setLong(2, ownerId);

                int deletedRows = ps.executeUpdate();
                return deletedRows > 0;
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Ошибка SQL: Элемент коллекции с ID: " + organizationId + " не может быть удалён!", ex);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return false;
    }

    /**
     * Метод, позволяющий удалить все элементы коллекции, принадлежащие пользователю
     * @param userLogin логин пользователя, который собирается очистить коллекцию
     * @return true, если элементы успешно удалены, иначе - false
     */
    @Override
    public boolean clearByUser(String userLogin) {
        String query = "DELETE FROM proglab7_organizations WHERE owner_id = ?";

        Long ownerId = userManager.getUserByLogin(userLogin);
        if (ownerId == null || ownerId == -1L) {
            return false;
        }

        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            if (connection == null) return false;

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, ownerId);
                ps.executeUpdate();
                return true;
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Ошибка SQL при очистке коллекции пользователя: ", ex);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return false;
    }

    /**
     * Метод, предназначенный для загрузки всех данных из БД в память при старте сервера
     */
    @Override
    public void load(CollectionManager collectionManager) {
        String query = "SELECT * FROM proglab7_organizations";

        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            if (connection == null) return;

            try (PreparedStatement ps = connection.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {

                while(rs.next()) {
                    String name = rs.getString("name");
                    Coordinates coordinates = new Coordinates(rs.getFloat("coordinates_x"), rs.getLong("coordinates_y"));
                    LocalDateTime creationDate = rs.getTimestamp("creation_date").toLocalDateTime();
                    Integer annualTurnover = rs.getInt("annual_turnover");
                    String fullName = rs.getString("full_name");

                    String typeStr = rs.getString("type");
                    OrganizationType type = (typeStr != null) ? OrganizationType.valueOf(typeStr) : null;

                    Address officialAddress = new Address(rs.getString("official_address_street"));

                    Organization org = new Organization(
                            name,
                            coordinates,
                            annualTurnover,
                            fullName,
                            type,
                            officialAddress
                    );

                    org.setCreationDate(creationDate);
                    org.setId(rs.getLong("id"));
                    org.setOwnerId(rs.getLong("owner_id"));

                    collectionManager.add(org); //добавляем в память
                }

                LOGGER.log(Level.INFO, "Коллекция успешно загружена из БД. Найдено записей: " + collectionManager.size());
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Ошибка SQL при загрузке коллекции: " + ex.getMessage());
            System.err.println("Предупреждение: Не удалось загрузить коллекцию из базы данных!");
            System.err.println("Сервер запущен в автономном режиме с пустой коллекцией.");
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

}
