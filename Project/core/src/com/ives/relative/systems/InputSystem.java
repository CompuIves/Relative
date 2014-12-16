package com.ives.relative.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.InputProcessor;
import com.ives.relative.entities.components.client.InputC;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ives on 5/12/2014.
 */
@Wire
public class InputSystem extends EntityProcessingSystem implements InputProcessor {
    protected ComponentMapper<InputC> mInputComponent;
    private List<Integer> keyDowns;
    private List<Integer> keyUpped;


    /**
     * Creates an entity system that uses the specified aspect as a matcher
     * against entities.
     */
    public InputSystem() {
        super(Aspect.getAspectForAll(InputC.class));
        keyDowns = new ArrayList<Integer>();
        keyUpped = new ArrayList<Integer>();
    }

    @Override
    protected void process(Entity e) {
        for (int key : keyDowns) {
            InputC inputC = mInputComponent.get(e);
            inputC.commandKeys.get(key).keyDown(e, true, world.getDelta());
        }
        for (int key : keyUpped) {
            InputC inputC = mInputComponent.get(e);
            inputC.commandKeys.get(key).keyUp(e, true);
        }
        keyUpped.clear();
    }

    @Override
    public boolean keyDown(int keycode) {
        keyDowns.add(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        keyUpped.add(keycode);
        keyDowns.remove(Integer.valueOf(keycode));
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
