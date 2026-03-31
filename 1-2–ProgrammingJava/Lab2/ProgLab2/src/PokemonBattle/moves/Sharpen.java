package PokemonBattle.moves;
import ru.ifmo.se.pokemon.*;

public final class Sharpen extends StatusMove {

    public Sharpen() {
        super(Type.NORMAL, 0, 0);
    }

    @Override
    protected void applySelfEffects(Pokemon poke) {
        Effect effect = new Effect().stat(Stat.ATTACK, +1);
        poke.addEffect(effect);
    }

    @Override
    protected String describe() {
        return "наточил хвостик";
    }
}

