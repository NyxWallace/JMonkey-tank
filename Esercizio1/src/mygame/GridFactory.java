/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Grid;

/**
 *
 * @author Antoine
 */
public class GridFactory extends Factory{
    
    public GridFactory(AssetManager assetManager){
        super(assetManager);
    }
    
    public Node newGrid(String name){
        Grid grid = new Grid(256, 256, 2);
        Geometry geom = new Geometry(name, grid);
        geom.setLocalTranslation(-128,0,-128);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
        Node gridNode = new Node();
        gridNode.attachChild(geom);
        return gridNode;
    }
}
