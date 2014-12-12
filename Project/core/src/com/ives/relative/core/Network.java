package com.ives.relative.core;

import com.artemis.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import com.ives.relative.core.network.KryoComparator;
import com.ives.relative.core.network.serializers.box2d.FixtureDefSerializer;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.handshake.modules.notice.CompleteFileNotice;
import com.ives.relative.core.packets.handshake.modules.notice.FinishFileTransferNotice;
import com.ives.relative.core.packets.handshake.modules.notice.StartFileNotice;
import com.ives.relative.core.packets.networkentity.NetworkEntity;
import com.ives.relative.managers.SolidTile;
import com.ives.relative.managers.assets.modules.Module;
import org.reflections.Reflections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public abstract void sendObjectTCP(int connectionID, Packet o);

    public abstract void sendObjectUDP(int connectionID, Packet o);

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

        Reflections packetReflections = new Reflections("com.ives.relative.core.packets");
        Set<Class<? extends Packet>> packets = packetReflections.getSubTypesOf(Packet.class);

        kryoList = new ArrayList<Class<?>>();
        kryoList.addAll(components);
        kryoList.addAll(packets);

        kryoList.sort(new KryoComparator());
    }

    /**
     * The central place for server and client to register the classes used by Kryo.
     */
    public void registerKryoClasses() {
        kryo.register(SolidTile.class);
        kryo.register(byte[].class);
        kryo.register(NetworkEntity.class);
        kryo.register(ArrayList.class);
        kryo.register(Module.class);
        kryo.register(Vector2.class);
        kryo.register(FixtureDef.class);
        kryo.register(BodyDef.class);
        kryo.register(Array.class);
        kryo.register(Object[].class);

        kryo.register(CompleteFileNotice.class);
        kryo.register(StartFileNotice.class);
        kryo.register(FinishFileTransferNotice.class);
        for (Class<?> c : kryoList) {
            kryo.register(c);
        }
    }
}
