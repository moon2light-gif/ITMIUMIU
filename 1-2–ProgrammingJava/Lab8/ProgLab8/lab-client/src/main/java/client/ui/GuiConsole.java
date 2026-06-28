package client.ui;

import client.connection.Client;
import client.utility.ElementReader;
import client.utility.RequestBuilder;
import common.transport.Request;
import common.transport.Response;
import common.utility.ArgumentType;
import common.utility.CommandDescriptor;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Класс {@code GuiConsole} встраивает оригинальную консоль клиента прямо в наш графический интерфейс
 *
 * @author mikhailkramar
 */
public class GuiConsole {

    private static final Set<String> MUTATING = Set.of("add", "add_if_min", "update", "remove_by_id", "remove_first", "clear");

    private final Client client;
    private final String login;
    private final String password;
    private final TextArea output;
    private final Runnable onLogout;
    private final Runnable onChange;

    private PipedOutputStream pipeOut;
    private Scanner scanner;
    private ElementReader elementReader;
    private RequestBuilder requestBuilder;
    private Map<String, CommandDescriptor> commands;

    private Thread worker;
    private volatile boolean running;

    private PrintStream originalOut;
    private PrintStream originalErr;
    private PrintStream consoleStream;

    public GuiConsole(Client client, String login, String password, TextArea output,
                      Runnable onLogout, Runnable onChange) {
        this.client = client;
        this.login = login;
        this.password = password;
        this.output = output;
        this.onLogout = onLogout;
        this.onChange = onChange;
    }

    /**
     * Запускает консоль, перенаправляет вывод, готовит ввод и запускает поток-обработчик!!!
     */
    public void start() {
        originalOut = System.out;
        originalErr = System.err;
        consoleStream = new PrintStream(new TextAreaStream(output), true, StandardCharsets.UTF_8);
        System.setOut(consoleStream);
        System.setErr(consoleStream);

        try {
            pipeOut = new PipedOutputStream();
            PipedInputStream pipeIn = new PipedInputStream(pipeOut, 1 << 16);
            scanner = new Scanner(pipeIn, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            output.appendText("Не удалось инициализировать консоль: " + ex.getMessage() + "\n");
            return;
        }

        elementReader = new ElementReader(scanner);
        requestBuilder = new RequestBuilder(elementReader, new HashMap<>());
        elementReader.setRequestBuilder(requestBuilder);
        requestBuilder.setUdpClient(client);
        requestBuilder.setUserData(login, password);

        running = true;
        worker = new Thread(this::loop, "gui-console");
        worker.setDaemon(true);
        worker.start();
    }

    /**
     * Передаёт строку из поля ввода в консоль (просто для удобства)
     */
    public void submit(String line) {
        output.appendText("> " + line + "\n");
        try {
            if (pipeOut != null) {
                pipeOut.write((line + "\n").getBytes(StandardCharsets.UTF_8));
                pipeOut.flush();
            }
        } catch (IOException ex) {
            output.appendText("Консоль недоступна.\n");
        }
    }

    /**
     * Останавливает нашу консоль и восстанавливает потоки вывода
     */
    public void stop() {
        running = false;
        try {
            if (pipeOut != null) pipeOut.close();
        } catch (IOException ignored) { }
        if (worker != null) worker.interrupt();
        if (originalOut != null) System.setOut(originalOut);
        if (originalErr != null) System.setErr(originalErr);
    }

    private void loop() {
        ensureCommands();

        while (running && scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String cmd = line.split("\\s+", 2)[0].toLowerCase();
            try {
                if (cmd.equals("exit")) {
                    System.out.println("Завершение работы.");
                    Platform.runLater(Platform::exit);
                    break;
                }
                if (cmd.equals("logout")) {
                    System.out.println("Вы вышли из учётной записи.");
                    Platform.runLater(onLogout);
                    break;
                }
                if (cmd.equals("execute_script")) {
                    runScript(line);
                    continue;
                }

                ensureCommands();
                Request request = requestBuilder.buildRequest(line, false);
                if (request == null) continue;

                Response response = client.sendAndReceive(request);
                printResponse(response);

                if (MUTATING.contains(cmd) && response != null && response.isSuccess()) {
                    Platform.runLater(onChange);
                }
            } catch (Exception ex) {
                System.out.println("Ошибка выполнения: " + ex.getMessage());
            }
        }
    }

    /**
     * Выполнение скрипта: переключаем чтение объекта на файлик
     */
    private void runScript(String line) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            System.out.println("Ошибка: execute_script требует путь к файлу!");
            return;
        }
        File file = new File(parts[1].trim());
        if (!file.exists()) {
            System.out.println("Файл не найден: " + file.getPath());
            return;
        }

        Scanner fileScanner = null;
        try {
            fileScanner = new Scanner(file, StandardCharsets.UTF_8);
            elementReader.setInteractive(false);
            elementReader.setScanner(fileScanner);
            ensureCommands();

            while (fileScanner.hasNextLine()) {
                String l = fileScanner.nextLine().trim();
                if (l.isEmpty()) continue;
                String c = l.split("\\s+", 2)[0].toLowerCase();

                if (c.equals("exit") || c.equals("logout")) {
                    System.out.println("Команда '" + c + "' в скрипте пропущена.");
                    continue;
                }
                if (c.equals("execute_script")) {
                    System.out.println("Вложенные скрипты в GUI не поддерживаются.");
                    continue;
                }

                System.out.println("> " + l);
                Request req = requestBuilder.buildRequest(l, true);
                if (req == null) continue;
                printResponse(client.sendAndReceive(req));
            }
            System.out.println("Скрипт выполнен.");
            Platform.runLater(onChange);
        } catch (Exception ex) {
            System.out.println("Ошибка скрипта: " + ex.getMessage());
        } finally {
            if (fileScanner != null) fileScanner.close();
            elementReader.setInteractive(true);
            elementReader.setScanner(scanner);
        }
    }

    private void printResponse(Response response) {
        if (response == null) {
            System.out.println("Сервер недоступен.");
            return;
        }
        System.out.println(response.getMessage());
        if (response.getCollection() != null) {
            for (Object o : response.getCollection()) {
                System.out.println(o);
            }
        }
    }

    /**
     * Загружает список доступных команд с сервера
     */
    private void ensureCommands() {
        if (commands != null && !commands.isEmpty()) return;
        Response r = client.sendAndReceive(new Request(login, password, "return_map", null, null));
        Map<String, CommandDescriptor> map = (r == null) ? null : r.getCommandDescriptorMap();

        if (map == null) {
            System.out.println("Не удалось загрузить список команд с сервера (попробуйте ещё раз).");
            return;
        }

        map.put("exit", new CommandDescriptor("exit", ArgumentType.NONE, false));
        map.put("execute_script", new CommandDescriptor("execute_script", ArgumentType.STRING, false));
        map.put("logout", new CommandDescriptor("logout", ArgumentType.NONE, false));
        commands = map;
        requestBuilder.setCommands(map);
    }

    private static final class TextAreaStream extends OutputStream {
        private final TextArea area;
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        TextAreaStream(TextArea area) {
            this.area = area;
        }

        @Override
        public synchronized void write(int b) {
            buffer.write(b);
            if (b == '\n') flushBuffer();
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) {
            for (int i = 0; i < len; i++) write(b[off + i]);
        }

        @Override
        public synchronized void flush() {
            if (buffer.size() > 0) flushBuffer();
        }

        private void flushBuffer() {
            String text = buffer.toString(StandardCharsets.UTF_8);
            buffer.reset();
            Platform.runLater(() -> area.appendText(text));
        }
    }
}
