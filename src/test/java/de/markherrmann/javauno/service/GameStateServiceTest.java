package de.markherrmann.javauno.service;

import de.markherrmann.javauno.data.state.UnoState;
import de.markherrmann.javauno.data.state.component.Game;
import de.markherrmann.javauno.data.state.component.GameLifecycle;
import de.markherrmann.javauno.controller.response.GameState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GameStateServiceTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameStateService gameStateService;

    private Game game;

    @Before
    public void setup(){
        String uuid = gameService.createGame();
        game = UnoState.getGame(uuid);
    }

    @Test
    public void ShouldGetSetPlayersState(){
        prepareGame();

        GameState state = gameStateService.get(game.getUuid(), game.getPlayers().get(0).getUuid());

        assertThat(state.getGame().getGameLifecycle()).isEqualTo(GameLifecycle.SET_PLAYERS);
    }

    @Test
    public void ShouldGetRunningStateOwnTurn(){
        int playerIndex = 0;
        prepareGame();
        gameService.startGame(game.getUuid());

        GameState state = gameStateService.get(game.getUuid(), game.getPlayers().get(playerIndex).getUuid());

        assertState(state, playerIndex, true);
    }

    @Test
    public void ShouldGetRunningStateOthersTurn(){
        int playerIndex = 1;
        prepareGame();
        gameService.startGame(game.getUuid());

        GameState state = gameStateService.get(game.getUuid(), game.getPlayers().get(playerIndex).getUuid());

        assertState(state, playerIndex, false);
    }

    @Test
    public void ShouldFailCausedByInvalidGameUuid(){
        Exception exception = null;

        try {
            gameStateService.get("invalid", "");
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void ShouldFailCausedByInvalidPlayerUuid(){
        game.setGameLifecycle(GameLifecycle.RUNNING);
        Exception exception = null;

        try {
            gameStateService.get(game.getUuid(), "invalid");
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isInstanceOf(IllegalArgumentException.class);
    }

    private void assertState(GameState state, int playerIndex, boolean myTurn){
        assertThat(state.getGame().getGameLifecycle()).isEqualTo(GameLifecycle.RUNNING);
        assertThat(state.getGame()).isEqualTo(game);
        assertThat(state.getPlayers()).isEqualTo(game.getPlayers());
        assertThat(state.getOwnCards()).isEqualTo(game.getPlayers().get(playerIndex).getCards());
        assertThat(state.isPlayersTurn()).isEqualTo(myTurn);
    }

    private void prepareGame(){
        playerService.addPlayer(game.getUuid(), "Max", false);
        playerService.addPlayer(game.getUuid(), "Maria", false);
        playerService.addPlayer(game.getUuid(), "", true);
    }
}