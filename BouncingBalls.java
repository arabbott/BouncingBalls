/*
 * Adam R. Abbott
* CMSC 325
* Professor Karmaker
* Project 1
* Requirements:This program creates a cube with 
* three spheres that generate at random locations 
* and random velocities.  Gravity is simulated 
* within the cube, and the balls are programmed 
* to be elastic so that they bounce.  XYZ 
* coordinates are displayed of each ball's 
* position in real time. Additionally, the 
* position of each ballis recorded every second 
* to a text file.
* 
* I utilized the source code for the JMonkey 
* HelloPhysics tutorial as a foundation for my project.  
* However, the code is highly modified from it.  I also 
* used a coding idea from a fellow classmate 
* (Lee Galbraith)on how to implement a timer in the 
* SimpleUpdate method.  I also used some code snippets to 
* write the output text file to the user's home directory
* on their system.
* 
* Sources:
* 
* JMonkeyEngine 3 Tutorial (13) - Hello Physics. (n.d.). 
* Retrieved April 5, 2015, 
* from http://wiki.jmonkeyengine.org/doku.php/jme3:beginner:hello_physics
* 
* Galbreith, L. (n.d.). Retrieved April 5, 2015, 
* from https://learn.umuc.edu/d2l/le/94550/discussions/threads/3363971/View
* 
* Alexander, A. (2014, June 10). Java properties - Get the user's home directory from Java. 
* Retrieved April 5, 2015, 
* from http://alvinalexander.com/java/java-users-home-directory-system-property
* 
* How to write to file in Java using BufferedWriter. (n.d.). Retrieved April 5, 2015, 
* from http://beginnersbook.com/2014/01/how-to-write-to-file-in-java-using-bufferedwriter/
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adam
 */
public class BouncingBalls extends SimpleApplication implements PhysicsCollisionListener{
    
    public static void main(String args[]) {
        
    
    BouncingBalls app = new BouncingBalls();
    app.start();
  }
    
    private BulletAppState bulletAppState;
    //Declare all materials
    Material red_ball, blue_ball, green_ball, trans_wall, side_wall, back_wall, 
            ceiling_mat, floor_mat, bumper_mat1, bumper_mat2, bumper_mat3, bumper_mat4;
    //Declare all boxes used for floor and walls
    private static final Box wall, wall1, wall2, wall3, frontWall, floor, bumper;
    //Declare all variables to set physics controls
    private RigidBodyControl wall_phy, wall1_phy, wall2_phy, wall3_phy, frontWall_phy,
            floor_phy, red_phy, blue_phy, green_phy, bumper_phy1,bumper_phy2, bumper_phy3, bumper_phy4;
    
    //Declare a sphere class  
    private static final Sphere sphere;
    //Declare the variables used for the 3 balls
    protected Geometry player1, player2, player3, bumper1, bumper2, bumper3, bumper4;
    //Variables to be used to calculate wall dimensions
    private static final float wallLength = .5f;
    private static final float wallWidth = 20f;
    private static final float wallHeight = 20f;
    //Counter variable used to create a timer
    float counter = 0.0f;
    
    private int ball_hits = 0;
    private int bumper_hits = 0;
    
    private AudioNode bumper_audio;
    
    Random r4 = new Random();
        int low4 = 100;
        int high4 = 200;
        int randVelocity = r4.nextInt(high4-low4);
    
    //Sets size/dimensions for all shapes
    static {
    
    sphere = new Sphere(32, 32, 2.5f, true, false);
    sphere.setTextureMode(Sphere.TextureMode.Projected);    
    wall = new Box(wallLength, wallWidth, wallHeight);
    wall.scaleTextureCoordinates(new Vector2f(1f, .5f));
    wall1 = new Box(wallLength, wallWidth, wallHeight);
    wall1.scaleTextureCoordinates(new Vector2f(1f, .5f));
    wall2 = new Box(wallWidth, wallLength, wallHeight);
    wall2.scaleTextureCoordinates(new Vector2f(1f, .5f));
    wall3 = new Box(wallHeight, wallWidth, wallLength);
    wall3.scaleTextureCoordinates(new Vector2f(1f, .5f));
    frontWall = new Box(wallHeight, wallWidth, wallLength);
    frontWall.scaleTextureCoordinates(new Vector2f(1f, .5f));
    floor = new Box(20f, 0.5f, 20f);
    floor.scaleTextureCoordinates(new Vector2f(3, 3));
    
    bumper = new Box(2,2,2);
    
    
    
    
    }
    

    @Override
    public void simpleInitApp() {
        //Sets up the physics space and sets gravity globally to earth standard
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0f,-9.81f,0f));
        //This sets the camera view
        cam.setLocation(new Vector3f(0, 15f, -75f));
        cam.lookAt(Vector3f.ZERO, Vector3f.ZERO);
        //Decalring each varibale for the 3 balls
        player1 = new Geometry("Ball1", sphere);
        player2 = new Geometry("Ball2", sphere);
        player3 = new Geometry("Ball3", sphere);
        
        bumper1 = new Geometry("Bumper1", bumper);
        bumper2 = new Geometry("Bumper2", bumper);
        //A random number to be used to set random location and velocity
        Random r1 = new Random();
        int low1 = 1;
        int high1 = 10;
        int randCoord1 = r1.nextInt(high1-low1);
        //A random number to be used to set random location and velocity
        Random r2 = new Random();
        int low2 = 1;
        int high2 = 9;
        int randCoord2 = r2.nextInt(high2-low2);
        //A random number to be used to set random location and velocity
        Random r3 = new Random();
        int low3 = 1;
        int high3 = 8;
        int randCoord3 = r3.nextInt(high3-low3);
        //Used to create a random number for velocity value
        //Random r4 = new Random();
        //int low4 = 100;
        //int high4 = 200;
        //int randVelocity = r4.nextInt(high4-low4);
        //Sets random location for each ball
        player1.setLocalTranslation(randCoord2,0f,randCoord1);
        player2.setLocalTranslation(randCoord1,0f,randCoord2);
        player3.setLocalTranslation(randCoord3,0f,randCoord1);
        

        //Declaring physics space that will be assigned to each ball(gravity effect)
        red_phy = new RigidBodyControl(1f);
        blue_phy = new RigidBodyControl(1f);
        green_phy = new RigidBodyControl(1f);
        
 
        
        //Adds physics to each ball
        player1.addControl(red_phy);
        player2.addControl(blue_phy);
        player3.addControl(green_phy);

        //Adds physics spaces for each ball to the environment
        bulletAppState.getPhysicsSpace().add(red_phy);
        bulletAppState.getPhysicsSpace().add(blue_phy);
        bulletAppState.getPhysicsSpace().add(green_phy);
        
        //bulletAppState.getPhysicsSpace().add(bumper_phy);
        //sets friction for the balls
        red_phy.setFriction(1f);
        blue_phy.setFriction(1f);
        green_phy.setFriction(1f);
 
        //Randomly set the velocity of each ball
        red_phy.setLinearVelocity(new Vector3f(randVelocity,randVelocity,randVelocity));
        blue_phy.setLinearVelocity(new Vector3f(randVelocity,randVelocity,randVelocity));
        green_phy.setLinearVelocity(new Vector3f(randVelocity,randVelocity,randVelocity));
        //This is used to get the balls to bounce off each other, walls, floor, and ceiling
        player1.getControl(RigidBodyControl.class).setRestitution(-1);
        player2.getControl(RigidBodyControl.class).setRestitution(-1);
        player3.getControl(RigidBodyControl.class).setRestitution(-1);

        //This helps to stop the balls from bouncing through solid objects
        red_phy.setCcdMotionThreshold(0.05f);
        red_phy.setCcdSweptSphereRadius(0.01f);
        blue_phy.setCcdMotionThreshold(0.05f);
        blue_phy.setCcdSweptSphereRadius(0.01f);
        green_phy.setCcdMotionThreshold(0.05f);
        green_phy.setCcdSweptSphereRadius(0.01f);
        //Creates the cube and sets the color of the balls
        initMaterials();
        initWalls();
        initFloor();
        initBumpers();
        player1.setMaterial(red_ball);
        player2.setMaterial(blue_ball);
        player3.setMaterial(green_ball);
        

        //Attaches the balls to the environment
        rootNode.attachChild(player1);
        rootNode.attachChild(player2);
        rootNode.attachChild(player3);
        
        initBumperAudio();

        
        
        
        
        // CollisionResults results = new CollisionResults();
       //red_phy.collidesWith(blue_phy, results);
 /**       
        com.jme3.bullet.collision.shapes.CollisionShape sphere1 = 
        CollisionShapeFactory.createDynamicMeshShape(player1);
        
        com.jme3.bullet.collision.shapes.CollisionShape sphere2 = 
        CollisionShapeFactory.createDynamicMeshShape(player2);
        
        GhostControl objectiveGhost = new GhostControl(sphere1);
player1.addControl(objectiveGhost); 
bulletAppState.getPhysicsSpace().add(objectiveGhost);

GhostControl objectiveGhost1 = new GhostControl(sphere2);
player2.addControl(objectiveGhost); 
bulletAppState.getPhysicsSpace().add(objectiveGhost1);

//MyCustomControl physicsControl = new MyCustomControl();
//bulletAppState.getPhysicsSpace().addCollisionListener(physicsControl);
//CollisionResults result = new CollisionResults();
//objectiveGhost.collidesWith(objectiveGhost1, result);
*/
bulletAppState.getPhysicsSpace().addCollisionListener(this);




        

        

        
    }
    
    public void initBumpers() {
        bumper1 = new Geometry("Bumper1", bumper);
        bumper2 = new Geometry("Bumper2", bumper);
        bumper3 = new Geometry("Bumper3", bumper);
        bumper4 = new Geometry("Bumper4", bumper);
        //bumper1.setLocalTranslation(-10,-8,-14);
        bumper1.setLocalTranslation(-10,0,10);
        bumper2.setLocalTranslation(-3,-5,8);
        bumper3.setLocalTranslation(8,0,0);
        bumper4.setLocalTranslation(15,-5,-10);
        bumper_phy1 = new RigidBodyControl(1f);
        bumper_phy2 = new RigidBodyControl(1f);
        bumper_phy3 = new RigidBodyControl(1f);
        bumper_phy4 = new RigidBodyControl(1f);
        bumper1.addControl(bumper_phy1);
        bumper2.addControl(bumper_phy2);
        bumper3.addControl(bumper_phy3);
        bumper4.addControl(bumper_phy4);
        bumper_phy1.setKinematic(true);
        bumper_phy2.setKinematic(true);
        bumper_phy3.setKinematic(true);
        bumper_phy4.setKinematic(true);
        bulletAppState.getPhysicsSpace().add(bumper_phy1);
        bulletAppState.getPhysicsSpace().add(bumper_phy2);
        bulletAppState.getPhysicsSpace().add(bumper_phy3);
        bulletAppState.getPhysicsSpace().add(bumper_phy4);
        bumper_phy1.setFriction(1f);
        bumper_phy2.setFriction(1f);
        bumper_phy3.setFriction(1f);
        bumper_phy4.setFriction(1f);
        bumper1.setMaterial(bumper_mat1);
        bumper2.setMaterial(bumper_mat2);
        bumper3.setMaterial(bumper_mat3);
        bumper4.setMaterial(bumper_mat4);
        
        rootNode.attachChild(bumper1);
        rootNode.attachChild(bumper2);
        rootNode.attachChild(bumper3);
        rootNode.attachChild(bumper4);
        
    }
    
     private void initBumperAudio() {
    /* gun shot sound is to be triggered by a mouse click. */
    bumper_audio = new AudioNode(assetManager, "Sound/sound.wav", true);
    bumper_audio.setPositional(false);
    bumper_audio.setLooping(true);
    bumper_audio.setVolume(2);
    rootNode.attachChild(bumper_audio);
     }
    
    public void collision(PhysicsCollisionEvent event) {
        if ( "Ball1".equals(event.getNodeA().getName()) && "Ball2".equals(event.getNodeB().getName())) {
            
            Vector3f vel = red_phy.getLinearVelocity();
            float a = vel.getX();
            float b = vel.getY();
            float c = vel.getZ();
            
            Vector3f vel1 = blue_phy.getLinearVelocity();
            float a1 = vel1.getX();
            float b1 = vel1.getY();
            float c1 = vel1.getZ();
                    
            red_phy.setLinearVelocity(new Vector3f(-a1,-b1,-c1));
            blue_phy.setLinearVelocity(new Vector3f(-a,-b,-c));
            
            ball_hits++;
            System.out.println(ball_hits);
            //blue_phy.setLinearVelocity(new Vector3f(randVelocity,randVelocity,randVelocity));          
            //red_phy.setLinearVelocity(new Vector3f(randVelocity,randVelocity,randVelocity));
            }
        if ("Ball1".equals(event.getNodeA().getName()) && "Ball3".equals(event.getNodeB().getName())){
            ball_hits++;
            System.out.println(ball_hits);
            
            Vector3f vel = red_phy.getLinearVelocity();
            float a = vel.getX();
            float b = vel.getY();
            float c = vel.getZ();
            
            Vector3f vel1 = green_phy.getLinearVelocity();
            float a1 = vel1.getX();
            float b1 = vel1.getY();
            float c1 = vel1.getZ();
            
            red_phy.setLinearVelocity(new Vector3f(-a1,-b1,-c1));
            green_phy.setLinearVelocity(new Vector3f(-a,-b,-c));
        }
        if ("Ball2".equals(event.getNodeA().getName()) && "Ball3".equals(event.getNodeB().getName())){
            ball_hits++;
            System.out.println(ball_hits);
            
            
            Vector3f vel = blue_phy.getLinearVelocity();
            float a = vel.getX();
            float b = vel.getY();
            float c = vel.getZ();
            
            Vector3f vel1 = green_phy.getLinearVelocity();
            float a1 = vel1.getX();
            float b1 = vel1.getY();
            float c1 = vel1.getZ();
            
            blue_phy.setLinearVelocity(new Vector3f(-a1,-b1,-c1));
            green_phy.setLinearVelocity(new Vector3f(-a,-b,-c));
        }
        if ("Ball1".equals(event.getNodeA().getName()) && "Bumper1".equals(event.getNodeB().getName())){
            bumper_hits++;
            System.out.println("Bumper hits:" + bumper_hits);
            red_phy.setLinearVelocity(new Vector3f(randVelocity,randVelocity,randVelocity));
            bumper_mat1.setColor("Color", ColorRGBA.Red);
        }
        if ("Ball2".equals(event.getNodeA().getName()) && "Bumper1".equals(event.getNodeB().getName())){
            bumper_hits++;
            System.out.println("Bumper hits:" + bumper_hits);
            blue_phy.setLinearVelocity(new Vector3f(randVelocity,randVelocity,randVelocity));
            bumper_mat1.setColor("Color", ColorRGBA.Blue);
        }
        if ("Ball3".equals(event.getNodeA().getName()) && "Bumper1".equals(event.getNodeB().getName())){
            bumper_hits++;
            System.out.println("Bumper hits:" + bumper_hits);
            green_phy.setLinearVelocity(new Vector3f(randVelocity,randVelocity,randVelocity));
            bumper_mat1.setColor("Color", ColorRGBA.Green);
        }
        if ("Ball1".equals(event.getNodeA().getName()) && "Bumper2".equals(event.getNodeB().getName())){
            bumper_hits++;
            System.out.println("Bumper hits:" + bumper_hits);
            red_phy.setLinearVelocity(new Vector3f(randVelocity,randVelocity,randVelocity));
            bumper_mat2.setColor("Color", ColorRGBA.Red);
        }
        if ("Ball2".equals(event.getNodeA().getName()) && "Bumper2".equals(event.getNodeB().getName())){
            bumper_hits++;
            System.out.println("Bumper hits:" + bumper_hits);
            blue_phy.setLinearVelocity(new Vector3f(randVelocity,randVelocity,randVelocity));
            bumper_mat2.setColor("Color", ColorRGBA.Blue);
        }
        if ("Ball3".equals(event.getNodeA().getName()) && "Bumper2".equals(event.getNodeB().getName())){
            bumper_hits++;
            System.out.println("Bumper hits:" + bumper_hits);
            green_phy.setLinearVelocity(new Vector3f(randVelocity,randVelocity,randVelocity));
            bumper_mat2.setColor("Color", ColorRGBA.Green);
        }
        if ("Ball1".equals(event.getNodeA().getName()) && "Bumper3".equals(event.getNodeB().getName())){
            bumper_hits++;
            System.out.println("Bumper hits:" + bumper_hits);
            red_phy.setLinearVelocity(new Vector3f(randVelocity,randVelocity,randVelocity));
            bumper_mat3.setColor("Color", ColorRGBA.Red);
        }
        if ("Ball2".equals(event.getNodeA().getName()) && "Bumper3".equals(event.getNodeB().getName())){
            bumper_hits++;
            System.out.println("Bumper hits:" + bumper_hits);
            blue_phy.setLinearVelocity(new Vector3f(randVelocity,randVelocity,randVelocity));
            bumper_mat3.setColor("Color", ColorRGBA.Blue);
        }
        if ("Ball3".equals(event.getNodeA().getName()) && "Bumper3".equals(event.getNodeB().getName())){
            bumper_hits++;
            System.out.println("Bumper hits:" + bumper_hits);
            green_phy.setLinearVelocity(new Vector3f(randVelocity,randVelocity,randVelocity));
            bumper_mat3.setColor("Color", ColorRGBA.Green);
        }
        if ("Ball1".equals(event.getNodeA().getName()) && "Bumper4".equals(event.getNodeB().getName())){
            bumper_hits++;
            System.out.println("Bumper hits:" + bumper_hits);
            red_phy.setLinearVelocity(new Vector3f(randVelocity,randVelocity,randVelocity));
            bumper_mat4.setColor("Color", ColorRGBA.Red);
        }
        if ("Ball2".equals(event.getNodeA().getName()) && "Bumper4".equals(event.getNodeB().getName())){
            bumper_hits++;
            System.out.println("Bumper hits:" + bumper_hits);
            blue_phy.setLinearVelocity(new Vector3f(randVelocity,randVelocity,randVelocity));
            bumper_mat4.setColor("Color", ColorRGBA.Blue);
        }
        if ("Ball3".equals(event.getNodeA().getName()) && "Bumper4".equals(event.getNodeB().getName())){
            bumper_hits++;
            System.out.println("Bumper hits:" + bumper_hits);
            green_phy.setLinearVelocity(new Vector3f(randVelocity,randVelocity,randVelocity));
            bumper_mat4.setColor("Color", ColorRGBA.Green);
        }
        
    }
 
    
    /**
    @Override
    public void collision(PhysicsCollisionEvent event) {
        if("Ball1".equals(event.getNodeA().getName()) || "Ball1".equals(event.getNodeB().getName())) {
            collision_count++;
                System.out.println(collision_count);
            
            if("Ball2".equals(event.getNodeA().getName()) || "Ball2".equals(event.getNodeB().getName())) {
                collision_count++;
                System.out.println(collision_count);
        //output.println("\nCollision number: " + collision_count);
        //output.print(event.getNodeA().getName() + " collided with ");        
        //output.println(event.getNodeB().getName() + " at position:");
        //output.println("sphere1: " + sphere1_phy.getPhysicsLocation());
        //output.println("sphere2: " + sphere2_phy.getPhysicsLocation());
            }else if("Ball3".equals(event.getNodeA().getName()) || "Ball3".equals(event.getNodeB().getName())) {
                
                collision_count++;
                System.out.println(collision_count);
        //output.println("\nCollission number: " + collision_count);
        //output.print(event.getNodeA().getName() + " collided with ");        
        //output.println(event.getNodeB().getName() + " at position:");
        //output.println("sphere1: " + sphere1_phy.getPhysicsLocation());
        //output.println("sphere3: " + sphere3_phy.getPhysicsLocation());
            }
    }else if("Ball3".equals(event.getNodeA().getName()) || "Ball3".equals(event.getNodeB().getName())) {
        collision_count++;
                System.out.println(collision_count);
        if("Ball2".equals(event.getNodeA().getName()) || "Ball2".equals(event.getNodeB().getName())) {
            collision_count++;
            System.out.println(collision_count);
        //output.println("\nCollission number: " + collision_count);
        //output.print(event.getNodeA().getName() + " collided with ");        
        //output.println(event.getNodeB().getName() + " at position:");
        //output.println("sphere2: " + sphere2_phy.getPhysicsLocation());
        //output.println("sphere3: " + sphere3_phy.getPhysicsLocation());
        }
      } 
    }*/
    
    
    //This method is used to create the materials used for all my objects
    public void initMaterials() {
        
        red_ball = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        red_ball.setColor("Color", ColorRGBA.Red);
        
        blue_ball = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        blue_ball.setColor("Color", ColorRGBA.Blue);
        
        green_ball = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        green_ball.setColor("Color", ColorRGBA.Green);
        
        trans_wall = new Material(assetManager, 
        "Common/MatDefs/Misc/Unshaded.j3md");
        trans_wall.setColor("Color", new ColorRGBA(255,255,255,0.1f));
        trans_wall.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        side_wall = new Material(assetManager, 
        "Common/MatDefs/Misc/Unshaded.j3md");
        side_wall.setColor("Color", new ColorRGBA(255,255,255,0.5f));
        side_wall.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        back_wall = new Material(assetManager, 
        "Common/MatDefs/Misc/Unshaded.j3md");
        back_wall.setColor("Color", new ColorRGBA(255,255,255,0.3f));
        back_wall.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        ceiling_mat = new Material(assetManager, 
        "Common/MatDefs/Misc/Unshaded.j3md");
        ceiling_mat.setColor("Color", new ColorRGBA(255,255,255,0.8f));
        ceiling_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        floor_mat.setColor("Color", ColorRGBA.Brown);
        
        bumper_mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bumper_mat1.setColor("Color", ColorRGBA.White);
        bumper_mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bumper_mat2.setColor("Color", ColorRGBA.White);
        bumper_mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bumper_mat3.setColor("Color", ColorRGBA.White);
        bumper_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bumper_mat4.setColor("Color", ColorRGBA.White);
    
    }
    //This method creates the floor and sets its physics properties
    public void initFloor() {
    Geometry floor_geo = new Geometry("Floor", floor);
    floor_geo.setMaterial(floor_mat);
    floor_geo.setLocalTranslation(2, -20f, 0);
    this.rootNode.attachChild(floor_geo);
    floor_phy = new RigidBodyControl(0.0f);
    floor_geo.addControl(floor_phy);
    floor_geo.getControl(RigidBodyControl.class).setRestitution(-1);
    bulletAppState.getPhysicsSpace().add(floor_phy);
    }
    //This method creates the cube and assigns its physics properties-solid walls, etc.
    public void initWalls() {
      Geometry rightWall_geo = new Geometry ("Wall", wall);
      Geometry leftWall_geo = new Geometry ("Wall1", wall);
      Geometry ceiling = new Geometry ("Wall2", wall2);
      Geometry backWall_geo = new Geometry ("Wall2", wall3);
      Geometry front_geo = new Geometry ("Front", frontWall);
      rightWall_geo.setMaterial(side_wall);
      leftWall_geo.setMaterial(side_wall);
      ceiling.setMaterial(ceiling_mat);
      backWall_geo.setMaterial(back_wall);
      front_geo.setMaterial(trans_wall);
      front_geo.setQueueBucket(RenderQueue.Bucket.Transparent);  //Allows the front wall to be transparent
      rootNode.attachChild(rightWall_geo);
      rootNode.attachChild(leftWall_geo);
      rootNode.attachChild(ceiling);
      rootNode.attachChild(backWall_geo);
      rootNode.attachChild(front_geo);
      rightWall_geo.setLocalTranslation(-18,0f,0);
      leftWall_geo.setLocalTranslation(22,0f,0);
      ceiling.setLocalTranslation(2,20f,0);
      backWall_geo.setLocalTranslation(2,0f,20);
      front_geo.setLocalTranslation(2,0f,-20);
      wall_phy = new RigidBodyControl(0.0f);
      wall1_phy = new RigidBodyControl(0.0f);
      wall2_phy = new RigidBodyControl(0.0f);
      wall3_phy = new RigidBodyControl(0.0f);
      frontWall_phy = new RigidBodyControl(0.0f);
      rightWall_geo.addControl(wall_phy);
      rightWall_geo.getControl(RigidBodyControl.class).setRestitution(-1);
      leftWall_geo.addControl(wall1_phy);
      leftWall_geo.getControl(RigidBodyControl.class).setRestitution(-1);
      ceiling.addControl(wall2_phy);
      ceiling.getControl(RigidBodyControl.class).setRestitution(-1);
      backWall_geo.addControl(wall3_phy);
      backWall_geo.getControl(RigidBodyControl.class).setRestitution(-1);
      front_geo.addControl(frontWall_phy);
      front_geo.getControl(RigidBodyControl.class).setRestitution(-1);
      bulletAppState.getPhysicsSpace().add(wall_phy);
      bulletAppState.getPhysicsSpace().add(wall1_phy);
      bulletAppState.getPhysicsSpace().add(wall2_phy);
      bulletAppState.getPhysicsSpace().add(wall3_phy);
      bulletAppState.getPhysicsSpace().add(frontWall_phy);
      }
    //Displays the ball's location in real-time and records them to a text file every second
    public void simpleUpdate(float tpf) {
        //Counter variable
        counter += tpf;
        //Varibale to store reb ball coordinates
        float redBallXCoord;
        float redBallYCoord;
        float redBallZCoord;
        //Varibale to store blue ball coordinates
        float blueBallXCoord;
        float blueBallYCoord;
        float blueBallZCoord;
        //Variable to store the green ball coordinates
        float greenBallXCoord;
        float greenBallYCoord;
        float greenBallZCoord;
        //These get the location of each ball and assign them to a varibale
        Vector3f redBallLocation = red_phy.getPhysicsLocation();
        Vector3f blueBallLocation = blue_phy.getPhysicsLocation();
        Vector3f greenBallLocation = green_phy.getPhysicsLocation();
        //Assigns the coordinates of the red ball to the varibales declared above
        redBallXCoord = redBallLocation.getX();
        redBallYCoord = redBallLocation.getY();
        redBallZCoord = redBallLocation.getZ();
        //Assigns the coordinates of the blue ball to the varibales declared above
        blueBallXCoord = blueBallLocation.getX();
        blueBallYCoord = blueBallLocation.getY();
        blueBallZCoord = blueBallLocation.getZ();
        //Assigns the coordinates of the green ball to the varibales declared above
        greenBallXCoord = greenBallLocation.getX();
        greenBallYCoord = greenBallLocation.getY();
        greenBallZCoord = greenBallLocation.getZ();
        //Declare strings to store the ball locations to be used in their real-time display
        String redLoc = "Red Ball Location   " + "X: " + redBallXCoord + "   "
              + "Y: " + redBallYCoord + "   " + "Z: " + redBallZCoord;
        
        String blueLoc = "Blue Ball Location   " + "X: " + blueBallXCoord + "   "
              + "Y: " + blueBallYCoord + "   " + "Z: " + blueBallZCoord;
        
        String greenLoc = "Green Ball Location   " + "X: " + greenBallXCoord + "   "
              + "Y: " + greenBallYCoord + "   " + "Z: " + greenBallZCoord;
        //guiNode that displays the ball locations in real time on the screen
        
        

        
        
        
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        
        
        String hit_count = ball_hits + "";
        BitmapText BallHits = new BitmapText(guiFont, false);
        BallHits.setSize(guiFont.getCharSet().getRenderedSize());
        BallHits.setColor(ColorRGBA.White);
        BallHits.setText("Total Ball Hits: " + hit_count);
        BallHits.setLocalTranslation(200,75,0);
        
        String bumper_count = bumper_hits + "";
        BitmapText BumperHits = new BitmapText(guiFont, false);
        BumperHits.setSize(guiFont.getCharSet().getRenderedSize());
        BumperHits.setColor(ColorRGBA.White);
        BumperHits.setText("Total Bumper Hits: " + bumper_count);
        BumperHits.setLocalTranslation(200,50,0);
        
        
        
        
        BitmapText redBall = new BitmapText(guiFont, false);
        redBall.setSize(guiFont.getCharSet().getRenderedSize());
        redBall.setColor(ColorRGBA.Red);
        redBall.setText(redLoc);
        //redBall.setText(hit_count);
        redBall.setLocalTranslation(150, 400, 0);
        BitmapText blueBall = new BitmapText(guiFont, false);
        blueBall.setSize(guiFont.getCharSet().getRenderedSize());
        blueBall.setColor(ColorRGBA.Blue);
        blueBall.setText(blueLoc);
        blueBall.setLocalTranslation(150, 375, 0);
        BitmapText greenBall = new BitmapText(guiFont, false);
        greenBall.setSize(guiFont.getCharSet().getRenderedSize());
        greenBall.setColor(ColorRGBA.Green);
        greenBall.setText(greenLoc);
        greenBall.setLocalTranslation(150, 350, 0);
        guiNode.attachChild(redBall);
        guiNode.attachChild(blueBall);
        guiNode.attachChild(greenBall);
        
        guiNode.attachChild(BallHits);
        guiNode.attachChild(BumperHits);
        //The below is used to get current date and time which is then used as a time stamp for the text file output
        Date timeHack = Calendar.getInstance().getTime();
        SimpleDateFormat formattedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String dateTime = formattedDateTime.format(timeHack);
        //This gets the user's home directory on their system and sets it to a variable
        String userHome = System.getProperty("user.home");
        //This creates a new File object with the userHome variable above and the name of the text file
        File file = new File(userHome + "\\bouncing_balls.txt");
        //This checks to see if the file above exists, if not it creates it.
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(BouncingBalls.class.getName()).log(Level.SEVERE, null, ex);
            }
	  }
        //The below is a timer that sends the location of each ball with a time stamp 
        //to a text file every second
        if (counter > 1.0f){
            
            BufferedWriter buffer;
            try {
                
                buffer=new BufferedWriter(new FileWriter(file,true));
                buffer.write(dateTime + "  " + redLoc);
                buffer.newLine();
                buffer.write(dateTime + "  " + blueLoc);
                buffer.newLine();
                buffer.write(dateTime + "  " + greenLoc);
                buffer.newLine();
                buffer.newLine();
                buffer.close();
                
                counter = 0.0f;
                
                }
            
            catch (IOException exception) {}
            
            }
        
        
        
    }
    
    
    
    
    
}
