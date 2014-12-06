package com.ives.relative.core.assets.json;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Created by Ives on 6/12/2014.
 */
public class JSONIndexer {

    public FileHandle[] indexFiles() {
        if(Gdx.files.local("modules").exists()) {
            return Gdx.files.local("modules").list();
        } else {
            if(!Gdx.files.local("modules").file().mkdir()) {

            }
        }
    }
}
