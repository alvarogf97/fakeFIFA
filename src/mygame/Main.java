package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import mygame.models.Ball;
import mygame.models.Team;
import mygame.terrain.FootballStadium;
import mygame.terrain.GoalHelper;
import mygame.terrain.Matcher;
import mygame.terrain.Timer;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    
    private Timer timer;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        BulletAppState states = new BulletAppState();
        stateManager.attach(states);
        Team teamA;
        Team teamB;
        Node teamANode = new Node();
        Node teamBNode = new Node();
        Node teams = new Node();
        Ball ball;
        
        /*
        *       =========================================
        *      ||            CREATE TERRAIN             ||
        *       =========================================
        */
        
        lighter();
        setDisplayFps(false);
        setDisplayStatView(false);
        cam.setLocation(new Vector3f(0, 50f, 0f));
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        FootballStadium stadium = new FootballStadium(assetManager);
        RigidBodyControl stadiumPhysics = new RigidBodyControl(0f);
        stadium.getNode().addControl(stadiumPhysics);
        states.getPhysicsSpace().add(stadiumPhysics);
        GoalHelper goalHelper = new GoalHelper(assetManager);
        Matcher matcher = new Matcher(assetManager, settings, guiNode);
        timer = new Timer(assetManager, settings, guiNode);
        
        /*
        *       =========================================
        *      ||               CREATE BALL             ||
        *       =========================================
        */
        
        Material matBall = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matBall.setTexture("DiffuseMap", assetManager.loadTexture("Materials/pelota.png"));
      
        ball = new Ball(matBall, new Vector3f(0,1,0), goalHelper, matcher, teamANode, teamBNode);
        states.getPhysicsSpace().add(ball.getPhysics());
        
        /*
        *       =========================================
        *      ||              CREATE TEAM A            ||
        *       =========================================
        */
        
        Vector3f [] positionsA = new Vector3f[]{
            new Vector3f(-33.5f,1,-50), //defenosr_left
            new Vector3f(+33.5f,1,-50), //defensor_right
            new Vector3f(0f,1,-29.3f), //midfield
            new Vector3f(0f,1,-85f), //goalkeeper
            new Vector3f(-33.5f,1,+25.5f), //leading_left
            new Vector3f(+33.5f,1,+25.5f)  //leading_right
        };
        Material matTeamA = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matTeamA.setTexture("DiffuseMap", assetManager.loadTexture("Materials/naranja.jpg"));
        
        teamA = new Team(matTeamA, "JUGADOR", teamBNode, teamANode, ball, states, positionsA);
        teams.attachChild(teamANode);
        
        
        /*
        *       =========================================
        *      ||              CREATE TEAM B            ||
        *       =========================================
        */
        
        Vector3f [] positionsB = new Vector3f[]{
            new Vector3f(+33.5f,1,+50), //defenosr_left
            new Vector3f(-33.5f,1,+50), //defensor_right
            new Vector3f(0f,1,+29.3f), //midfield
            new Vector3f(0f,1,+85f), //goalkeeper
            new Vector3f(+33.5f,1,-25.5f), //leading_left
            new Vector3f(-33.5f,1,-25.5f)  //leading_right
        };
        Material matTeamB = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matTeamB.setTexture("DiffuseMap", assetManager.loadTexture("Materials/azul.png"));
        
        teamB = new Team(matTeamB, "JUGADOR", teamANode, teamBNode, ball, states, positionsB);
        teams.attachChild(teamANode);
        
        /*
        *       =========================================
        *      ||            ADD TO ROOT NODE           ||
        *       =========================================
        */
        
        rootNode.attachChild(stadium.getNode());
        rootNode.attachChild(teamANode);
        rootNode.attachChild(teamBNode);
        rootNode.attachChild(ball.getGeometry());
        rootNode.attachChild(goalHelper.getGoal_front());
        rootNode.attachChild(goalHelper.getGoal_back());
        
        ball.setTeamA(teamANode);
        ball.setTeamB(teamBNode);
        teamA.setOponents(teamBNode);
        teamB.setOponents(teamANode);

    }

    @Override
    public void simpleUpdate(float tpf) {
        if(timer !=null){
            timer.addTime(tpf);
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    public void lighter(){
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f,-0.5f,-0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
    }
}
