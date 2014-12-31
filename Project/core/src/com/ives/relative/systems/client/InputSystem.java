package com.ives.relative.systems.client;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.commands.Command;
import com.ives.relative.entities.components.client.InputC;
import com.ives.relative.managers.CommandManager;

/**
 * Created by Ives on 5/12/2014.
 * This system is clientside. It checks for pressed keys and executes them on every entity with an InputC. The executed
 * command will be sent to the server.
 */
@Wire
public class InputSystem extends EntityProcessingSystem implements InputProcessor {
    public Array<Command> commandsActivated;
    protected ComponentMapper<InputC> mInputComponent;
    protected CommandManager commandManager;
    /**
     * Creates an entity system that uses the specified aspect as a matcher
     * against entities.
     */
    public InputSystem() {
        super(Aspect.getAspectForAll(InputC.class));
        commandsActivated = new Array<Command>();
    }

    @Override
    protected void process(Entity e) {
        for (Command command : commandsActivated) {
            if (command.canExecute(e))
                command.whilePressed(e);
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        for (Entity e : getActives()) {
            InputC inputC = mInputComponent.get(e);
            Command commandTemplate = inputC.commandKeys.get(keycode);
            Command command = commandManager.getCommand(commandTemplate);
            if (commandTemplate.canExecute(e)) {
                //Send the keydown, maybe the server accepts it because the client is outdated.
                command.keyDown(e);
            }
            command.sendDown(e);
            //Activate the command, the check will still be done while it's activated. But the state may change while
            //executing the command and in that case you want it activated.
            commandsActivated.add(command);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (Entity e : getActives()) {
            InputC inputC = mInputComponent.get(e);
            Command commandTemplate = inputC.commandKeys.get(keycode);
            for (Command c : commandsActivated) {
                if (c.equals(commandTemplate)) {
                    if (c.canExecute(e)) {
                        //Only execute up if it is allowed to.
                        c.keyUp(e);
                    }

                    c.sendUp(e);
                    commandManager.freeCommand(c);
                }
            }
            commandsActivated.removeValue(commandTemplate, false);
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
