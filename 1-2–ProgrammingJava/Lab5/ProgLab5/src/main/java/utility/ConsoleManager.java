package utility;

import commands.Command;
import model.Coordinates;
import model.Organization;
import service.CollectionManager;
import service.CommandManager;
import service.RequestExecutor;
import transport.Request;

import java.io.File;
import java.util.Scanner;

//собираем идеальный Request
public class ConsoleManager {
    private final CommandManager commandManager;
    private final CollectionManager collectionManager;
    private final ElementReader elementReader;
    private final Scanner scanner;
    private final RequestExecutor requestExecutor;

    public ConsoleManager(CommandManager cm, CollectionManager collectionManager, ElementReader er, Scanner sc, RequestExecutor re) {
        this.commandManager = cm;
        this.elementReader = er;
        this.requestExecutor = re;
        this.scanner = sc;
        this.collectionManager = collectionManager;
    }

    public void start() {
        processInput(this.scanner, false);
    }

    public void fileMode(String fileName) {
        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            processInput(fileScanner, true);
            System.out.println("Скрипт " + fileName + " успешно выполнен!");
        } catch (Exception ex) {
            System.out.println("Ошибка скрипта: " + ex.getMessage());
        }
    }

    public void processInput(Scanner currentScanner, boolean isFileMode) {
        elementReader.setInteractive(!isFileMode);
        elementReader.setScanner(currentScanner);

        while (currentScanner.hasNextLine()) {

            String line = currentScanner.nextLine().trim();
            if (line.isEmpty()) continue; //пусть вводит, пока не ввёл

            try { //тут вызывается логика ввода из ElementReader'а
                Request request = buildRequest(line, isFileMode);

                if (request != null) {
                    requestExecutor.execute(request); //вызываем выполнение запроса
                }

            } catch (RuntimeException ex) { //ловим исключение из ElementReader'а
                System.out.println(ex.getMessage());
                if (isFileMode) break;
                continue; //возвращаемся в начало ввода, соответственно request не создаётся
            }
        }

        elementReader.setScanner(this.scanner);
        elementReader.setInteractive(true);

        if (!isFileMode) {
            System.out.println("\nПоток ввода завершён! Завершение работы...");
            System.exit(0);
        }
    }

    private Request buildRequest(String line, boolean isFileMode) {
        String[] parts = line.split("\\s+", 2);
        String name = parts[0].toLowerCase(); //
        String arg = parts.length > 1 ? parts[1] : "";

        Command command = commandManager.getCommand(name); //получаем команду по названию
        if (command == null) {
            System.out.println("Ошибка: Такая команда не найдена!");
            return null;
        }

        //валидация обычного аргумента/аргументов
        if (!validateArgs(command, arg)) {
            return null;
        }

        Organization org = null;
        //собираем Organization
        if (command.requiresObjectArgument()) {
            if (command.canAcceptObject(arg)) {
                //тут вызывается логика ввода из ElementReader'а
                org = new Organization(
                        elementReader.askName(),
                        new Coordinates(elementReader.askX(), elementReader.askY()),
                        elementReader.askAnnualTurnover(),
                        elementReader.askFullName(this.collectionManager),
                        elementReader.askType(),
                        elementReader.askOfficialAddress()
                );
            }
            else {
                System.out.println("Ошибка: Условия выполнения команды " + name + " не соблюдены (возможно ID не найден).");
                if (isFileMode) throw new RuntimeException("Прерывание из-за не соблюдения условий выполнения команды.");
                return null;
            }
        }
        return new Request(name, arg, org);
    }

    private boolean validateArgs(Command command, String arg) {
        ArgumentType type = command.getArgumentType(); //получаем тип аргумента у команды
        //если у команды нет аргумента, но пользователь зачем-то ввёл
        if (type == ArgumentType.NONE && !arg.isEmpty()) {
            System.out.println("Ошибка: Команда " + command.getName() + " не принимает аргументы!");
            return false;
        }
        //если у команды есть аргумент, но пользователь ничего не ввёл
        if (type != ArgumentType.NONE && arg.isEmpty()) {
            System.out.println("Ошибка: Команда " + command.getName() + " требует аргумент!");
            return false;
        }
        //если в строке после аргумента есть пробелы, значит, ввели больше одного аргумента (кстати, корректно отработает, если кто-то случайно ввёл update 12 и пробел после, так как до этого прописан trim())
        if (!arg.isEmpty() && arg.split("\\s+").length > 1) {
            System.out.println("Ошибка: Команда " + command.getName() + " принимает только один обычный аргумент!");
            return false;
        }
        //валидация ввёденных чисел
        try {
            if (type == ArgumentType.LONG) {
                Long.parseLong(arg);
            }
            if (type == ArgumentType.INT) {
                Integer.parseInt(arg);
            }
        } catch (NumberFormatException ex) {
            System.out.println("Ошибка: Аргумент должен быть числом типа " + type +"!");
            return false;
        }
        //если валидация успешно пройдена
        return true;
    }
}
