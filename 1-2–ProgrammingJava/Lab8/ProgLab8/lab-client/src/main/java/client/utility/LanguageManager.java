package client.utility;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {

    //единственный экземпляр этого класса
    private static LanguageManager languageManager;

    private Locale currentLocale;
    private ResourceBundle bundle;

    private static final String BUNDLE_PATH = "i18n/messages";

    private LanguageManager() {
        setLocale(new Locale("ru")); //по умолчанию русский
    }

    /**
     * Конструктор для получения единственного экземпляра
     * @return если уже создан, то возвращаем как раз его
     */
    public static LanguageManager getInstance() {
        if (languageManager == null) {
            languageManager = new LanguageManager();
        }
        return languageManager;
    }

    /**
     * Метод, позволяющий установить язык
     * @param locale объект класса Locale с установленным регионом
     */
    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        this.bundle = ResourceBundle.getBundle(BUNDLE_PATH, locale);
    }

    /**
     * Метод, позволяющий получить перевод по строчке-ключу
     * @param key ключ для получения перевода
     * @return перевод
     */
    public String get(String key) {
        return bundle.getString(key);
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

}
