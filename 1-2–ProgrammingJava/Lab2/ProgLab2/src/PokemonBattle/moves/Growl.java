package PokemonBattle.moves;
import ru.ifmo.se.pokemon.*;

public final class Growl extends StatusMove {
    public Growl() {
        super(Type.NORMAL, 0, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon pokeOpp) {
        Effect effect = new Effect().stat(Stat.ATTACK, -1);
        pokeOpp.addEffect(effect);
    }

    @Override
    protected String describe() {
        return "очень громко рычит";
    }
}
