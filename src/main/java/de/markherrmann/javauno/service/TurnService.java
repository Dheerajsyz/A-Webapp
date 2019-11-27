package de.markherrmann.javauno.service;

import de.markherrmann.javauno.data.state.component.Game;
import de.markherrmann.javauno.data.state.component.GameLifecycle;
import de.markherrmann.javauno.data.state.component.Player;
import de.markherrmann.javauno.data.state.component.TurnState;
import de.markherrmann.javauno.exceptions.IllegalArgumentException;
import de.markherrmann.javauno.exceptions.IllegalStateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class TurnService {

    private final FinalizeTurnService finalizeTurnService;
    private final GameService gameService;
    private final PlayerService playerService;
    private final HousekeepingService housekeepingService;
    private final Logger logger = LoggerFactory.getLogger(TurnService.class);

    @Autowired
    public TurnService(FinalizeTurnService finalizeTurnService, GameService gameService,
                       PlayerService playerService, HousekeepingService housekeepingService){
        this.finalizeTurnService = finalizeTurnService;
        this.gameService = gameService;
        this.playerService = playerService;
        this.housekeepingService = housekeepingService;
    }

    void finalizeTurn(Game game){
        Runnable runnable = () -> finalize(game);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    boolean isPlayersTurn(Game game, Player player){
        int currentIndex = game.getCurrentPlayerIndex();
        Player current = game.getPlayers().get(currentIndex);
        boolean playersTurn = current.equals(player);
        if(!playersTurn){
            logger.warn(String.format(
                    "It's not the players turn. Game: %s; playersIndex: %d; currentPlayerIndex: %d",
                    game.getUuid(),
                    game.getPlayers().indexOf(player),
                    game.getCurrentPlayerIndex()));
        }
        return playersTurn;
    }

    boolean isGameInLifecycle(Game game, GameLifecycle gameLifecycle){
        boolean inLifecycle = game.getGameLifecycle().equals(gameLifecycle);
        if(!inLifecycle){
            logger.error("game is in wrong lifecycle. Game: " + game.getUuid());
        }
        return inLifecycle;
    }

    void failIfInvalidTurnState(Game game, TurnState... validTurnStates) throws IllegalStateException {
        for(TurnState state : validTurnStates){
            if(game.getTurnState().equals(state)){
                return;
            }
        }
        logger.error(String.format("turn is in wrong state for this action. Game: %s; validStates: %s; state: %s",
                game.getUuid(),
                Arrays.asList(validTurnStates),
                game.getTurnState()));
        throw new IllegalStateException("turn is in wrong state for this action.");
    }

    Game getGame(String gameUuid) throws IllegalArgumentException {
        return gameService.getGame(gameUuid);
    }

    Player getPlayer(String playerUuid, Game game) throws IllegalArgumentException {
        return playerService.getPlayer(playerUuid, game);
    }

    void updateLastAction(Game game){
        housekeepingService.updateLastAction(game);
    }

    private void finalize(Game game){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex){
            logger.error("ERROR! Final Countdown Interrupted. while loop with bad performance will be used.", ex);
            waitWithWhileLoop();
        }
        synchronized (game){
            finalizeTurnService.finalizeTurn(game);
        }
    }

    private void waitWithWhileLoop(){
        long start = System.currentTimeMillis();
        long diff;
        do {
            diff = System.currentTimeMillis() - start;
        } while(diff < 3000);
    }
}
