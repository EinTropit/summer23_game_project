package com.mygdx.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Screens.PlayScreen;

public class Player extends Sprite {


    public enum State { FALLING, JUMPING, STANDING, RUNNING, ATTACKING }
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    public Body swordBody;
    //private TextureAtlas atlas;
    private Array<Animation<TextureRegion>> playerStand;
    private Array<Animation<TextureRegion>> playerRun;
    private Array<Animation<TextureRegion>> playerJump;
    private Array<TextureRegion> playerFall;
    private Array<Animation<TextureRegion>> attackAnim;
    private int rndAttack;
    private float stateTimer;
    private boolean runningRight;
    private int hasSword;
    private Enemy enemy;
    private static final int FRAME_WIDTH = 64;
    private static final int FRAME_HEIGHT = 40;
    //private static final int TXTR_IN_ROW = 7;


    public Player(PlayScreen screen) {

        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        hasSword = 0;

        MapObject object = screen.getMap().getLayers().get(3).getObjects().getByType(RectangleMapObject.class).first();
        Rectangle startRect = ((RectangleMapObject) object).getRectangle();
        definePlayer(startRect);

        playerStand = new Array<Animation<TextureRegion>>(2);
        playerRun = new Array<Animation<TextureRegion>>(2);
        playerJump = new Array<Animation<TextureRegion>>(2);
        playerFall = new Array<TextureRegion>(2);
        attackAnim = new Array<Animation<TextureRegion>>(3);
        rndAttack = 0;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        TextureAtlas atlas = new TextureAtlas("character/Captain_No_Sword.pack");
        for (int i = 0; i < 5; i++)
            frames.add(new TextureRegion(atlas.findRegion("Idle"), i * FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT));
        playerStand.add(new Animation<TextureRegion>(0.1f, frames));
        frames.clear();
        for (int i = 0; i < 6; i++)
            frames.add(new TextureRegion(atlas.findRegion("Run"), i * FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT));
        playerRun.add(new Animation<TextureRegion>(0.1f, frames));
        frames.clear();
        for (int i = 0; i < 3; i++)
            frames.add(new TextureRegion(atlas.findRegion("Jump"), i * FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT));
        playerJump.add(new Animation<TextureRegion>(0.1f, frames));
        frames.clear();
        playerFall.add(new TextureRegion(atlas.findRegion("Fall"), 0, 0, FRAME_WIDTH, FRAME_HEIGHT));

        //atlas.dispose();
        TextureAtlas atlas2 = new TextureAtlas("character/Captain_Sword.pack");
        for (int i = 0; i < 5; i++)
            frames.add(new TextureRegion(atlas2.findRegion("Idle"), i * FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT));
        playerStand.add(new Animation<TextureRegion>(0.1f, frames));
        frames.clear();
        for (int i = 0; i < 6; i++)
            frames.add(new TextureRegion(atlas2.findRegion("Run"), i * FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT));
        playerRun.add(new Animation<TextureRegion>(0.1f, frames));
        frames.clear();
        for (int i = 0; i < 3; i++)
            frames.add(new TextureRegion(atlas2.findRegion("Jump"), i * FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT));
        playerJump.add(new Animation<TextureRegion>(0.1f, frames));
        playerFall.add(new TextureRegion(atlas2.findRegion("Fall"), 0, 0, FRAME_WIDTH, FRAME_HEIGHT));
        frames.clear();
        for (int j = 1; j < 4; j++) {
            for (int i = 0; i < 3; i++)
                frames.add(new TextureRegion(atlas2.findRegion("Attack " + j), i * FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT));
            attackAnim.add(new Animation<TextureRegion>(0.1f, frames));
            frames.clear();
        }

        setBounds(0, 0, FRAME_WIDTH / MyGdxGame.PPM, FRAME_HEIGHT / MyGdxGame.PPM);
        setRegion(new TextureRegion(atlas.findRegion("Idle"), 0, 0, FRAME_WIDTH, FRAME_HEIGHT));
    }

    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
        swordBody.setTransform(b2body.getPosition().x + (runningRight ? 17 : -17) / MyGdxGame.PPM, b2body.getPosition().y, b2body.getAngle());

    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case JUMPING:
                region = playerJump.get(hasSword).getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = playerRun.get(hasSword).getKeyFrame(stateTimer, true);
                break;
            case FALLING:
                region = playerFall.get(hasSword);
                break;
            case ATTACKING:
                region = attackAnim.get(rndAttack).getKeyFrame(stateTimer);
                break;
            case STANDING:
            default:
                region = playerStand.get(hasSword).getKeyFrame(stateTimer, true);
                break;
        }

        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
            /*Filter filter = new Filter();
            filter.categoryBits = MyGdxGame.DESTROYED_BIT;
            //Gdx.app.log("dfad", "fds "+b2body.getFixtureList().size);
            b2body.getFixtureList().get(2).setFilterData(filter);

            filter.categoryBits = MyGdxGame.PLAYER_SWORD_BIT;
            b2body.getFixtureList().get(3).setFilterData(filter);

             */
        }
        else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
            /*Filter filter = new Filter();
            filter.categoryBits = MyGdxGame.DESTROYED_BIT;
            //Gdx.app.log("dfad", "fds "+b2body.getFixtureList().size);
            b2body.getFixtureList().get(3).setFilterData(filter);

            filter.categoryBits = MyGdxGame.PLAYER_SWORD_BIT;
            b2body.getFixtureList().get(2).setFilterData(filter);

             */
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState() {
        if(previousState == State.ATTACKING && stateTimer <= 0.3f)
            return State.ATTACKING;
        if(b2body.getLinearVelocity().y > 0)
            return State.JUMPING;
        if(b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        if(b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        return State.STANDING;
    }

    public void definePlayer(Rectangle startRect) {
        BodyDef bdef = new BodyDef();
        bdef.position.set((startRect.getX() + startRect.getWidth() / 2) / MyGdxGame.PPM, (startRect.getY() + startRect.getHeight() / 2) / MyGdxGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);
        swordBody = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(10 / MyGdxGame.PPM);
        fdef.filter.categoryBits = MyGdxGame.PLAYER_BIT;
        fdef.filter.maskBits = MyGdxGame.GROUND_BIT | MyGdxGame.SWORD_BIT | MyGdxGame.OBJECT_BIT | MyGdxGame.ENEMY_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef);

        EdgeShape feet = new EdgeShape();
        feet.set(new Vector2(-5 / MyGdxGame.PPM, -9 / MyGdxGame.PPM), new Vector2(5 / MyGdxGame.PPM, -9 / MyGdxGame.PPM));
        fdef.shape = feet;
        fdef.filter.categoryBits = MyGdxGame.PLAYER_BIT;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData("feet");

        PolygonShape sword = new PolygonShape();
        Vector2[] vertices= new Vector2[4];
        vertices[0] = new Vector2(-8, -3).scl(1 / MyGdxGame.PPM);
        vertices[1] = new Vector2(8, -3).scl(1 / MyGdxGame.PPM);
        vertices[2] = new Vector2(-8, -9).scl(1 / MyGdxGame.PPM);
        vertices[3] = new Vector2(8, -9).scl(1 / MyGdxGame.PPM);
        sword.set(vertices);
        fdef.shape = sword;
        fdef.isSensor =true;
        fdef.filter.categoryBits = MyGdxGame.PLAYER_SWORD_BIT;
        swordBody.createFixture(fdef).setUserData(this);

        /*
        vertices[0] = new Vector2(-10, -3).scl(1 / MyGdxGame.PPM);
        vertices[1] = new Vector2(-25, -3).scl(1 / MyGdxGame.PPM);
        vertices[2] = new Vector2(-10, -9).scl(1 / MyGdxGame.PPM);
        vertices[3] = new Vector2(-25, -9).scl(1 / MyGdxGame.PPM);
        sword.set(vertices);
        fdef.shape = sword;
        fdef.isSensor =true;
        fdef.filter.categoryBits = MyGdxGame.DESTROYED_BIT;
        b2body.createFixture(fdef).setUserData(this);

         */

    }

    public void setSword(boolean hasSword) {
        this.hasSword = hasSword ? 1 : 0;
    }

    public Boolean hasSword() {
        return hasSword == 1;
    }

    public void attack() {
        if(currentState != State.ATTACKING) {
            previousState = currentState = State.ATTACKING;
            stateTimer = 0;
            rndAttack = (int)(Math.random() * 3);
            if(enemy != null)
                enemy.hit();
        }
    }

    public void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }
}
