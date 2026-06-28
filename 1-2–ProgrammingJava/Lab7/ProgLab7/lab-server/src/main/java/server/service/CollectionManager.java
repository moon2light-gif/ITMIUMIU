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
    private final LinkedList<Organization> collection = new LinkedList<>(); //коллекция, которой мы управляем
    private final LocalDateTime initializationDate; //дата инициализации коллекции

    /**
     * Конструктор менеджера коллекции
     *
     */
    public CollectionManager() {
        this.initializationDate = LocalDateTime.now();
    }

    /**
     * Метод, позволяющий добавлять элемент в коллекцию
     * @param org объект организации
     */
    public synchronized void add(Organization org) {
        collection.add(org);
    }

    /**
     * Метод для проверки коллекции на пустоту, основанный на ID пользователя
     * @param ownerId ID пользователя
     * @return true, если в общей коллекции нет ни одного элемента пользователя, иначе - false
     */
    public synchronized boolean isUserCollectionEmpty(Long ownerId) {
        return collection.stream().noneMatch(org -> org.getOwnerId().equals(ownerId));
    }

    /**
     * Метод, предназначенный для удаления элементов коллекции, которые закреплены за конкретным пользователем
     * @param ownerId ID пользователя
     */
    public synchronized void clearByUser(Long ownerId) {
        collection.removeIf(org -> org.getOwnerId().equals(ownerId));
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
     * Метод, предназначенный для возвращения копии коллекции (нужен для многопоточного взаимодействия)
     * @return копия коллекции объектов Organization
     */
    public synchronized LinkedList<Organization> getCollectionCopy() {
        return new LinkedList<Organization>(collection);
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
