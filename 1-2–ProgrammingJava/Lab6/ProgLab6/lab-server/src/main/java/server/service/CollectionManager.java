package server.service;

import common.model.Organization;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Класс {@code CollectionManager} отвечает за хранение элементов типа Organization в коллекции LinkedList,
 * их добавление, сортировку через Stream API и проверку уникальности поля fullName
 *
 * @author mikhailkramar
 */
public class CollectionManager {
    private final LinkedList<Organization> collection = new LinkedList<>();
    private final LocalDateTime initializationDate;
    private Long lastId = 0L;
    private final DumpManager dumpManager;

    /**
     * Конструктор менеджера коллекции
     *
     * @param dumpManager объект класса DumpManager, который управляет сохранением в файл и загрузкой из файла
     */
    public CollectionManager(DumpManager dumpManager) {
        this.initializationDate = LocalDateTime.now();
        this.dumpManager = dumpManager;
    }

    public synchronized void add(Organization org) {
        collection.add(org);
    }

    public synchronized void clear() {
        collection.clear();
    }

    /**
     * Метод для генерации следующего доступного ID
     * @return максимальный доступный ID
     */
    public synchronized Long generateNextId() {
        return ++lastId;
    }

    /**
     * Метод, для обновления последнего ID
     * @param id идентификационный номер организации
     */
    public synchronized void updateLastId(Long id) {
        if (id > lastId) {
            lastId = id;
        }
    }

    /**
     * Метод для получения объекта организации по ID
     * @param id идентификационный номер организации
     * @return объект организации
     */
    public synchronized Organization getById(Long id) {
        return collection.stream().filter(org -> Objects.equals(org.getId(), id)).findFirst().orElse(null);
    }

    /**
     * Метод для проверки поля fullName на уникальность
     * @param input введённый fullName
     * @return true, если уникальный, иначе - false
     */
    public synchronized boolean fullNameIsUnique(String input) {
        if (input == null || input.isEmpty()) return true;
        return collection.stream().noneMatch(org -> input.equals(org.getFullName()));
    }

    /**
     * Метод, который возвращает отсортированную по местоположению (взял только по координате X) коллекцию
     * @return коллекция
     */
    public synchronized LinkedList<Organization> getCollection() {
        LinkedList<Organization> sortedCollection = collection.stream().sorted(Comparator.comparing(o -> o.getCoordinates().getX())).collect(Collectors.toCollection(LinkedList::new));
        this.collection.clear();
        this.collection.addAll(sortedCollection);
        return this.collection;

    }

    /**
     * Метод для сохранения коллекции в файл через dumpManager
     */
    public synchronized String save() {
        return dumpManager.save(this.collection);
    }

    /**
     * Метод для получения информации о коллекции
     * @return строка, содержащая необходимую информацию
     */
    public synchronized String getInfo() {
        return "Тип коллекции: " + collection.getClass().getSimpleName() + "\n" +
                "Дата инициализации: "  + (initializationDate != null ? initializationDate : "не инициализирована") + "\n" +
                "Количество элементов: " + collection.size();
    }

    /**
     * Метод для получения размера коллекции
     * @return размер коллекции
     */
    public synchronized int size() {
        return collection.size();
    }
}
