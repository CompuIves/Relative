package com.ives.relative.entities.components.client;

import com.artemis.Component;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Input;
import com.ives.relative.entities.commands.*;


/**
 * Created by Ives on 5/12/2014.
 * Saves which command will be executed at specified key. This will be read from the InputSystem
 */
@Wire
public class InputC extends Component {
    public transient CommandsMap<Integer, Command> commandKeys;

    public InputC() {
        commandKeys = new CommandsMap<Integer, Command>(new DoNothingCommand());
        commandKeys.put(Input.Keys.A, new MoveLeftCommand());
        commandKeys.put(Input.Keys.D, new MoveRightCommand());
        commandKeys.put(Input.Keys.SPACE, new JumpCommand());
        commandKeys.put(Input.Keys.W, new CreateBodyCommand());
    }
}
