package mainObjects;

//источник света для лампы и камина
public abstract class LightSource {
    public boolean isOn;
    public int intensity;

    public abstract void turnOn() throws NoFuelException;
    public abstract void turnOff();

    //не абстрактный!!!
    public LightLevel getLightLevel() {
        if (!isOn) {
            return LightLevel.OBSCURITY;
        }
        else if (intensity > 0 && intensity <= 40) {
            return LightLevel.DARK;
        }
        else if (intensity > 40 && intensity <= 60) {
            return LightLevel.BRIGHT;
        }
        return LightLevel.VERY_BRIGHT;
    }
}
