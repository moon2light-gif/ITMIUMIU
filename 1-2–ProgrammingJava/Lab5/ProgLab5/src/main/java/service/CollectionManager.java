package service;

import java.util.LinkedList;
import model.Organization;
import java.time.LocalDateTime;
import java.util.Objects;

//будет содержать LinkedList с типом Organization и управлять этой коллекцией
//отвечает за целостность данных в списке
public class CollectionManager { // он же Receiver
    private final LinkedList<Organization> collection = new LinkedList<>();
    private final LocalDateTime initializationDate; //дата инициализации нашей коллекции
    private Long lastId = 0L;
    private final DumpManager dumpManager;

    public CollectionManager(DumpManager dumpManager) {
        this.initializationDate = LocalDateTime.now();
        this.dumpManager = dumpManager;
    }

    //добавление элемента в коллекцию
    public void add(Organization org) {
        collection.add(org);
    }

    //очистка коллекции
    public void clear() {
        collection.clear();
    }

    //метод для генерации следующего ID - для объектов из консоли
    public Long generateNextId() {
        return ++lastId;
    }

    //метод для DumpManager'а
    public void updateLastId(Long id) {
        if (id > lastId) {
            lastId = id;
        }
    }

    public Organization getById(Long id) {
        for (Organization org : collection) {
            if (Objects.equals(org.getId(), id)) {
                return org;
            }
        }
        return null;
    }

    //метод, который мы используем в OrganizationInputHandler
    public boolean fullNameIsUnique(String input) {
        if(input == null || input.isEmpty()) return true;
        for (Organization org : collection) {
            if (input.equals(org.getFullName())) {
                return false;
            }
        }
        return true;
    }

    public LinkedList<Organization> getCollection() {
        return collection;
    }

    public void save() {
        dumpManager.save(this.collection);
    }

    //этот метод будет использоваться командой info
    public String getInfo() {
        return "Тип коллекции: " + collection.getClass().getSimpleName() + "\n" +
                "Дата инициализации: "  + (initializationDate != null ? initializationDate : "не инициализирована") + "\n" +
                "Количество элементов: " + collection.size();
    }
}
