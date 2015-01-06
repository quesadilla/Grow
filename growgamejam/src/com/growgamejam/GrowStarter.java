package com.growgamejam;

import com.badlogic.gdx.backends.jogl.JoglApplication;

public class GrowStarter {
	public static void main(String[] args)
	{
		new JoglApplication(new Game(), 
							"Light", 800, 600, true);
	}
}