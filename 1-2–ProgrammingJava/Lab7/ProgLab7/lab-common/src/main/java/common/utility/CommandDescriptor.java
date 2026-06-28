package common.utility;

import java.io.Serializable;

/**
 * Класс-описание {@code CommandDescriptor} является "паспортом" команды,
 * содержит только сведения о команде
 *
 * @author mihailkramar
 */
public class CommandDescriptor implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final ArgumentType argumentType;
    private final boolean requiresObjectArgument;

    /**
     * Конструктор для CommandDescriptor
     * @param name имя команды
     * @param argumentType тип аргумента
     * @param requiresObjectArgument флаг нужен/не нужен элемент организации
     */
    public CommandDescriptor(String name, ArgumentType argumentType, boolean requiresObjectArgument) {
        this.name = name;
        this.argumentType = argumentType;
        this.requiresObjectArgument = requiresObjectArgument;
    }

    /**
     * Метод для получения имени команды
     * @return имя команды
     */
    public String getName() {
        return name;
    }

    /**
     * Метод для получения типа аргумента, который требует команда
     * @return тип аргумента
     */
    public ArgumentType getArgumentType() {
        return argumentType;
    }

    /**
     * Метод для проверки нужности/не нужности объекта организации команде
     * @return true, если команде нужен объект организации, иначе - false
     */
    public boolean isRequiresObjectArgument() {
        return requiresObjectArgument;
    }
}
