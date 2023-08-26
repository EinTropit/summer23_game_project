package com.mygdx.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Screens.PlayScreen;

public class Sword extends InteractiveTileObject {
    private Player player;

    public Sword(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds, true);
        this.player = screen.getPlayer();
        fixture.setUserData(this);
        setCategoryFilter(MyGdxGame.SWORD_BIT);

    }

    @Override
    public void onCollision() {
        player.setSword(true);
        setCategoryFilter(MyGdxGame.DESTROYED_BIT);
        getCell().setTile(null);
        setToDestroy = true;
    }
}
