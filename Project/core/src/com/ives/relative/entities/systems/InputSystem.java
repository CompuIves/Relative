package com.ives.relative.entities.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.InputProcessor;
import com.ives.relative.entities.commands.Command;
import com.ives.relative.entities.components.BodyComponent;
import com.ives.relative.entities.components.InputComponent;
import com.ives.relative.entities.components.VisualComponent;
import com.ives.relative.entities.components.mappers.Mappers;

import java.util.ArrayList;

/**
 * Created by Ives on 5/12/2014.
 */
public class InputSystem extends EntitySystem implements InputProcessor {
    private ImmutableArray<Entity> entities;

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(InputComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public boolean keyDown(int keycode) {
        for(Entity entity : entities) {
            InputComponent inputComponent = Mappers.input.get(entity);
            inputComponent.commandKeys.get(keycode).execute(entity);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
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
