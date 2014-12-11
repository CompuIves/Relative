package com.ives.relative.entities.components;

import com.artemis.Component;
import com.badlogic.gdx.Input;
import com.ives.relative.entities.commands.*;


/**
 * Created by Ives on 5/12/2014.
 */
public class InputComponent extends Component {
    public transient CommandsMap<Integer, Command> commandKeys;

    public InputComponent() {
        commandKeys = new CommandsMap<Integer, Command>(new Command());
        commandKeys.put(Input.Keys.LEFT, new MoveLeftCommand());
        commandKeys.put(Input.Keys.RIGHT, new MoveRightCommand());
        commandKeys.put(Input.Keys.SPACE, new JumpCommand());
    }
}
