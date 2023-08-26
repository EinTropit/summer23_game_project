package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Screens.PlayScreen;

public class MyGdxGame extends Game {
	public static final int V_WIDTH = 500;
	public static final int V_HEIGHT = 400;
	public static final Vector2 MAP_SIZE = new Vector2(30 * 32, 20 * 32);
	public static final float PPM = 400;

	public static final short GROUND_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short SWORD_BIT = 4;
	public static final short PLAYER_SWORD_BIT = 8;
	public static final short DESTROYED_BIT = 16;
	public static final short OBJECT_BIT = 32;
	public static final short ENEMY_BIT = 64;

	public SpriteBatch batch;

	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
