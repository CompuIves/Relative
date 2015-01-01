package com.ives.relative.entities.commands;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Ives on 1/1/2015.
 */
public abstract class ClickCommand extends Command {
    Vector2 worldPosClicked;

    /**
     * Creates a new command
     *
     * @param simulate Should it be executed on the client before getting a reaction from the server?
     */
    public ClickCommand(boolean simulate, Vector2 worldPosClicked) {
        super(simulate);
        this.worldPosClicked = worldPosClicked;
    }

    public void setWorldPosClicked(Vector2 worldPosClicked) {
        this.worldPosClicked = worldPosClicked;
    }

}
