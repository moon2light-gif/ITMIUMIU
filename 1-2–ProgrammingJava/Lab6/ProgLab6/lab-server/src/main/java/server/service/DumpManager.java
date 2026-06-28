package server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper; //наш инструмент для работы с XML
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import common.model.Organization;
import common.utility.Validator;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList; //тип нашей коллекции
import java.util.List;
import java.util.Set;

/**
 * Класс {@code DumpManager} отвечает за запись данных в файл и за чтение данных из файла
 *
 * @author mikhailkramar
 */
public class DumpManager {
    private final String fileName; //имя файла, который получили
    private final XmlMapper xmlMapper; //специальный объект переводчик из Java в xml и обратно

    public DumpManager(String fileName) {
        this.fileName = fileName;
        this.xmlMapper = new XmlMapper();
        //jackson не знает, что такое LocalDateTime, поэтому нужно зарегистрировать этот модуль
        this.xmlMapper.registerModule(new JavaTimeModule());
        //делаем адекватный вид дат в xml
        this.xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //делаем так, чтобы запись велась не в одну строку
        this.xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Метод load позволяет читать данных из заданного XML-файла через BufferedInputStream
     * @param collectionManager менеджер коллекции
     */
    public void load(CollectionManager collectionManager) {
        File file = new File(fileName);

        if (!file.exists()) {
            System.out.println("Такой файл не найден, создана пустая коллекция!");
            return; //сразу выходим из метода
        }

        if (file.length() == 0) {
            System.out.println("Файл пустой, загружена пустая коллекция!");
            return;
        }

        if (!file.canRead()) {
            System.out.println("Ошибка: Нет прав на чтение файла!");
            return;
        }

        Set<Long> uniqueIds = new HashSet<>(); //храним уникальные id

        try (BufferedInputStream bs = new BufferedInputStream(new FileInputStream(file))) {
            var iter = xmlMapper.readerFor(Organization.class).readValues(bs);

            while (iter.hasNextValue()) {
                try {
                    Organization org = (Organization) iter.nextValue();
                    if (org != null) {
                        if (Validator.validateOrganization(org, collectionManager::fullNameIsUnique) && !uniqueIds.contains(org.getId())) {
                            collectionManager.add(org);
                            collectionManager.updateLastId(org.getId());
                            uniqueIds.add(org.getId());
                        } else {
                            System.out.println("Объект пропущен! Ошибка валидации (или дубликат ID).");
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Ошибка при чтении элемента (битая xml-структура или некорректный формат данных): он будет пропущен!");
                }
            }
            System.out.println("Коллекция загружена! Добавлено элементов: " + uniqueIds.size());
        } catch (Exception ex) {
            System.out.println("Критическая ошибка в XML-файле: Файл не может быть прочитан!");
        }
    }

    /**
     * Метод save позволяет сохранять элементы коллекции в строковом виде в XML-файл
     * @param collection коллекция, заполненная объектами Organization
     * @return сообщение, содержащее информацию о выполнении команды "save"
     */
    public String save(LinkedList<Organization> collection) {
        if (fileName == null || fileName.isEmpty()) {
            return "Ошибка: Не установлен путь к файлу!\n";
        }

        File file = new File(fileName);

        if (file.exists() && !file.canWrite()) {
            return "Ошибка: Нет прав на запись в файл: " + fileName + "\n";
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName))) {
            xmlMapper.writeValue(writer, collection);
            return "Коллекция была успешно сохранена в файл: " + fileName + "\n";
        } catch (IOException ex) {
            return "Ошибка при записи в файл: " + ex.getMessage() + "\n";
        }

    }

}

