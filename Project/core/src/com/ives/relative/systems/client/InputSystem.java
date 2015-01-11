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
import com.ives.relative.entities.commands.*;
import com.ives.relative.entities.components.client.InputC;
import com.ives.relative.managers.CommandManager;
import com.ives.relative.systems.CommandSystem;

/**
 * Created by Ives on 5/12/2014.
 * This system is clientside. It checks for pressed keys and executes them on every entity with an InputC. The executed
 * command will be sent to the server.
 */
@Wire
public class InputSystem extends EntityProcessingSystem implements InputProcessor {
    protected ComponentMapper<InputC> mInputComponent;
    protected CommandManager commandManager;
    protected CommandSystem commandSystem;
    protected ClientNetworkSystem clientNetworkSystem;

    Camera camera;

    /**
     * Creates an entity system that uses the specified aspect as a matcher
     * against entities.
     */
    public InputSystem(Camera camera) {
        super(Aspect.getAspectForAll(InputC.class));
        this.camera = camera;
    }

    @Override
    protected void process(Entity e) {

    }

    @Override
    public boolean keyDown(int keycode) {
        for (Entity e : getActives()) {
            InputC inputC = mInputComponent.get(e);
            Command commandTemplate = inputC.commandKeys.get(keycode);
            Command command = commandManager.getCommand(commandTemplate);
            if (command.isSimulate())
                commandSystem.commandDown(command, e);

            if (!(command instanceof MoveRightCommand) && !(command instanceof MoveLeftCommand) && !(command instanceof JumpCommand))
                clientNetworkSystem.sendDownCommand(command);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (Entity e : getActives()) {
            InputC inputC = mInputComponent.get(e);
            Command commandTemplate = inputC.commandKeys.get(keycode);
            if (commandTemplate.isSimulate())
                commandSystem.commandUp(commandManager.getID(commandTemplate), e);
            if (!(commandTemplate instanceof MoveRightCommand) && !(commandTemplate instanceof MoveLeftCommand) && !(commandTemplate instanceof JumpCommand))
                clientNetworkSystem.sendUpCommand(commandTemplate);
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
        for (Entity e : getActives()) {
            ClickCommand c = getClickCommand(e);
            c.setWorldPosClicked(new Vector2(gamePos.x, gamePos.y));
            commandSystem.commandDown(c, e);
            clientNetworkSystem.sendClickCommand(c);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 gamePos = camera.unproject(new Vector3(screenX, screenY, 0));
        for (Entity e : getActives()) {
            ClickCommand c = getClickCommand(e);
            c.setWorldPosClicked(new Vector2(gamePos.x, gamePos.y));
            commandSystem.commandUp(commandManager.getID(c), e);
            clientNetworkSystem.sendUnClickCommand(c);
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

    public ClickCommand getClickCommand(Entity e) {
        return (ClickCommand) commandManager.getCommand(BreakTileCommand.class);
    }
}
