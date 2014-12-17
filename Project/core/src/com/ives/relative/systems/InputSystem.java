package com.ives.relative.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.InputProcessor;
import com.ives.relative.entities.commands.Command;
import com.ives.relative.entities.components.client.InputC;
import com.ives.relative.managers.CommandSystem;
import com.ives.relative.managers.NetworkManager;

/**
 * Created by Ives on 5/12/2014.
 */
@Wire
public class InputSystem extends EntitySystem implements InputProcessor {
    protected ComponentMapper<InputC> mInputComponent;
    protected NetworkManager networkManager;
    /**
     * Creates an entity system that uses the specified aspect as a matcher
     * against entities.
     */
    public InputSystem() {
        super(Aspect.getAspectForAll(InputC.class));
    }

    @Override
    protected void end() {
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
    }

    @Override
    public boolean keyDown(int keycode) {
        for (Entity e : getActives()) {
            System.out.println("ENR " + e.getId());
            InputC inputC = mInputComponent.get(e);
            Command command = inputC.commandKeys.get(keycode).clone();
            CommandSystem commandSystem = e.getWorld().getSystem(CommandSystem.class);
            commandSystem.commandDown(command, networkManager.getNetworkID(e), true);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (Entity e : getActives()) {
            InputC inputC = mInputComponent.get(e);
            Command command = inputC.commandKeys.get(keycode);
            CommandSystem commandSystem = e.getWorld().getSystem(CommandSystem.class);
            byte commandID = commandSystem.getID(command);
            commandSystem.commandUp(commandID, networkManager.getNetworkID(e), true);
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
