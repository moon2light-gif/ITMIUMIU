package PokemonBattle.moves;
import ru.ifmo.se.pokemon.*;

public final class Thunderbolt extends SpecialMove {

    public Thunderbolt() {
        super(Type.ELECTRIC, 90, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon pokeOpp) {
        Effect effect = new Effect().chance(0.1);
        if (effect.success()) {
            Effect.paralyze(pokeOpp);
        }
    }

    @Override
    protected String describe() {
        return "фигачит молнией БЭУМС";
    }

}
