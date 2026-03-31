package PokemonBattle.moves;
import ru.ifmo.se.pokemon.*;

public final class Thunder extends SpecialMove {
    public Thunder() {
        super(Type.ELECTRIC, 110 ,70);
    }

    @Override
    protected void applyOppEffects(Pokemon pokeOpp) {
        Effect effect = new Effect().chance(0.3);
        if (effect.success()) {
            Effect.paralyze(pokeOpp);
        }
    }

    @Override
    protected String describe() {
        return "наводит грома";
    }

}
