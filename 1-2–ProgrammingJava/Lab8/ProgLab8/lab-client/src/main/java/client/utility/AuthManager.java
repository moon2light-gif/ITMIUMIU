package client.utility;

import client.connection.Client;
import common.transport.Request;
import common.transport.Response;

import java.util.Scanner;

/**
 * Класс {@code AuthManager} отвечает за меню ввода данных пользователя, его авторизацию в системе
 *
 * @author mikhailkramar
 */
public class AuthManager {
    private final Scanner scanner;
    private final Client udpClient;

    private String userLogin;
    private String userPassword;

    /**
     * Конструктор класса AuthManager
     * @param scanner текущий сканер
     * @param udpClient объект UDPClient
     */
    public AuthManager(Scanner scanner, Client udpClient) {
        this.scanner = scanner;
        this.udpClient = udpClient;
    }

    /**
     * Метод запуска меню авторизации пользователя
     */
    public void runAuthorizationMenu() {
        //перед началом каждой новой авторизации устанавливаем данные о пользователе в null'ы
        this.userLogin = null;
        this.userPassword = null;

        System.out.println("~~~~~~~~~~~~~~Меню авторизации~~~~~~~~~~~~~~");

        while (true) {
            System.out.println("1. Вход");
            System.out.println("2. Регистрация");
            System.out.println("3. Выход из приложения");
            System.out.print("Выберите действие (цифра 1-3): ");

            String userChoice = scanner.nextLine().trim();

            String inputLogin = null;
            String inputPassword = null;

            if (userChoice.equals("1")) {
                System.out.print("Введите свой логин: ");
                inputLogin = scanner.nextLine().trim();
                System.out.print("Введите пароль: ");
                inputPassword = scanner.nextLine().trim();
            }
            else if (userChoice.equals("2")) {
                System.out.print("Придумайте себе логин: ");
                inputLogin = scanner.nextLine().trim();
                System.out.print("Придумайте себе крутой пароль: ");
                inputPassword = scanner.nextLine().trim();
            }
            else if (userChoice.equals("3")) {
                System.out.println("\nЗавершение работы приложения...");
                System.exit(0);
            }
            else {
                System.out.println("Такого варианта нет, попробуй ещё раз!");
                continue;
            }

            if (inputLogin.isEmpty() || inputPassword.isEmpty()) {
                System.out.println("Логин и пароль не могут быть пустыми!");
                continue;
            }

            String commandName = userChoice.equals("1") ? "login" : "register";
            Request authRequest = new Request(inputLogin, inputPassword, commandName, null, null);

            try {

                Response response = udpClient.sendAndReceive(authRequest); //получаем ответ от сервера

                if (response != null && response.isSuccess()) {
                    this.userLogin = inputLogin;
                    this.userPassword = inputPassword;

                    System.out.println(response.getMessage());

                    break;
                }
                else {
                    System.out.println("Ошибка: " + (response != null ? response.getMessage() : "Пустой ответ от сервера!"));
                }

            } catch (RuntimeException ex) {
                System.err.println("Ошибка: " + ex.getMessage());
                //остальные ошибки обработаны в sendAndReceive
            }
        }

    }

    /**
     * Метод для получения логина пользователя
     * @return логин
     */
    public String getUserLogin() {
        return userLogin;
    }

    /**
     * Метод для получения пароля пользователя
     * @return пароль
     */
    public String getUserPassword() {
        return userPassword;
    }
}
