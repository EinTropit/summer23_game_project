package com.mygdx.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MyGdxGame;

public class Sword extends InteractiveTileObject {
    private Player player;
    private Texture texture;
    public Sword(World world, TiledMap map, Rectangle bounds, Player player) {
        super(world, map, bounds, true);
        this.player = player;
        fixture.setUserData(this);
        setCategoryFilter(MyGdxGame.SWORD_BIT);

        texture = new Texture("character/Sword Idle 01.png");


        //setBounds(0, 0, FRAME_WIDTH / MyGdxGame.PPM, FRAME_HEIGHT / MyGdxGame.PPM);
        //setRegion(new TextureRegion(atlas.findRegion("Idle"), 0, 0, FRAME_WIDTH, FRAME_HEIGHT));
    }

    @Override
    public void onCollision() {
        player.setSword(true);
        setCategoryFilter(MyGdxGame.DESTROYED_BIT);
        getCell().setTile(null);
    }
}
