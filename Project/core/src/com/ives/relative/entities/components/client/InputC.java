package com.ives.relative.entities.components.client;

import com.artemis.Component;
import com.badlogic.gdx.Input;
import com.ives.relative.entities.commands.*;


/**
 * Created by Ives on 5/12/2014.
 */
public class InputC extends Component {
    public transient CommandsMap<Integer, Command> commandKeys;

    public InputC() {
        commandKeys = new CommandsMap<Integer, Command>(new DoNothingCommand((byte) 0));
        commandKeys.put(Input.Keys.LEFT, new MoveLeftCommand((byte) 1));
        commandKeys.put(Input.Keys.RIGHT, new MoveRightCommand((byte) 2));
        commandKeys.put(Input.Keys.SPACE, new JumpCommand((byte) 3));
        commandKeys.put(Input.Keys.UP, new CreateBodyCommand((byte) 4));
    }
}
