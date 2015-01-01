package com.ives.relative.systems.client;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.commands.BreakTileCommand;
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

    Camera camera;
    /**
     * Creates an entity system that uses the specified aspect as a matcher
     * against entities.
     */
    public InputSystem(Camera camera) {
        super(Aspect.getAspectForAll(InputC.class));
        commandsActivated = new Array<Command>();
        this.camera = camera;
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
        Vector3 gamePos = camera.unproject(new Vector3(screenX, screenY, 0));
        Command c = new BreakTileCommand(new Vector2(gamePos.x, gamePos.y));
        if (!commandsActivated.contains(c, true)) {
            for (Entity e : getActives()) {
                if (c.canExecute(e))
                    c.keyDown(e);
                c.sendDown(e);
                commandsActivated.add(c);
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 gamePos = camera.unproject(new Vector3(screenX, screenY, 0));
        Command c = new BreakTileCommand(new Vector2(gamePos.x, gamePos.y));
        if (commandsActivated.contains(c, true)) {
            for (Entity e : getActives()) {
                if (c.canExecute(e))
                    c.keyUp(e);
                c.sendUp(e);
                commandsActivated.removeValue(c, true);
            }
        }
        return true;
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
