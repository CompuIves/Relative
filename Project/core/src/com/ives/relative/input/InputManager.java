package com.ives.relative.input;

import com.badlogic.gdx.Gdx;
import com.ives.relative.Relative;
import com.ives.relative.tiles.TileManager;

/**
 * Created by Ives on 12/1/2014.
 */
public class InputManager {
    Relative relative;

    public InputManager(Relative relative) {
        this.relative = relative;
    }

    public void updateInput() {
        processClicks();
    }

    public void processClicks() {
        if(Gdx.input.justTouched()) {
            System.out.println("PLACETILELOL");
            if(relative.gameScreen.getCurrentPlanet().getTile(TileManager.getX(Gdx.input.getX()), TileManager.getY(Gdx.input.getY())) != null)
                System.out.println("TILE FOUND");
            else
                relative.gameScreen.getCurrentPlanet().placeTile(Gdx.input.getX(), Gdx.input.getY());
        }
    }
}
