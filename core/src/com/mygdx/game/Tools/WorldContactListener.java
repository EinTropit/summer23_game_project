package com.mygdx.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Sprites.Enemy;
import com.mygdx.game.Sprites.InteractiveTileObject;
import com.mygdx.game.Sprites.Player;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        if("feet".equals(fixA.getUserData()) || "feet".equals(fixB.getUserData())) {
            Fixture feet = fixA.getUserData() == "feet" ? fixA : fixB;
            Fixture object = feet == fixA ? fixB : fixA;

            if(object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())) {
                ((InteractiveTileObject) object.getUserData()).onCollision();
            }
        }

        switch (cDef) {
            case (MyGdxGame.PLAYER_SWORD_BIT | MyGdxGame.ENEMY_BIT):
                Gdx.app.log("contact", "attack");
                Fixture player = fixA.getFilterData().categoryBits == MyGdxGame.PLAYER_SWORD_BIT ? fixA : fixB;
                Fixture enemy = player == fixA ? fixB : fixA;
                ((Player)player.getUserData()).setEnemy((Enemy)enemy.getUserData());
                if(((Player)player.getUserData()).getState() == Player.State.ATTACKING)
                    ((Enemy)enemy.getUserData()).hit();
                break;
        }

    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
        switch (cDef) {
            case (MyGdxGame.PLAYER_SWORD_BIT | MyGdxGame.ENEMY_BIT):
                Gdx.app.log("contact", "attack");
                Fixture player = fixA.getFilterData().categoryBits == MyGdxGame.PLAYER_SWORD_BIT ? fixA : fixB;
                Fixture enemy = player == fixA ? fixB : fixA;
                ((Player) player.getUserData()).setEnemy(null);
                break;
        }

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
