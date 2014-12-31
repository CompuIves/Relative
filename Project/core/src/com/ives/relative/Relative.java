package com.ives.relative;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.ives.relative.core.client.ClientManager;
import com.ives.relative.core.server.ServerManager;

public class Relative extends Game {
    public static String VERSION = "1.0";
    public static Relative relative;

    /**
     * Sets the mode for the game, 3 modes available:
     * <p>1: CLIENT </p>
     * <p>2: SERVER </p>
     * <p>ELSE: BOTH </p>
     */
    public static Mode MODE;
    private ServerManager serverManager;
    private ClientManager clientManager;

    /**
     * This gets executed when the game starts. Because this game has an internal server there is both a
     * {@link com.ives.relative.core.server.ServerManager} and a {@link com.ives.relative.core.client.ClientManager} running
     * most of the time. They both extend {@link com.ives.relative.core.GameManager}. The GameManager keeps starts all the
     * common systems and managers of both client and server, the ServerManager starts all the managers and systems of the server
     * and the ClientManager starts all the systems and managers specific to the client.
     */
    @Override
    public void create() {
        relative = this;
        Gdx.graphics.setDisplayMode(1280, 720, false);

        //Log.DEBUG();
        //Creates two instances, an internal server and a client which connects to the server.
        //Add the server first, otherwise the client starts searching for a server while the server hasn't even started yet.
        switch (MODE) {
            case Both:
                serverManager = new ServerManager();
                clientManager = new ClientManager();
                setScreen(clientManager);
                break;
            case Client:
                clientManager = new ClientManager();
                setScreen(clientManager);
                break;
            case Server:
                serverManager = new ServerManager();
                break;
        }

    }

    /**
     * This gets called every frame, it just passes on the method to the ServerManager and the ClientManager
     */
    @Override
    public void render() {
        //Here was a line of code which made me debug for 2 hours. RIP.
        if (serverManager != null)
            serverManager.render(Gdx.graphics.getDeltaTime());

        if (clientManager != null)
            clientManager.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    public enum Mode {
        Client, Server, Both
    }
}
