package PokemonBattle.moves;
import ru.ifmo.se.pokemon.*;

public final class Nuzzle extends PhysicalMove {
    public Nuzzle() {
        super(Type.ELECTRIC, 20, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon pokeOpp) {
        Effect.paralyze(pokeOpp);
    }

    @Override
    protected String describe() {
        return "обнюхивает жоско";
    }
}
