package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper; //наш инструмент для работы с XML
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import model.Organization;
import utility.Validator;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList; //тип нашей коллекции
import java.util.List;
import java.util.Set;

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

    //чтение данных из файла через BufferedInputStream
    public void load(CollectionManager cm) {
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
                        if (Validator.validateOrganization(org, cm) && !uniqueIds.contains(org.getId())) {
                            cm.add(org);
                            cm.updateLastId(org.getId());
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

    //запись данных в файл через OutputStreamWriter
    public void save (LinkedList<Organization> collection) {
        if (fileName == null || fileName.isEmpty()) {
            System.out.println("Ошибка: Не установлен путь к файлу!");
            return;
        }

        File file = new File(fileName);

        if (file.exists() && !file.canWrite()) {
            System.out.println("Ошибка: Нет прав на запись в файл: " + fileName);
            return;
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName))) {
            xmlMapper.writeValue(writer, collection);
            System.out.println("Коллекция была успешно сохранена в файл: " + fileName);
        } catch (IOException ex) {
            System.out.println("Ошибка при записи в файл: " + ex.getMessage());
        }

    }

}
