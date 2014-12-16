package com.ives.relative.entities.components.client;

import com.artemis.Component;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Input;
import com.ives.relative.entities.commands.*;


/**
 * Created by Ives on 5/12/2014.
 */
@Wire
public class InputC extends Component {
    public transient CommandsMap<Integer, Command> commandKeys;

    public InputC() {
        commandKeys = new CommandsMap<Integer, Command>(new DoNothingCommand());
        commandKeys.put(Input.Keys.LEFT, new MoveLeftCommand());
        commandKeys.put(Input.Keys.RIGHT, new MoveRightCommand());
        commandKeys.put(Input.Keys.SPACE, new JumpCommand());
        commandKeys.put(Input.Keys.UP, new CreateBodyCommand());
    }
}
