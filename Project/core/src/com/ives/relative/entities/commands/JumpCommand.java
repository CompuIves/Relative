package com.ives.relative.entities.commands;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.body.Physics;

/**
 * Created by Ives on 5/12/2014.
 */
public class JumpCommand extends Command {

    public JumpCommand() {
        super(true);
    }

    @Override
    public void executeDown(Entity e) {
    }

    /**
     * The jump method is in the execute part because the player maybe wants to jump multiple times with one tap.
     *
     * @param e The entity this will be executed on
     */
    @Override
    public void execute(Entity e) {
        Body body = e.getWorld().getMapper(Physics.class).get(e).body;
        body.applyLinearImpulse(new Vector2(0, 5), body.getPosition(), true);
    }

    @Override
    public void executeUp(Entity e, float delta) {

    }

    @Override
    public void undo() {

    }

    @Override
    public boolean canExecute(Entity e) {
        Physics p = e.getWorld().getMapper(Physics.class).get(e);
        Array<Contact> contacts = p.contacts;
        for (Contact contact : contacts) {
            if (contact.isTouching()) {
                //TODO make this a bit more accurate!
                return true;
            }
        }
        return false;
    }

    @Override
    public Command clone() {
        return new JumpCommand();
    }
}
