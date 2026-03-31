package mainObjects;

public class NoFuelException extends Exception {

    @Override
    public String getMessage() {
        return "Керосин в лампе закончился - она не загорится!!!";
    }

}
