package com.ives.relative.entities.commands;


import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.components.living.MovementSpeed;


/**
 * Created by Ives on 5/12/2014.
 */
public class MoveRightCommand extends Command {
    /**
     * Deltas for reconciliation
     */
    float x, y;
    float vx, vy;

    float dx, dy;
    float dvx, dvy;

    public MoveRightCommand() {
        super(true);
    }

    @Override
    public void executeDown(Entity e) {
    }

    @Override
    public void execute(Entity e) {
        try {
            float mvSpeed = e.getWorld().getMapper(MovementSpeed.class).get(e).movementSpeed;
            float vx = mvSpeed;
            moveEntity(e, vx);
        } catch (Exception sae) {
            System.out.println("GOT AN ARRAYBOUND ERROR VERY STRANGE");
            System.out.println(e.getId());
        }

    }

    @Override
    public void executeUp(Entity e, float delta) {

    }

    private void moveEntity(Entity e, float vx) {
        Body body = e.getWorld().getMapper(Physics.class).get(e).body;
        float oldvx = body.getLinearVelocity().x;
        float deltavx = vx - oldvx;
        float impulse = body.getMass() * deltavx;
        body.applyLinearImpulse(new Vector2(impulse, 0), body.getWorldCenter(), true);
    }

    @Override
    public void startRecord(Entity e) {
        Position position = e.getWorld().getMapper(Position.class).get(e);
        Velocity velocity = e.getWorld().getMapper(Velocity.class).get(e);
        x = position.x;
        y = position.y;
        vx = velocity.vx;
        vy = velocity.vy;
    }

    @Override
    void whileRecord(Entity e) {
        Position position = e.getWorld().getMapper(Position.class).get(e);
        Velocity velocity = e.getWorld().getMapper(Velocity.class).get(e);

        dx = position.x - x;
        dy = position.y - y;
        dvx = velocity.vx - vx;
        dvy = velocity.vy - vy;
    }

    @Override
    public void applyReconciliation(Entity e) {
        Position position = e.getWorld().getMapper(Position.class).get(e);
        Velocity velocity = e.getWorld().getMapper(Velocity.class).get(e);
        Body body = e.getWorld().getMapper(Physics.class).get(e).body;

        position.x += dx;
        position.y += dy;
        velocity.vx += vx;
        velocity.vy += vy;
        body.setTransform(position.x, position.y, body.getAngle());
        body.setLinearVelocity(velocity.vx, velocity.vy);
    }

    @Override
    public void reset() {
        super.reset();
        x = 0;
        y = 0;
        dx = 0;
        dy = 0;
        dvx = 0;
        dvy = 0;
    }


    @Override
    public Command clone() {
        return new MoveRightCommand();
    }
}