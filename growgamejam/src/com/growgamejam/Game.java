package com.growgamejam;

//import java.util.ArrayList;
//import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Game implements ApplicationListener {

	private STATE antagState;
	
	public static final int WALK = 1; // speed of player
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 600;
	public static final int COLLECTIBLE_X = 700;
	public static final int COLLECTIBLE_Y = 500;
	public static final int RADIUS = 50;
	
	
	
	// speed of protagonist walking
	int currentSpeed;
	int currentFrame;
	int randomX;
	int randomY;
	int antagX;
	int antagY;
	
	int antagObjX;
	int antagObjY;
	
	int circleSize;
	
	// keep track of time
	float walkTimeUp;
	float walkTimeDown;
	float walkTimeLeft;
	float walkTimeRight;
	float collectTime;
	float hitTime;
	float enemyTime;
	float circleTime;
	float stunTime;
	float flashTime;
	
	boolean gamePaused;
	boolean flashPressed;
	
	// state of enemy
	enum STATE {THIEF, ATTACKER, STUNNED};
	
	// in charge of drawing everything
	SpriteBatch batch;
	
	// keeps track of keyboard keys
//	InputProcessor processor;
	
	// game over text
	BitmapFont font;
	
	// pictures to be drawn
	TextureRegion background;
	TextureRegion sanic;
	TextureRegion glowy;
	TextureRegion enemy;
	TextureRegion ring;
	
	// protagonist animations
	Array<AtlasRegion> protagUp;
	Array<AtlasRegion> protagDown;
	Array<AtlasRegion> protagLeft;
	Array<AtlasRegion> protagRight;
	
	Array<AtlasRegion> protagSE;
	Array<AtlasRegion> protagSW;
	Array<AtlasRegion> protagNE;
	Array<AtlasRegion> protagNW;
	
	// what stores the pictures
	TextureAtlas demoAtlas;
	TextureAtlas enemyAtlas;
	
	TextureAtlas protagAtlas;
	
	// what the picture is attached to
	Rectangle protagonist;
	Rectangle collectible;
	Rectangle antagonist;
	Rectangle lightRingVertical;
	Rectangle lightRingHorizontal;
	
	Circle flash;
	
	@Override
	public void create() {
				
		// player is standing
		currentSpeed = 0;
		
		// set font
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.setScale(5, 5);
		
		antagState = STATE.STUNNED;
		
		flashPressed = false;
		
		circleSize = RADIUS * 2;
		stunTime = 0;
		
		// locate the pack with all the pictures
		demoAtlas = new TextureAtlas(Gdx.files.internal("assets/demostuff.pack"));
		enemyAtlas = new TextureAtlas(Gdx.files.internal("assets/EnemyAndCircle.pack"));
		protagAtlas = new TextureAtlas(Gdx.files.internal("assets/protagonist.pack"));
		
		// find the pictures in the atlas
		background = demoAtlas.findRegion("growbackground");
		sanic = demoAtlas.findRegion("growmainchar");
		glowy = demoAtlas.findRegion("growglow");
		
		enemy = enemyAtlas.findRegion("growenemy");
		ring = enemyAtlas.findRegion("growcircle");
		
		// protagonist movement
		protagUp = protagAtlas.findRegions("walkup");
		protagDown = protagAtlas.findRegions("walkdown");
		protagLeft = protagAtlas.findRegions("sidewalkleft");
		protagRight = protagAtlas.findRegions("sidewalkright");
		
		protagSE = protagAtlas.findRegions("sewalk");
		protagSW = protagAtlas.findRegions("swwalk");
		protagNE = protagAtlas.findRegions("newalk");
		protagNW = protagAtlas.findRegions("nwwalk");
		
		// randomize collectible location
		randomX = MathUtils.random(COLLECTIBLE_X);
		randomY = MathUtils.random(COLLECTIBLE_Y);
		antagX = MathUtils.random(COLLECTIBLE_X);
		antagY = MathUtils.random(COLLECTIBLE_Y);
		
		// start up the drawing class
		batch = new SpriteBatch();
		
		// create the protag rectangle
		protagonist = new Rectangle(100, 100, 100, 100);
		collectible = new Rectangle(randomX, randomY, 50, 50);
		antagonist = new Rectangle(antagX, antagY, 100, 100);
		
		lightRingVertical = new Rectangle(protagonist.x, protagonist.y, 50, 100);
		lightRingHorizontal = new Rectangle(protagonist.x, protagonist.y, 100, 50);
		
		flash = new Circle(100, 100, circleSize);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		
		// keep track of player's movement
		walkTimeUp += Gdx.graphics.getDeltaTime();
		walkTimeDown += Gdx.graphics.getDeltaTime();
		walkTimeLeft += Gdx.graphics.getDeltaTime();
		walkTimeRight += Gdx.graphics.getDeltaTime();
		hitTime += Gdx.graphics.getDeltaTime();
		enemyTime += Gdx.graphics.getDeltaTime();
		
		// time taken to collect next collectible
		collectTime += Gdx.graphics.getDeltaTime();
		circleTime += Gdx.graphics.getDeltaTime();
		flashTime += Gdx.graphics.getDeltaTime();
		
		if(antagState == STATE.STUNNED)
		{
			stunTime += Gdx.graphics.getDeltaTime();
		}
		
		gamePaused = false;
		
		// Shrink circle over time 
		if(circleTime > 1)
		{
			if(circleSize > 0)
			{
				circleSize -= 5;
				lightRingVertical.height -= 5;
				lightRingHorizontal.width -= 5;
				lightRingVertical.y += 2.5;
				lightRingHorizontal.x += 2.5;
				circleTime = 0;
			}
		}
		
		// show the entire room
		if(Gdx.input.isKeyPressed(Keys.SPACE))
		{
//			flashPressed = true;
			if(flash.radius < 350 && flashTime > .02)
			{
				flash.radius += 10;
				flash.x -= 10;
				flash.y -= 10;
				flashTime = 0;
			}
		}
		
		else
		{
			flash.radius = RADIUS;
			flash.x = protagonist.x;
			flash.y = protagonist.y;
		}
		
		// Pause game (NOT WORKING AT THE MOMENT)
		if(Gdx.input.isKeyPressed(Keys.P))
		{
			if(gamePaused == false)
			{
				gamePaused = true;
			}
			else
			{
				gamePaused = false;
			}
		}
		
		// move player up
		if(Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W))
		{
			currentSpeed = WALK;
			
			// top boundary
			if(protagonist.y < (SCREEN_HEIGHT - protagonist.height) && walkTimeUp > .005)
			{
				currentFrame++;
				lightRingVertical.y += currentSpeed;
				lightRingHorizontal.y += currentSpeed;
				flash.y += currentSpeed;
				protagonist.y += currentSpeed;
				walkTimeUp = 0;
			}
			
			if(currentFrame >= protagUp.size)
			{
				currentFrame = 0;
			}
		}
		
		// move player down
		if(Gdx.input.isKeyPressed(Keys.DOWN)|| Gdx.input.isKeyPressed(Keys.S))
		{	
			// bottom boundary
			if(protagonist.y > 0 && walkTimeDown > .005)
			{
				currentFrame++;
				lightRingVertical.y -= currentSpeed;
				lightRingHorizontal.y -= currentSpeed;
				flash.y -= currentSpeed;
				protagonist.y -= currentSpeed;
				walkTimeDown = 0;
			}
			
			if(currentFrame >= protagDown.size)
			{
				currentFrame = 0;
			}
		}
		
		// move player left
		if(Gdx.input.isKeyPressed(Keys.LEFT)|| Gdx.input.isKeyPressed(Keys.A))
		{
			
			currentSpeed = WALK;
			
			// left boundary
			if(protagonist.x > 0 && walkTimeLeft > .005)
			{
				currentFrame++;
				lightRingVertical.x -= currentSpeed;
				lightRingHorizontal.x -= currentSpeed;
				flash.x -= currentSpeed;
				protagonist.x -= currentSpeed;
				walkTimeLeft = 0;
			}
			
			if(currentFrame >= protagLeft.size)
			{
				currentFrame = 0;
			}
		}
		
		// move player right
		if(Gdx.input.isKeyPressed(Keys.RIGHT)|| Gdx.input.isKeyPressed(Keys.D) )
		{
			currentSpeed = WALK;
			
			// right boundary 
			if((protagonist.x < SCREEN_WIDTH - protagonist.width) && walkTimeRight > .005)
			{
				currentFrame++;
				lightRingVertical.x += currentSpeed;
				lightRingHorizontal.x += currentSpeed;
				flash.x += currentSpeed;
				protagonist.x += currentSpeed;
				walkTimeRight = 0;
			}
			
			if(currentFrame >= protagRight.size)
			{
				currentFrame = 0;
			}
		}
		
		
		
		
		// enemy behavior when stealing items
		if(antagState == STATE.THIEF)
		{
			// find difference between antagonist and collectible
			antagObjX = (int)(antagonist.x - collectible.x - 1);
			antagObjY = (int)(antagonist.y - collectible.y - 1);
			
			// move toward it
			if(enemyTime > .01)
			{
				// if antagonist is bottom-left of the object
				if(antagObjX < 0 && antagObjY < 0)
				{
					antagonist.x += 1;
					antagonist.y += 1;
				}
				
				// if antagonist is top-left of the object
				else if(antagObjX < 0 && antagObjY > 0)
				{
					antagonist.x += 1;
					antagonist.y -= 1;
				}
			
				// if antagonist is bottom-right of the object
				else if(antagObjX > 0 && antagObjY < 0)
				{
					antagonist.x -= 1;
					antagonist.y += 1;
				}
				
				// if antagonist is top-right of the object
				else if(antagObjX > 0 && antagObjY > 0)
				{
					antagonist.x -= 1;
					antagonist.y -= 1;
				}
				
				// if antagonist is left of object
				else if(antagObjX < 0)
				{
					antagonist.x += 1;
				}
				
				// if antagonist is right of object
				else if(antagObjX > 0)
				{
					antagonist.x -= 1;
				}
				
				// if antagonist is below the object
				else if(antagObjY < 0)
				{
					antagonist.y += 1;
				}
				
				// if antagonist is above the object
				else if(antagObjY > 0)
				{
					antagonist.y -= 1;
				}
				
				enemyTime = 0;
			}
		}
		
		// if enemy attacks the player
		if(antagState == STATE.ATTACKER)
		{
			// find difference between antagonist and player
						antagObjX = (int)(antagonist.x - protagonist.x - 1);
						antagObjY = (int)(antagonist.y - protagonist.y - 1);
						
						// move toward it
						if(enemyTime > .01)
						{
							// if antagonist is bottom-left of the object
							if(antagObjX < 0 && antagObjY < 0)
							{
								antagonist.x += 1;
								antagonist.y += 1;
							}
							
							// if antagonist is top-left of the object
							else if(antagObjX < 0 && antagObjY > 0)
							{
								antagonist.x += 1;
								antagonist.y -= 1;
							}
						
							// if antagonist is bottom-right of the object
							else if(antagObjX > 0 && antagObjY < 0)
							{
								antagonist.x -= 1;
								antagonist.y += 1;
							}
							
							// if antagonist is top-right of the object
							else if(antagObjX > 0 && antagObjY > 0)
							{
								antagonist.x -= 1;
								antagonist.y -= 1;
							}
							
							// if antagonist is left of object
							else if(antagObjX < 0)
							{
								antagonist.x += 1;
							}
							
							// if antagonist is right of object
							else if(antagObjX > 0)
							{
								antagonist.x -= 1;
							}
							
							// if antagonist is below the object
							else if(antagObjY < 0)
							{
								antagonist.y += 1;
							}
							
							// if antagonist is above the object
							else if(antagObjY > 0)
							{
								antagonist.y -= 1;
							}
							
							enemyTime = 0;
						}
			
		}
		
		if(antagState == STATE.STUNNED && stunTime > 2)
		{
			antagState = STATE.THIEF;
			stunTime = 0;
		}
	
//		Gdx.gl.glClearColor(1, 1, 1, 1);
//		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		

		batch.begin();
		
		batch.draw(background, 0, 0, 800, 600);
		
		// if the player hasn't collected it, draw the collectible
		if(!(protagonist.contains(collectible)))
		{
			batch.draw(glowy, randomX, randomY, 100, 100);
		}
		
		// randomly generate another collectible and increase light circle
		else if(protagonist.contains(collectible))
		{
			circleSize += 20;
			lightRingVertical.height += 20;
			lightRingHorizontal.width += 20;
			lightRingVertical.y -= 10;
			lightRingHorizontal.x -= 10;
			randomX = MathUtils.random(COLLECTIBLE_X);
			randomY = MathUtils.random(COLLECTIBLE_Y);
			collectible.x = randomX;
			collectible.y = randomY;
			batch.draw(glowy,  randomX, randomY, 100, 100);
		}
		
		// if the enemy steals items
		if(antagObjX == 0 && antagObjX == 0 && antagState == STATE.THIEF)
		{
			randomX = MathUtils.random(COLLECTIBLE_X);
			randomY = MathUtils.random(COLLECTIBLE_Y);
			collectible.x = randomX;
			collectible.y = randomY;
			batch.draw(glowy,  randomX, randomY, 100, 100);
			
		}
		
		// antagonist hurts protagonist if they hit each other
		if(antagonist.overlaps(protagonist) && hitTime > 1)
		{
			if(circleSize > 0)
			{
				circleSize -= 10;
				lightRingVertical.height -= 10;
				lightRingHorizontal.width -= 10;
				lightRingVertical.y += 5;
				lightRingHorizontal.x += 5;
				hitTime = 0;
			}
		}
		
		// draw protagonist and antagonist

		if(circleSize == 0)
		{
			font.draw(batch, "UR 2 SLOW", 100, 100);
		}
		
		// ring of light
		batch.draw(ring, lightRingHorizontal.x, lightRingVertical.y, circleSize, circleSize);
		
		// northwest
		if((Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) && 
				(Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)))
		{
			batch.draw(protagNW.get(currentFrame), protagonist.x, protagonist.y, 100, 100);
		}
		
		// southwest
		else if((Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) && 
				(Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)))
		{
			batch.draw(protagSW.get(currentFrame), protagonist.x, protagonist.y, 100, 100);
		}
		
		// northeast
		else if((Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D))&& 
				(Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)))
		{
			batch.draw(protagNE.get(currentFrame), protagonist.x, protagonist.y, 100, 100);
		}
		
		// northwest
		else if((Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) && 
				(Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)))
		{
			batch.draw(protagSE.get(currentFrame), protagonist.x, protagonist.y, 100, 100);
		}
		
		else if(Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S))
		{
			batch.draw(protagDown.get(currentFrame), protagonist.x, protagonist.y, 100, 100);
		}
		
		// draw the protagonist moving up, down, left, or right
		else if(Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W))
		{
			batch.draw(protagUp.get(currentFrame), protagonist.x, protagonist.y, 100, 100);
		}
		else if(Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A))
		{
			batch.draw(protagLeft.get(currentFrame), protagonist.x, protagonist.y, 100, 100);
		}
		else if(Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D))
		{
			batch.draw(protagRight.get(currentFrame), protagonist.x, protagonist.y, 100, 100);
		}
		else
		{
			batch.draw(protagDown.get(0), protagonist.x, protagonist.y, 100, 100);
		}
		
		if(Gdx.input.isKeyPressed(Keys.SPACE))
		{
			batch.draw(ring, flash.x, flash.y, flash.radius * 2, flash.radius * 2);
		}
		
		
		// enemy
		batch.draw(enemy, antagonist.x, antagonist.y, 100, 100);
		
		batch.end();
	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

}
