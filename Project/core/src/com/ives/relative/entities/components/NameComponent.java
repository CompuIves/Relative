package com.ives.relative.entities.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by Ives on 4/12/2014.
 */
public class NameComponent extends Component {
    public String internalName;
    public String publicName;

    public NameComponent(String internalName, String publicName) {
        this.internalName = internalName;
        this.publicName = publicName;
    }
}
