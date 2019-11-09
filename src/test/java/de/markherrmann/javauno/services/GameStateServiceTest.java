package de.markherrmann.javauno.services;

import de.markherrmann.javauno.data.state.UnoState;
import de.markherrmann.javauno.data.state.components.Game;
import de.markherrmann.javauno.data.state.components.GameLifecycle;
import de.markherrmann.javauno.data.state.responses.GameAddPlayersState;
import de.markherrmann.javauno.data.state.responses.GameBetweenRoundsState;
import de.markherrmann.javauno.data.state.responses.GameRunningState;
import de.markherrmann.javauno.data.state.responses.GameState;
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
    private GameStateService gameStateService;

    private Game game;

    @Before
    public void setup(){
        String uuid = gameService.createGame();
        game = UnoState.getGames().get(uuid);
    }

    @Test
    public void ShouldGetAddPlayersState(){
        GameState state = gameStateService.get(game.getUuid(), "");

        assertThat(state).isInstanceOf(GameAddPlayersState.class);
        assertThat(state.getPlayers()).isEmpty();
    }

    @Test
    public void ShouldGetRunningState(){
        prepareGame();
        gameService.startGame(game.getUuid());

        GameState state = gameStateService.get(game.getUuid(), game.getPlayerList().get(0).getUuid());

        assertThat(state).isInstanceOf(GameRunningState.class);
        assertThat(((GameRunningState)state).getGame()).isEqualTo(game);
        assertThat(state.getPlayers()).isEqualTo(game.getPlayerList());
        assertThat(((GameRunningState)state).getOwnCards()).isEqualTo(game.getPlayerList().get(0).getCards());
    }

    @Test
    public void ShouldGetBetweenRoundsState(){
        prepareGame();
        gameService.startGame(game.getUuid());
        game.setGameLifecycle(GameLifecycle.BETWEEN_ROUNDS);

        GameState state = gameStateService.get(game.getUuid(), game.getPlayerList().get(0).getUuid());

        assertThat(state).isInstanceOf(GameBetweenRoundsState.class);
        assertThat(((GameBetweenRoundsState)state).getGame()).isEqualTo(game);
        assertThat(state.getPlayers()).isEqualTo(game.getPlayerList());
        assertThat(((GameBetweenRoundsState)state).getOwnCards()).isEqualTo(game.getPlayerList().get(0).getCards());
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

    private void prepareGame(){
        gameService.addPlayer(game.getUuid(), "Max", false);
        gameService.addPlayer(game.getUuid(), "Maria", false);
        gameService.addPlayer(game.getUuid(), "", true);
    }
}