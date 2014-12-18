package com.ives.relative.systems;

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
 */
@Wire
public class InputSystem extends EntityProcessingSystem implements InputProcessor {
    protected ComponentMapper<InputC> mInputComponent;
    protected CommandManager commandManager;

    private Array<Command> commandsActivated;
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
            command.whilePressed(e);
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        for (Entity e : getActives()) {
            System.out.println("ENR " + e.getId());
            InputC inputC = mInputComponent.get(e);
            Command commandTemplate = inputC.commandKeys.get(keycode);
            Command command = commandManager.getCommand(commandTemplate);
            command.keyDown(e, true);
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
                    c.keyUp(e, true);
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
