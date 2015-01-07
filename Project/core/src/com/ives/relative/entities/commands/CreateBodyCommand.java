package com.ives.relative.entities.commands;

import com.artemis.Entity;
import com.ives.relative.core.server.ServerManager;
import com.ives.relative.core.server.ServerNetwork;
import com.ives.relative.managers.planet.TileManager;
import com.ives.relative.network.packets.updates.ComponentPacket;
import com.ives.relative.systems.server.NetworkSendSystem;
import com.ives.relative.utils.ComponentUtils;

/**
 * Created by Ives on 14/12/2014.
 */
public class CreateBodyCommand extends Command {
    Entity body;

    public CreateBodyCommand() {
        super(false);
    }

    @Override
    public void executeDown(Entity entity) {
        body = entity.getWorld().getManager(TileManager.class).createTile("earth", 10, 15, 0, "dirt", true);
        ComponentPacket componentPacket = entity.getWorld().getSystem(NetworkSendSystem.class).generateFullComponentPacket(body);
        ((ServerNetwork) entity.getWorld().getManager(ServerManager.class).network).sendObjectTCPToAll(componentPacket);
    }

    @Override
    public void execute(Entity e, float delta) {

    }

    @Override
    public void executeUp(Entity e, float delta) {

    }

    @Override
    public void undo() {
        ComponentUtils.removeEntity(body);
    }

    @Override
    public void reset() {
        super.reset();
        this.body = null;
    }

    @Override
    public boolean canExecute(Entity e) {
        return true;
    }

    @Override
    public Command clone() {
        return new CreateBodyCommand();
    }
}
