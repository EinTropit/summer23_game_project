package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Screens.PlayScreen;

public class Player extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING }
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private Animation<TextureRegion> playerStand;
    private Animation<TextureRegion> playerRun;
    private Animation<TextureRegion> playerJump;
    private float stateTimer;
    private boolean runningRight;
    private static final int FRAME_WIDTH = 50;
    private static final int FRAME_HEIGHT = 37;
    private static final int TXTR_IN_ROW = 7;


    public Player(World world, PlayScreen screen) {
        super(screen.getAtlas().findRegion("adventurer-Sheet"));
        this.world = world;
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 0; i < 4; i++)
            frames.add(new TextureRegion(getTexture(), (i % TXTR_IN_ROW) * FRAME_WIDTH, (i / TXTR_IN_ROW) * FRAME_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT));
        playerStand = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();
        for (int i = 8; i < 14; i++)
            frames.add(new TextureRegion(getTexture(), (i % TXTR_IN_ROW) * FRAME_WIDTH, (i / TXTR_IN_ROW) * FRAME_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT));
        playerRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();
        for (int i = 16; i < 23; i++)
            frames.add(new TextureRegion(getTexture(), (i % TXTR_IN_ROW) * FRAME_WIDTH, (i / TXTR_IN_ROW) * FRAME_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT));
        playerJump = new Animation<TextureRegion>(0.07f, frames);

        definePlayer();

        setBounds(0, 0, FRAME_WIDTH / MyGdxGame.PPM, FRAME_HEIGHT / MyGdxGame.PPM);
        setRegion(new TextureRegion(getTexture(), 0, 0, FRAME_WIDTH, FRAME_HEIGHT));
    }

    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case JUMPING:
                region = playerJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = playerRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = playerStand.getKeyFrame(stateTimer, true);
                break;
        }

        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }
        else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState() {
        if(b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        if(b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        if(b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        return State.STANDING;
    }

    public void definePlayer() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(64 / MyGdxGame.PPM, 64 * 6 / MyGdxGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(16 / MyGdxGame.PPM);

        fdef.shape = shape;
        b2body.createFixture(fdef);
    }
}
