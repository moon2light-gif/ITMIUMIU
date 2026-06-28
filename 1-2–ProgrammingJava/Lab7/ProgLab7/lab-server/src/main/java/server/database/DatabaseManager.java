package server.database;

import common.model.Organization;
import server.service.CollectionManager;

public interface DatabaseManager {
    long insert(String userLogin, Organization org);
    boolean update(String userLogin, Long organizationId, Organization org);
    boolean removeById(String userLogin, Long organizationId);
    boolean clearByUser(String userLogin);
    void load(CollectionManager collectionManager);
}
