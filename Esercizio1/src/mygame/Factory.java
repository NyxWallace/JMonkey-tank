/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;

/**
 *
 * @author Antoine
 */
public class Factory {
    protected final AssetManager assetManager;
    
    public Factory(AssetManager assetManager){
        this.assetManager = assetManager;
    }
}
