package PokemonBattle.moves;
import ru.ifmo.se.pokemon.*;

public final class StoneEdge extends PhysicalMove {
    public StoneEdge() {
        super(Type.ROCK, 100, 80);
    }

    @Override
    protected double calcCriticalHit(Pokemon att, Pokemon def) {
        if (0.125 > Math.random()) {
            System.out.println("Критический удар!");
            return (double)2.0F;
        } else {
            return (double)1.0F;
        }
    }

    @Override
    protected String describe() {
        return "бьёт краем камня";
    }

}
