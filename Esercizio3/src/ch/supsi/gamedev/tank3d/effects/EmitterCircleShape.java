/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.supsi.gamedev.tank3d.effects;

import com.jme3.effect.shapes.EmitterShape;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.io.IOException;

/**
 *
 * @author Antoine
 */
public class EmitterCircleShape implements EmitterShape{
    private float radius;

    public EmitterCircleShape() {
    } 
        
    public EmitterCircleShape(float radius) {
        this.radius = radius;
    }

    public void setRadius(float radius){
        this.radius = radius;
    }
    
    public float getRadius(){
        return radius;
    }

    @Override
    public void getRandomPoint(Vector3f store) {
        float angle = FastMath.nextRandomFloat()*FastMath.TWO_PI;
        store.set(FastMath.cos(angle)*radius,0.0f,FastMath.sin(angle)*radius);
    }

    @Override
    public void getRandomPointAndNormal(Vector3f store, Vector3f normal) {
        this.getRandomPoint(store);
        normal.set(0.0f,1.0f,0.0f);
    }

    @Override
    public EmitterShape deepClone() {
          try {
            EmitterCircleShape clone = (EmitterCircleShape) super.clone();
            clone.radius = radius;
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(radius, "radius", 0f);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        this.radius = im.getCapsule(this).readFloat("radius", 0f);
    }
    
}

