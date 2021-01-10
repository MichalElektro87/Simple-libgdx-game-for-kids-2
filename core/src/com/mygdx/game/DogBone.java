package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class DogBone extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture dogLeftImg, dogRightImg, landscapeImg, dogBoneImg, dogBoneScoreImg;
	private Rectangle dogRect;
	private BitmapFont font;
	private Sound fallSound;
	private Music backgroundMusic;
	private Array<Rectangle> dogBonesRect;
	private long lastBoneTime;
	private int boneCounter = 0;
	private Boolean left = false, right = true;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		dogLeftImg = new Texture("dogLeft.png");
		dogRightImg = new Texture("dogRight.png");
		landscapeImg = new Texture("landscape.png");
		dogBoneImg = new Texture("bone.png");
		dogBoneScoreImg = new Texture("boneScore.png");
		font = new BitmapFont();
		fallSound = Gdx.audio.newSound(Gdx.files.internal("fall.wav"));
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("backgroundMusic.mp3"));
		backgroundMusic.play();
		backgroundMusic.setLooping(true);

		dogRect = new Rectangle();
		dogBonesRect = new Array<Rectangle>();

		dogRect.width = dogLeftImg.getWidth();
		dogRect.height = dogLeftImg.getHeight();
		dogRect.x = 0;
		dogRect.y = 0;

		spawnBone();
	}

	private void spawnBone() {
		Rectangle dogBoneRect = new Rectangle();
		dogBoneRect.x = MathUtils.random(0, 800 - dogBoneImg.getWidth());
		dogBoneRect.y = 600;
		dogBoneRect.width = dogBoneImg.getWidth();
		dogBoneRect.height = dogBoneImg.getHeight();
		dogBonesRect.add(dogBoneRect);
		lastBoneTime = TimeUtils.nanoTime();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			dogRect.x += 10;
			right = true;
			left = false;
		}

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			dogRect.x -= 10;
			right = false;
			left = true;
		}

		if (dogRect.x < 0)
			dogRect.x+=10;

		if (dogRect.x + dogRect.getWidth() > 800)
			dogRect.x-=10;

		batch.begin();
		batch.draw(landscapeImg, 0, 0);
		batch.draw(dogBoneScoreImg, 800-dogBoneScoreImg.getWidth()-50, 570);
		font.draw(batch, "" + boneCounter, 770, 585);


		for(Rectangle bone: dogBonesRect) {
			batch.draw(dogBoneImg, bone.x, bone.y);
		}

		if (right)
			batch.draw(dogRightImg, dogRect.x, dogRect.y);

		if (left)
			batch.draw(dogLeftImg, dogRect.x, dogRect.y);

		batch.end();

		if(TimeUtils.nanoTime() - lastBoneTime > 1000000000) spawnBone();

		for (Iterator<Rectangle> iter = dogBonesRect.iterator(); iter.hasNext(); ) {
			Rectangle dogBoneRect = iter.next();
			dogBoneRect.y -= 200 * Gdx.graphics.getDeltaTime();
			if(dogBoneRect.y + dogBoneImg.getHeight() < 0) iter.remove();
			if(dogBoneRect.overlaps(dogRect)) {
				fallSound.play();
				boneCounter++;
				iter.remove();
			}
		}

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		dogLeftImg.dispose();
		dogRightImg.dispose();
		dogBoneImg.dispose();
		landscapeImg.dispose();
	}
}
