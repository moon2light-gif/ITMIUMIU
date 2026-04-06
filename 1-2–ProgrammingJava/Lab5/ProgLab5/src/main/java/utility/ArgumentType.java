package utility;

//можно расширять, если добавятся новые
public enum ArgumentType {
    NONE, //у команд без аргументов (show, clear, info)
    STRING, //строковый аргумент (fileName для execute_script)
    LONG, //числовой аргумент (id)
    INT // для annualTurnover
}
