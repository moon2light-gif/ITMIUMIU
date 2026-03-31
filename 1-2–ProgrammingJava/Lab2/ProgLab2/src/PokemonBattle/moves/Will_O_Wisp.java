package PokemonBattle.moves;
import ru.ifmo.se.pokemon.*;

public final class Will_O_Wisp extends StatusMove {

    public Will_O_Wisp() {
        super(Type.FIRE, 0, 85);
    }

    @Override
    protected void applyOppEffects(Pokemon pokeOpp) {
        Effect effect = new Effect().chance(0.85);
        if (effect.success()) {
            Effect.burn(pokeOpp);
        }
    }

    @Override
    protected String describe() {
        return "воспламеняет всё вокруг";
    }
}
