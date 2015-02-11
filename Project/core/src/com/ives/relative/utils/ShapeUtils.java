package com.ives.relative.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Ives on 11/2/2015.
 */
public class ShapeUtils {
    public static Vector2[] getCubeVertices(float width, float height) {
        float halfWidth = width / 2;
        float halfHeight = height / 2;

        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-halfWidth, halfHeight);
        vertices[1] = new Vector2(-halfWidth, -halfHeight);
        vertices[2] = new Vector2(halfWidth, -halfHeight);
        vertices[3] = new Vector2(halfWidth, halfHeight);

        return vertices;
    }

    public static Vector2[] getTriangleVertices(float width, float height) {
        float halfWidth = width / 2;
        float halfHeight = height / 2;

        Vector2[] vertices = new Vector2[3];
        vertices[0] = new Vector2(-halfWidth, halfHeight);
        vertices[1] = new Vector2(-halfWidth, -halfHeight);
        vertices[2] = new Vector2(halfWidth, -halfHeight);

        return vertices;
    }

    public static Vector2[] getContourVertices(int contour, float width, float height) {
        float hw = width / 2;
        float hh = height / 2;

        Array<Vector2> verticesArray = new Array<Vector2>();

        switch (contour) {
            case 1:
                verticesArray.add(new Vector2(-hw, hh));
                verticesArray.add(new Vector2(0, 0));
                verticesArray.add(new Vector2(hw, hh));
                break;
            case 2:
                verticesArray.add(new Vector2(hw, hh));
                verticesArray.add(new Vector2(0, 0));
                verticesArray.add(new Vector2(hw, -hh));
                break;
            case 3:
                verticesArray.add(new Vector2(-hw, hh));
                verticesArray.add(new Vector2(hw, -hh));
                verticesArray.add(new Vector2(hw, hh));
                break;
            case 4:
                verticesArray.add(new Vector2(0, 0));
                verticesArray.add(new Vector2(-hw, -hh));
                verticesArray.add(new Vector2(hw, -hh));
                break;
            case 6:
                verticesArray.add(new Vector2(-hw, -hh));
                verticesArray.add(new Vector2(hw, -hh));
                verticesArray.add(new Vector2(hw, hh));
                break;
            case 8:
                verticesArray.add(new Vector2(-hw, hh));
                verticesArray.add(new Vector2(-hw, -hh));
                verticesArray.add(new Vector2(0, 0));
                break;
            case 9:
                verticesArray.add(new Vector2(-hw, hh));
                verticesArray.add(new Vector2(-hw, -hh));
                verticesArray.add(new Vector2(hw, hh));
                break;
            case 12:
                verticesArray.add(new Vector2(-hw, hh));
                verticesArray.add(new Vector2(-hw, -hh));
                verticesArray.add(new Vector2(hw, -hh));
                break;
            default:
                verticesArray.add(new Vector2(-hw, hh));
                verticesArray.add(new Vector2(-hw, -hh));
                verticesArray.add(new Vector2(hw, -hh));
                verticesArray.add(new Vector2(hw, hh));
                break;
        }
        return verticesArray.toArray(Vector2.class);
    }
}
