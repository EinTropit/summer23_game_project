package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Screens.PlayScreen;

public class Crab extends Enemy {
    private float stateTime;
        private Animation<TextureRegion> idleAnimation;
    private Array<TextureRegion> frames;
    private static final int FRAME_WIDTH = 72;
    private static final int FRAME_HEIGHT = 32;

    public Crab(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();

        Texture texture = new Texture("enemies/crabby.png");
        for (int i = 0; i < 9; i++)
            frames.add(new TextureRegion(texture, i * FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT));
        idleAnimation = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        stateTime = 0;

        setBounds(getX(), getY(), FRAME_WIDTH / MyGdxGame.PPM, FRAME_HEIGHT / MyGdxGame.PPM);

        setToDestroy = false;
        destroyed = false;
    }

    public void update(float dt) {
        stateTime += dt;
        if(setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
            //setRegion();
        }
        else if (!destroyed) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(idleAnimation.getKeyFrame(stateTime, true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(10 / MyGdxGame.PPM);
        fdef.filter.categoryBits = MyGdxGame.ENEMY_BIT;
        fdef.filter.maskBits = MyGdxGame.GROUND_BIT | MyGdxGame.PLAYER_BIT | MyGdxGame.PLAYER_SWORD_BIT |
                MyGdxGame.SWORD_BIT | MyGdxGame.ENEMY_BIT | MyGdxGame.OBJECT_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void hit() {
        setToDestroy = true;
    }
}
