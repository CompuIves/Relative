package com.ives.relative.entities.components;


import com.artemis.Component;

/**
 * Created by Ives on 4/12/2014.
 */
public class Name extends Component {
    public String internalName;
    public String publicName;

    public Name(String internalName, String publicName) {
        this.internalName = internalName;
        this.publicName = publicName;
    }

    public Name() {
    }
}
