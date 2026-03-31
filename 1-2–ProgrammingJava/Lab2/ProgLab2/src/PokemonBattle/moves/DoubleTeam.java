package PokemonBattle.moves;
import ru.ifmo.se.pokemon.*;

public final class DoubleTeam extends StatusMove {

    public DoubleTeam() {
        super(Type.NORMAL, 0, 0);
    }

    @Override
    protected void applySelfEffects(Pokemon poke) {
        Effect effect = new Effect().turns(1).stat(Stat.EVASION, +1);
        poke.addEffect(effect);
    }

    @Override
    protected String describe() {
        return "такой неуязвимый блин класс";
    }

}
