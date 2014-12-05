package com.ives.relative.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Input;
import com.ives.relative.entities.commands.Command;
import com.ives.relative.entities.commands.MoveLeftCommand;
import com.ives.relative.entities.commands.MoveRightCommand;

import java.util.HashMap;

/**
 * Created by Ives on 5/12/2014.
 */
public class InputComponent extends Component {
    public HashMap<Integer, Command> commandKeys;

    public InputComponent() {
        commandKeys = new HashMap<Integer, Command>();

        commandKeys.put(Input.Keys.LEFT, new MoveLeftCommand());
        commandKeys.put(Input.Keys.RIGHT, new MoveRightCommand());
    }
}
