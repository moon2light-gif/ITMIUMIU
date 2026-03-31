package PokemonBattle.moves;
import ru.ifmo.se.pokemon.*;

public final class Rest extends StatusMove {
    public Rest() {
        super(Type.PSYCHIC, 0, 0);
    }

    @Override
    protected void applySelfEffects(Pokemon poke) {
        Effect effect = new Effect().turns(2).condition(Status.SLEEP);
        poke.restore();
        poke.addEffect(effect);
    }

    @Override
    protected String describe() {
        return "наелся и спит";
    }
}
