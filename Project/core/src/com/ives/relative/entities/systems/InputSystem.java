package com.ives.relative.entities.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.InputProcessor;
import com.ives.relative.entities.components.InputComponent;
import com.ives.relative.entities.components.mappers.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ives on 5/12/2014.
 */
public class InputSystem extends EntitySystem implements InputProcessor {
    private List<Integer> keysPressed;

    Family family;

    private ImmutableArray<Entity> entities;

    public InputSystem(Family family) {
        this.family = family;
        keysPressed = new ArrayList<Integer>();
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(family);
    }

    @Override
    public void update(float deltaTime) {
        for(Entity entity : entities) {
            for (int key : keysPressed) {
                InputComponent inputComponent = Mappers.input.get(entity);
                inputComponent.commandKeys.get(key).execute(entity);
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        keysPressed.add(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        keysPressed.remove(Integer.valueOf(keycode));
        for(Entity entity : entities) {
            InputComponent inputComponent = Mappers.input.get(entity);
            inputComponent.commandKeys.get(keycode).antiExecute(entity);
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
