/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author Antoine
 */
public class TankFactory extends Factory{
    public TankFactory(AssetManager assetManager){
        super(assetManager);
    }
    
    public Node newTank(String name){
        Box chassis = new Box(2.0f, 0.5f, 3.5f);
        Box turret = new Box(1.0f, 0.4f, 1.0f);
        Cylinder cylinder = new Cylinder(16,16,0.2f,3,true);
        
        Geometry chassisgeom = new Geometry("Chassis", chassis);
        Geometry turretgeom = new Geometry("Turret", turret);
        Geometry cylindergeom = new Geometry("Cylinder", cylinder);
        
        cylindergeom.setLocalTranslation(0.0f, 0.0f, 1.5f);
        
        Material chassismat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        chassismat.setColor("Color", ColorRGBA.DarkGray);
        chassisgeom.setMaterial(chassismat);  
        
        Material turretmat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        turretmat.setColor("Color", ColorRGBA.LightGray);
        turretgeom.setMaterial(turretmat);
        
        Material cannonmat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        cannonmat.setColor("Color", ColorRGBA.Black);
        cylindergeom.setMaterial(cannonmat);
        
        Node chassisnode = new Node(name);
        Node turretode = new Node(name+"Turret");        
        Node cylindernode = new Node(name+"Cannon");
        
        turretode.setLocalTranslation(0.0f, 0.9f, -0.8f);
        cylindernode.setLocalTranslation(0.0f,0.0f,1.0f);
        
        chassisnode.attachChild(chassisgeom);
        chassisnode.attachChild(turretode);
        turretode.attachChild(turretgeom);
        turretode.attachChild(cylindernode);
        cylindernode.attachChild(cylindergeom);
        return chassisnode;
    }
}
