// Practice purpose only, no commercial use
// Author: Yuxiang Zhang
//*Sprites and pictures are retrieved from Internet and modified

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.event.*;

public class main extends GameEngine {
	// Main Function
	public static void main(String args[]) {
		// Create new game and set fps to 50
		createGame(new main(),50);
	}

	//-------------------------------------------------------
	// Game
	//-------------------------------------------------------
	
	enum GameState {Menu, Playing, Gameover};
	GameState state = GameState.Menu;
	
	int menuOption = 0;
	int health;
	double moveSpeed = 3.5;
	
	boolean left, right, fall, faceLeft, faceRight;
	double cameraX;
	double cameraY;
	
	double playerX, playerY, playerW, playerH;
	double playerVX,playerVY;
	double fallVY;	
	
	double backgroundX;
	double backgroundY;
	
	double coinX[] = new double[8];
	double coinY[] = new double[8]; 
	
	double platformX[], platformY[], platformW[], platformH[];
	int numPlatforms, numSpikes;
	
	double spikeX[], spikeY[], spikeW[], spikeH[];
	double destinationX;
	
	double decorationX[], decorationY[];
	
	int walkframe;
	int cloudFrame;
	double cloundVX;
	
	int coinCount;
	int score;
	int temp[] = new int[8];
	
	
	//-------------------------------------------------------
	// Image variables
	//-------------------------------------------------------
	
	// Menu images
	Image logo;
	Image background;
	Image play;
	Image playHighlighted;
	Image exit;
	Image exitHighlighted;
	Image retry;
	Image retryHighlighted;
	
	// Platform Image
	Image floor;
	Image platform1;
	Image platform2;
	Image platform3;
	
	// Spike Image
	Image spike;
	
	// Animation sprites image
	Image playerAnimation;
	
	// Player sub-images
	Image playerStandImage;
	Image playerWalkImage[];
	Image playerAttackImage[];
	Image playerJumpImage;
	
	// HUD images
	Image healthImage;
	Image coinImage;
	
	// decoration images;
	Image arrowImage;
	Image dangerImage;
	Image destinationImage;
	
	// Audios
	AudioClip bgm = loadAudio("sounds/bgm.wav");
	AudioClip jumpSound = loadAudio("sounds/jump.wav");
	AudioClip attackSound = loadAudio("sounds/attack.wav");
	AudioClip coinSound = loadAudio("sounds/coin.wav");
	AudioClip selectSound = loadAudio("sounds/select.wav");
	AudioClip gameoverSound = loadAudio("sounds/gameover.wav");
	AudioClip winSound = loadAudio("sounds/win.wav");

	//-------------------------------------------------------
	// Initialize game settings
	//-------------------------------------------------------
	public void init() {
		// Initialise Window Size
		setWindowSize(800,500);
		intiPlayer();
		intiMap();
		
		// menu images
		logo = loadImage("images/menu/logo.png");
		play = loadImage("images/menu/play.png");
		playHighlighted = loadImage("images/menu/play_highlight.png");
		exit = loadImage("images/menu/exit.png");
		exitHighlighted = loadImage("images/menu/exit_highlight.png");
		retry = loadImage("images/menu/retry.png");
		retryHighlighted = loadImage("images/menu/retry_highlight.png");
		healthImage = loadImage("images/hp.png");
		coinImage = loadImage("images/coin.png");
		
		// Load Map images
		background = loadImage("images/background.png");
		floor = loadImage("images/floor.png");
		platform1 = loadImage("images/platform/platform1.png");
		platform2 = loadImage("images/platform/platform2.png");
		platform3 = loadImage("images/platform/platform3.png");
		spike = loadImage ("images/spike.png");
		
		// Load decoration images
		arrowImage = loadImage("images/decoration/arrow.png");
		dangerImage = loadImage("images/decoration/danger_1.png");;
	   destinationImage = loadImage("images/decoration/destination.png");;
		
		// Player Animation Images
		playerAnimation = loadImage("images/animation.png");
		playerStandImage = subImage(playerAnimation,0,0,48,67);
		playerJumpImage = subImage(playerAnimation,0,134,48,67);
		
		// Player attack animation
		playerAttackImage = new Image[3];
		for (int i=0; i <3; i++) {
			playerAttackImage[i] = subImage(playerAnimation, 48+i*144,134,144,86);
		}
		
		// Player walking animation
		playerWalkImage = new Image[4];
		for (int i=0; i<4; i++) {
			playerWalkImage[i] = subImage(playerAnimation,0+i*48,67,48,67);
		}
		walkframe = 0;				
	}
	
	// Initialize player settings
	public void intiPlayer() {
		for (int i=0; i<8; i++) {
			temp[i] = -1;
		}
		health = 10;
		playerX = 200; playerY = 350;
		playerVX = 0; playerVY = 0;
		fallVY = 0;
		playerW = 35; playerH = 67;
		left = false;right = false; fall = false;
	   faceLeft = false; faceRight = true;
	   cameraX = 0; cameraY = 0;
		coinCount = 0;
		score = 0;
	}
	
	// Initialize map and environment
	public void intiMap() {
		startAudioLoop (bgm);
		backgroundX = 0;
		backgroundY = -400;
		numPlatforms = 100;
		cloudFrame = 0;
		cloundVX = 2.5;
		destinationX = 6500;
	   // Allocate Arrays
		platformX = new double[numPlatforms];
		platformY = new double[numPlatforms];
		platformW = new double[numPlatforms];
		platformH = new double[numPlatforms];
		
		spikeX = new double[numPlatforms];
		spikeY = new double[numPlatforms];
		spikeW = new double[numPlatforms];
		spikeH = new double[numPlatforms];
		
		decorationX = new double[3];
	   decorationY = new double[3];
		
		// Initialize all platforms and spike outside screen
		for(int i=0; i<numPlatforms; i++) {
			platformX[i] = -200;
			spikeX[i] = -200;
		}
		
		// Floor platform
		platformX[0] = 0; platformY[0] = 450; platformW[0] = 7000;platformH[0] = 100;
		
		// Decoration
		decorationX[0]=400;decorationY[0]=375;
		decorationX[1]=4200;decorationY[1]=360;
		decorationX[2]=6200;decorationY[2]=125;
		
		// Platform 1 size (Grass platform)
		for(int i=1; i<50; i++) {
			platformW[i] = 100; platformH[i] = 20;
		}
		// Platform 2 size (Stone platform)
		for(int i=50; i<90; i++) {
			platformW[i] = 30; platformH[i] = 20;
		}
		// Platform 3 size (Cloud platform)
		for(int i=90; i<100; i++){
			platformW[i] = 90; platformH[i] = 20;
		}
		
		// Platform 1 Inti (1~50)
		platformX[1] = 860; platformY[1] = 360; 
		platformX[2] = 1030; platformY[2] = 280;
		platformX[3] = 860; platformY[3] = 200; 
		platformX[4] = 1030; platformY[4] = 120;
		platformX[5] = 2000; platformY[5] = 360; 
		platformX[6] = 1850; platformY[6] = 280; 
		platformX[7] = 1700; platformY[7] = 200; 
		platformX[8] = 1550; platformY[8] = 120; 
		platformX[9] = 1400; platformY[9] = 40;
		platformX[10] = 2620; platformY[10] = 360; 
		platformX[11] = 2800 ; platformY[11] = 280; 
		platformX[12] = 3000; platformY[12] = 280;
		platformX[13] = 3200; platformY[13] = 280; 
		platformX[14] = 3400; platformY[14] = 280;
		platformX[15] = 3600; platformY[15] = 280;
		platformX[16] = 3900; platformY[16] = 360; 
		platformX[17] = 4830; platformY[17] = 350;
		
		// ----Platform 2 Inti (50~90)----
		platformX[50] = 4050; platformY[50] = 270; 
		platformX[51] = 4150; platformY[51] = 200; 
		platformX[52] = 4250; platformY[52] = 130; 
		platformX[53] = 4350; platformY[53] = 60; 
		platformX[54] = 4450; platformY[54] = -10; 
		platformX[55] = 4550; platformY[55] = -80; 
		platformX[56] = 4650; platformY[56] = -150; 
		platformX[57] = 4550; platformY[57] = -220; 
		platformX[58] = 4450; platformY[58] = -290; 
		platformX[59] = 4350; platformY[59] = -370;
		platformX[60] = 4250; platformY[60] = -290;
		platformX[61] = 4450; platformY[61] = -370; 
		platformX[62] = 4550; platformY[62] = -370; 
		platformX[63] = 5600; platformY[63] = -200; 
		platformX[64] = 5500; platformY[64] = -130; 
		platformX[65] = 5400; platformY[65] = -60; 
		platformX[66] = 5300; platformY[66] = 10; 
		platformX[67] = 5200; platformY[67] = 80; 
		platformX[68] = 5100; platformY[68] = 150; 
		platformX[69] = 5000; platformY[69] = 220; 
		
		// ----Platform 3 Cloud Inti (90~100)----
		platformX[90] = 4600; platformY[90] = -370; 
		platformX[91] = 5700; platformY[91] = -370; 
		platformX[92] = 5000; platformY[92] = -220; // bonus
		platformX[93] = 5450; platformY[93] = 350;
		
		// ----Spike Inti----
		for (int i=0; i<numPlatforms; i++) {
			spikeW[i] = 113; spikeH[i]= 35;
		}
		spikeX[0] = 1030; spikeY[0] = 410; 
		spikeX[1] = 2800; spikeY[1] = 410;
		for (int i=2; i<13; i++) {
			spikeX[i] = 4400+(i-2)*100; spikeY[i] = 410;
		}
	}
	
	//-------------------------------------------------------
	// Player Movement and location
	//-------------------------------------------------------

   // Update all player movement and collision
	public void updatePlayer(double dt) {
		walkframe = getFrame(0.8,4);
		
		if(right) {
			playerVX = -moveSpeed;
		} else if(left) {
			playerVX = moveSpeed;
		}
		
		// Move camera when player reaches screen margin
		if((playerX>=500)&&(playerVX <0)) {
			playerX = 500;
			cameraX = -playerVX;
			backgroundX += playerVX *0.2;
		} else
		if((playerX<=200)&&(playerVX>0)) {
			if (backgroundX < 0) {
				playerX = 200;
			   cameraX = -playerVX;
			   backgroundX += playerVX *0.2;
			} else {
				cameraX = 0;
				if(playerX <= 0) {
					playerX = 0;
				}
			}
		} else {
			cameraX=0;
		}
		
		// Update player movement to up and down
		if(playerY <= 100) {
			playerY = 100;
			cameraY = -playerVY;
			backgroundY += 0.5*playerVY;
		} else if ((playerY >=300)&&(platformY[0]>450)){
			playerY = 300;
			cameraY = -playerVY;
			backgroundY += 0.5*playerVY;
		} else {
			cameraY = 0;
		}
	   
		// Apply gravity
		playerVY -= 50 *dt;
		//playerVY -= 1;
		
		// Update Player location
		playerX -= playerVX;
		playerY -= playerVY;
		
		// Land on all platform
		double playerLeft = playerX;
		double playerRight = playerX + playerW;
		double playerTop = playerY;
		double playerBottom = playerY + playerH;
		
		fall = true;
		for (int i = 0; i < numPlatforms; i++){
			double platformLeft = platformX[i];
			double platformRight = platformX[i] + platformW[i];
			double platformTop = platformY[i];
			double platformBottom = platformY[i] + platformH[i];
			if ((playerRight > platformLeft)&&(playerLeft < platformRight)&&(playerBottom<platformBottom)&&(playerBottom>=platformTop-5)&&(playerVY < 0)) {
				// Set gravity to 0 when on platform
				playerVY = 0;
				playerY = platformY[i]-playerH;
				fall = false;
				if(i>=90) {
					if(i%2 == 0) {
					   playerVX = -cloundVX;
					} else {
						playerVX = cloundVX;
					}
				}
			}
		}
	}
	
	// Jump function, called by key listener
	public void playerJump() {
		if (!fall) {
			playAudio(jumpSound);
			playerVY = 15;
			//playerVY = 30;
			fall = true;
		}
	}
	
	// Update camera vision
	public void updateCamera() {
		for (int i=0;i<numPlatforms;i++) {
			platformX[i] -= cameraX;
			platformY[i] -= cameraY;
			spikeX[i] -= cameraX;
			spikeY[i] -= cameraY;
		}
		destinationX -= cameraX;
		for (int i=0; i<3; i++) {
			decorationX[i] -= cameraX;
			decorationY[i] -= cameraY;
		}
	}
	
	// Update clound platform and automatic move
	public void updateCloudPlatform() {
		if(cloudFrame<200) {
		   platformX[90] += cloundVX;
			platformX[91] += -cloundVX;
			platformX[92] += cloundVX;
			platformX[93] += -cloundVX;
			cloudFrame++;
		}
		if(cloudFrame == 200) {
			cloundVX *= -1;
			cloudFrame = 0;
		}
	}
	
	// Update coin and detect collection
	public void updateCoin() {
		if (temp[0] != 0) {coinX[0] = platformX[4]+40; coinY[0] = platformY[4]-25;}
		if (temp[1] != 1) {coinX[1] = platformX[9]+40; coinY[1] = platformY[9]-25;}
		if (temp[2] != 2) {coinX[2] = platformX[12]+40; coinY[2] = platformY[12]-25;}
		if (temp[3] != 3) {coinX[3] = platformX[14]+40; coinY[3] = platformY[14]-25;}
		if (temp[4] != 4)	{coinX[4] = platformX[0]+3250; coinY[4] = platformY[0]-25;}
		if (temp[5] != 5) {coinX[5] = platformX[17]+40; coinY[5] = platformY[17]-25;}
		if (temp[6] != 6)	{coinX[6] = platformX[60]+20; coinY[6] = platformY[60]-25;}
		if (temp[7] != 7) {coinX[7] = platformX[92]+40; coinY[7] = platformY[92]-25;}
		
		for (int i = 0; i < 8; i++) {
		   if ((playerX + playerW > coinX[i])&&(playerX < coinX[i]+ 29)&&(playerY < coinY[i]+ 26)&&(playerY + playerH > coinY[i])){
				playAudio(coinSound);
				coinCount++;
				temp[i] = i;
				coinX[i] = -100;
			}
		}
	}
	
	// Update current score
	public void updateScore() {
		score = coinCount*10 + health * 10;
	}
	
	// A function to return current frame
	public int getFrame(double d, int num_frames) {
		return (int)Math.floor(((getTime()/1000.0 % d) / d) * num_frames);
	}
	
	// function to detect whether game over condition met
	public void gameOver () {
		// When player step on spike
		for (int i = 0; i < 50; i++) {
		   if ((playerX + playerW > spikeX[i])&&(playerX < spikeX[i]+ spikeW[i])&&(playerY < spikeY[i]+ spikeH[i])&&(playerY + playerH > spikeY[i])){
				playAudio(attackSound);
				playerVY = 10;
				//playerVX = 5;
				//left = false;
				//right = false;
				health--;
			}
		}
		if (health <= 0) {
			state = GameState.Gameover;
			stopAudioLoop(bgm);
			playAudio(gameoverSound);
		}
		if (playerX >= destinationX) {
			state = GameState.Gameover;
			stopAudioLoop(bgm);
			playAudio(winSound);
		}
	}
	
	//-------------------------------------------------------
	// Draw/Paint functions
	//-------------------------------------------------------
	
	// Draw health bar
	public void drawHUD() {
		// Health
		for (int i=0; i < health; i++) {
			drawImage(healthImage,20+i*20,20,20,19);
		}
		// Coin collected
		drawImage(coinImage, 20,50,29,26);
		changeColor(black);
		drawText(60, 75, "X "+coinCount,"Arial",30);
	}
	
	public void drawCoin() {
		for(int i = 0; i < 8; i++) {
		   drawImage(coinImage,coinX[i],coinY[i],29,26);
		}
	}
	
	// Draw Player animation
	public void drawPlayer() {
		if(fall) {
			if(faceLeft){
			drawImage(playerJumpImage,playerX,playerY,48,67);
			} else if(faceRight) {
				drawImage(playerJumpImage,playerX+48,playerY,-48,67);
			}
		} else {
			if (left){
		      // Walk to left animation
		      drawImage(playerWalkImage[walkframe],playerX,playerY,48,67);
		   } else if (right){
		    // Walk to right animation
			   drawImage(playerWalkImage[walkframe],playerX+48,playerY,-48,67);
		   } else if(faceLeft){
			   // Face to left standing image
				drawImage(playerStandImage,playerX,playerY,48,67);
		   } else if(faceRight) {
				// Face to right standing image
				drawImage(playerStandImage,playerX+48,playerY,-48,67);
		   }
	   }
	}
	
	// Draw Map includes all platforms and background
	public void drawMap() {
		// draw background image
		drawImage(background,backgroundX,backgroundY);
		
		// draw floor platform
		for (int i=0; i<10; i++) {
			drawImage(floor,platformX[0]+720*i,platformY[0]-10);
		}
		
		// draw other platforms
		for (int i=1; i<50; i++) {
			drawImage(platform1,platformX[i]-20,platformY[i]-25,142,60);
		}
		for(int i = 50; i<90; i++) {
			drawImage(platform2,platformX[i],platformY[i]-25,44,55);
		}
		for(int i = 90; i<100; i++) {
			drawImage(platform3,platformX[i]-5,platformY[i]-5,109,38);
		}
		
		// draw spikes
		for (int i=0; i<100; i++) {
			drawImage(spike, spikeX[i], spikeY[i], 113,39);
		}
		
		//draw decorations
		drawImage(arrowImage,decorationX[0],decorationY[0]);
		drawImage(dangerImage,decorationX[1],decorationY[1]);
		drawImage(destinationImage,decorationX[2],decorationY[2]);
	}
	
	// Paint menu graphics
	public void paintMenu() {
		drawImage(background,0,-400);
		drawImage(logo,170,50);
		drawText(10,15, "Practice purpose only, no commercial use", "Arial", 10);
		drawText(10,30, "Author: Yuxiang Zhang", "Arial", 10);
		// Insruction
		drawText(65,295, "Move: ¡ü ¡ý ¡û ¡ú ", "Arial", 25);
		drawText(65,320, "Jump: SPACE", "Arial", 25);
		drawText(65,345, "Select: Enter", "Arial", 25);
		
		// Play
		if(menuOption == 0) {
			drawImage(playHighlighted, 220, 250);
		} else {
			drawImage(play,            220, 250);
		}
		
		//Exit
		if(menuOption == 1) {
			drawImage(exitHighlighted, 220, 310);
		} else {
			drawImage(exit,            230, 310);
		}
	}
	
	public void paintGameover() {
		drawImage (background,backgroundX,backgroundY);
		drawRectangle(220,140,380,150);
		drawText(230, 200, "YOUR SCORE: "+score,"Arial",40);
		drawText(270, 230, "Coin collected: "+coinCount+"/8","Arial",20);
		if(health <= 0) {
			drawText(280, 120, "GAME OVER","Arial",50);
			drawText(270, 250, "      Health left: ---","Arial",20);
		} else {
			drawText(200, 120, "You reached home!","Arial",50);
		   drawText(270, 250, "Health:       : "+health,"Arial",20);
		}
		
		
		// Retry
		if(menuOption == 0) {
			drawImage(retryHighlighted, 220, 300);
		} else {
			drawImage(retry,            220, 300);
		}
		
		//Exit
		if(menuOption == 1) {
			drawImage(exitHighlighted, 220, 360);
		} else {
			drawImage(exit,            230, 360);
		}
	}
	
	public void paintGame(){
		drawMap();
		drawPlayer();
		drawCoin();
		drawHUD();
	}
	
	//-------------------------------------------------------
	// Engine Provided functions
	//-------------------------------------------------------
	public void update(double dt) {
		if(state == GameState.Playing) {
			updateGame(dt);
		}
	}
	
	public void updateGame(double dt) {
		updateCamera();
		updatePlayer(dt);
		updateCloudPlatform();
		updateCoin();
		gameOver();
		updateScore();
	}
	
	public void paintComponent() {
		changeBackgroundColor(black);
		clearBackground(800, 500);
		if(state == GameState.Menu) {
			paintMenu();
		} else if(state == GameState.Playing) {
			paintGame();
		} else if (state == GameState.Gameover) {
			paintGameover();
		}
	}
	
	//-------------------------------------------------------
	// Keyboard event listener
	//-------------------------------------------------------
	public void keyPressed(KeyEvent event) {
    	if(state == GameState.Menu) {
    		keyPressedMenu(event);
    	} else if (state == GameState.Playing){
    		keyPressedGame(event);
    	} else if(state == GameState.Gameover) {
			keyPressedMenu(event);
		}
    }
	
	public void keyPressedGame(KeyEvent event) {
		// Left Arrow
		if(event.getKeyCode() == KeyEvent.VK_LEFT) {
			left = true;
			faceLeft = true;
			faceRight = false;
		}
		// Right Arrow
		if(event.getKeyCode() == KeyEvent.VK_RIGHT) {
			right = true;
			faceLeft = false;
			faceRight = true;
		}
		// Space
		if(event.getKeyCode() == KeyEvent.VK_SPACE) {
			playerJump();
		}
	}
	
	public void keyPressedMenu(KeyEvent event) {
		//Up Arrow
		if(event.getKeyCode() == KeyEvent.VK_UP) {
			if(menuOption > 0) {
				menuOption--;
				playAudio(selectSound);
			}
		}
		//Down Arrow
		if(event.getKeyCode() == KeyEvent.VK_DOWN) {
			if(menuOption < 1) {
				menuOption++;
				playAudio(selectSound);
			}
		}
		//Enter Key
		if(event.getKeyCode() == KeyEvent.VK_ENTER) {
			if(menuOption == 0) {
				playAudio(coinSound);
				//Start Game
				state = GameState.Playing;
				intiPlayer();
		      intiMap();
			} else if(menuOption == 1) {
				//Option Menu
				System.exit(0);
			} 
		}
	}
	
	public void keyReleased(KeyEvent event) {
		if(state == GameState.Playing) {
		   if(event.getKeyCode() == KeyEvent.VK_LEFT) {
			   left = false;
			   playerVX = 0;
		   }
		   // Right Arrow
		   if(event.getKeyCode() == KeyEvent.VK_RIGHT) {
			   right = false;
			   playerVX = 0;
		   }
		}
	}
}
