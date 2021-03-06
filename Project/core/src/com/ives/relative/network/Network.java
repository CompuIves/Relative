package com.ives.relative.network;

import com.artemis.Component;
import com.artemis.PooledComponent;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import com.ives.relative.entities.commands.Command;
import com.ives.relative.managers.AuthorityManager;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.assets.modules.Module;
import com.ives.relative.managers.event.StateManager;
import com.ives.relative.network.packets.BasePacket;
import com.ives.relative.network.packets.handshake.modules.notice.CompleteFileNotice;
import com.ives.relative.network.packets.handshake.modules.notice.FinishFileTransferNotice;
import com.ives.relative.network.packets.handshake.modules.notice.StartFileNotice;
import com.ives.relative.network.serializers.box2d.FixtureDefSerializer;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.utils.KryoComparator;
import org.reflections.Reflections;

import java.io.IOException;
import java.util.*;

/**
 * Created by Ives on 4/12/2014.
 */
public abstract class Network extends Listener {
    public EndPoint endPoint;
    public Kryo kryo;

    private List<Class<?>> kryoList;

    public Network(EndPoint endPoint) throws IOException {
        this.endPoint = endPoint;
        kryo = endPoint.getKryo();
        kryo.addDefaultSerializer(FixtureDef.class, new FixtureDefSerializer());

        addKryoClasses();
        registerKryoClasses();
    }

    public abstract void sendObjectTCP(int connectionID, BasePacket o);

    public abstract void sendObjectUDP(int connectionID, BasePacket o);

    @Override
    public abstract void connected(Connection connection);

    public abstract void closeConnection(int connection, String message);

    public void closeCurrentConnection(String message) {
        Gdx.app.log("Connection", message);
        endPoint.close();
    }


    /**
     * This class adds al the specified kryo classes from the packages, after this it sorts them and adds them to the
     * arraylist. Another method will register every class to Kryo.
     * I sort every class because the classes may be added in a different order between instances, this is caused by
     * reflection. If the classes are out of order Kryo will not be able to understand the packets sent/received.
     */
    public void addKryoClasses() {
        //Get all the components and add it to the kryo
        Reflections componentReflections = new Reflections("com.ives.relative.entities.components");
        Set<Class<? extends Component>> components = componentReflections.getSubTypesOf(Component.class);

        Reflections pooledComponentReflections = new Reflections("com.ives.relative.entities.components");
        Set<Class<? extends PooledComponent>> pooledComponents = pooledComponentReflections.getSubTypesOf(PooledComponent.class);

        Reflections packetReflections = new Reflections("com.ives.relative.network.packets");
        Set<Class<? extends BasePacket>> packets = packetReflections.getSubTypesOf(BasePacket.class);


        kryoList = new ArrayList<Class<?>>();
        kryoList.addAll(components);
        kryoList.addAll(packets);
        kryoList.addAll(pooledComponents);

        kryoList.sort(new KryoComparator());
    }

    /**
     * The central place for server and client to register the classes used by Kryo.
     */
    public void registerKryoClasses() {
        kryo.register(byte[].class);
        kryo.register(ArrayList.class);
        kryo.register(Module.class);
        kryo.register(Vector2.class);
        kryo.register(FixtureDef.class);
        kryo.register(BodyDef.class);
        kryo.register(Array.class);
        kryo.register(Object[].class);
        kryo.register(float[].class);
        kryo.register(Fixture.class);
        kryo.register(Body.class);
        kryo.register(NetworkManager.Type.class);
        kryo.register(Command[].class);
        kryo.register(Command.class);
        kryo.register(Component[].class);
        kryo.register(StateManager.EntityState.class);
        kryo.register(int[].class);
        kryo.register(AuthorityManager.AuthorityType.class);
        kryo.register(Chunk.class);
        kryo.register(UUID.class);
        kryo.register(BodyDef.BodyType.class);
        kryo.register(HashMap.class, new MapSerializer());

        kryo.register(CompleteFileNotice.class);
        kryo.register(StartFileNotice.class);
        kryo.register(FinishFileTransferNotice.class);
        for (Class<?> c : kryoList) {
            kryo.register(c);
        }
    }
}
