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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private Matcher matcher;
    private boolean end = false;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        BulletAppState states = new BulletAppState();
        stateManager.attach(states);
        Team teamA = null;
        Team teamB = null;
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
        matcher = new Matcher(assetManager, settings, guiNode);
        timer = new Timer(assetManager, settings, guiNode);
        
        /*
        *       =========================================
        *      ||               CREATE BALL             ||
        *       =========================================
        */
        
        Material matBall = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matBall.setTexture("DiffuseMap", assetManager.loadTexture("Materials/pelota.png"));
      
        ball = new Ball(matBall, new Vector3f(2,1,1), goalHelper, matcher, teamANode, teamBNode);
        states.getPhysicsSpace().add(ball.getPhysics());
        ball.getPhysics().setLinearDamping(0.15f);
        
        /*
        *       =========================================
        *      ||              CREATE TEAM A            ||
        *       =========================================
        */
        
        Vector3f [] positionsA = new Vector3f[]{
            new Vector3f(-33.5f,2,-50), //defenosr_left
            new Vector3f(+33.5f,2,-50), //defensor_right
            new Vector3f(0f,2,-29.3f), //midfield
            new Vector3f(0f,2,-85f), //goalkeeper
            new Vector3f(-33.5f,2,+25.5f), //leading_left
            new Vector3f(+33.5f,2,+25.5f)  //leading_right
        };
        String [] filesPasarA = new String[]{
            "pasar_defensor_left_teamA",
            "pasar_defensor_right_teamA",
            "pasar_midfield_teamA",
            "pasar_goalKeepper_teamA",
            "pasar_leading_left_teamA",
            "pasar_leading_right_teamA"};
        
        Material matTeamA = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matTeamA.setTexture("DiffuseMap", assetManager.loadTexture("Materials/naranja.jpg"));
        
        try {
            teamA = new Team(matTeamA, "JUGADOR", teamBNode, teamANode, ball, states, positionsA,0, stadium.getPorteria2(), matcher, filesPasarA);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        teams.attachChild(teamANode);
        
        
        /*
        *       =========================================
        *      ||              CREATE TEAM B            ||
        *       =========================================
        */
        
        Vector3f [] positionsB = new Vector3f[]{
            new Vector3f(+33.5f,2,+50), //defenosr_left
            new Vector3f(-33.5f,2,+50), //defensor_right
            new Vector3f(0f,2,+29.3f), //midfield
            new Vector3f(0f,2,+85f), //goalkeeper
            new Vector3f(+33.5f,2,-25.5f), //leading_left
            new Vector3f(-33.5f,2,-25.5f)  //leading_right
        };
        String [] filesPasarB = new String[]{
            "pasar_defensor_left_teamB",
            "pasar_defensor_right_teamB",
            "pasar_midfield_teamB",
            "pasar_goalKeepper_teamB",
            "pasar_leading_left_teamB",
            "pasar_leading_right_teamB"};
        
        Material matTeamB = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matTeamB.setTexture("DiffuseMap", assetManager.loadTexture("Materials/azul.png"));
        
        try {
            teamB = new Team(matTeamB, "JUGADOR", teamANode, teamBNode, ball, states, positionsB,1, stadium.getPorteria2(), matcher, filesPasarB);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        if(timer !=null && timer.getTime()<=600){
            timer.addTime(tpf);
        }
        
        if(timer !=null && timer.getTime()>600 && !end){
            rootNode.detachAllChildren();
            timer.dettachFromParent();
            matcher.finishGame();
            end = true;
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
