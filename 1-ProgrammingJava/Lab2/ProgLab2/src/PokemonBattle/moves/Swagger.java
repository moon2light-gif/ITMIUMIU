package PokemonBattle.moves;
import ru.ifmo.se.pokemon.*;

public final class Swagger extends StatusMove {

    public Swagger() {
        super(Type.NORMAL, 0, 85);
    }

    @Override
    protected void applyOppEffects(Pokemon pokeOpp) {
        pokeOpp.confuse();
        pokeOpp.setMod(Stat.ATTACK, +2);
    }

    @Override
    protected String describe() {
        return "";
    }
}
