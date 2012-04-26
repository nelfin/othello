package iago;

import iago.Player.PlayerType;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class StrategyLookup {
    private static final Map<String, Class<? extends Player>> strategyTable =
        new HashMap<String, Class<? extends Player>>();
    static {
        strategyTable.put("simple", SimplePlayer.class);
        strategyTable.put("greedy", GreedyPlayer.class);
        strategyTable.put("alpha-beta", AlphaBetaPlayer.class);
        strategyTable.put("negamax", NegamaxPlayer.class);
    }
    private static final Class<?>[] constructorSignature = { PlayerType.class };
    
    public static Player playerFromStrategy(String strategy, PlayerType colour) throws IllegalArgumentException {
        // Crazy voodoo
        Player retval = null;
        try {
            retval =
                strategyTable.get(strategy).getConstructor(constructorSignature).newInstance(colour);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retval;
    }
}
