package PokemonBattle.moves;
import ru.ifmo.se.pokemon.*;

public final class Facade extends PhysicalMove {
    public Facade() {
        super(Type.NORMAL, 70, 100);
    }

    private boolean flag;

    @Override
    public void applyOppDamage(Pokemon pokeOpp, double damage) {
        Status defStatus = pokeOpp.getCondition();
        if (defStatus.equals(Status.POISON) || defStatus.equals(Status.BURN) || defStatus.equals(Status.PARALYZE)) {
            pokeOpp.setMod(Stat.HP, 2 * (int) damage);
            flag = true;
        } else {
            pokeOpp.setMod(Stat.HP, (int) damage);
            flag = false;
        }
    }

    @Override
    protected String describe() {
        if (flag) {
            return "жоско бьёт";
        }
        else return "бьёт";
    }

}
