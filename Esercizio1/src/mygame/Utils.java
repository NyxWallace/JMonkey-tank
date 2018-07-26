/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Antoine
 */
public class Utils {
    public static <T extends Spatial> T getChild(Node parent, String name, Class<T> spatialClass){
        T result = null;
        Spatial spatial = parent.getChild(name);
        if(spatialClass.isAssignableFrom(spatial.getClass())){
            result = (T) spatial;
        }
        return result;
    }
}
