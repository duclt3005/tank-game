package tancky;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

class Tank {

	private static final int TANK_WIDTH = 36;
	private static final int TANK_HEIGHT = 36;
	private static final int SPEED = 12;
	private static final int UP_LIMIT = 16;
	
	int tankX;
	int tankY;
	int id;
	int numKill = 0;
	Direction checkTankEdge=null;
	Direction checkTankEdgeOld = null;
	Direction oldPress = null ;

	private boolean buttonUP = false;
	private boolean buttonDown = false;
	private boolean buttonLeft = false;
	private boolean buttonRight = false;
	private boolean isLive = true;
	private Brick brick;


	Direction direction;
	Direction barrelDirection = Direction.D;
	BufferedImage tank_icon = null;

	private TankClient tankClient;

	boolean isLive() {
		return isLive;
	}

	void setLive() {
		this.isLive = false;
	}

	int getId() {
		return this.id;
	}

	int getNumKill() {
		return this.numKill;
	}

	public void setNumKill() {
		this.numKill++;
	}

	private Tank(int x, int y) {
		this.tankX = x;
		this.tankY = y;
	}

	Tank(int x, int y, Direction direction, TankClient tankClient, Brick brick) {
		this(x, y);
		this.direction = direction;
		this.oldPress = this.direction;
		this.tankClient = tankClient;
		this.brick = brick;
		try {
			tank_icon = ImageIO.read(new File("images/tank.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean checkEdge(int x, int y) {
		int[] brickPosX = brick.bricksXPos;
		int[] brickPosY = brick.bricksYPos;
		int[] solidPosX = brick.solidBricksXPos;
		int[] solidPosY = brick.solidBricksYPos;
		int[] greenPosX = brick.greenBricksXPos;
		int[] greenPosY = brick.greenBricksYPos;
		int[] brickON = brick.brickON;

		if(!checkEdgeTank(x,y)) {
			return false;
		}
		for (int i = 0; i < brickPosX.length; i++) {
			if ((brickON[i] == 1)
					&& (new Rectangle(x, y, 36, 36).intersects(new Rectangle(brickPosX[i], brickPosY[i], 36, 36)))) {
				return false;
			}
		}
		for (int i = 0; i < solidPosX.length; i++) {
			if (new Rectangle(x, y, 36, 36).intersects(new Rectangle(solidPosX[i], solidPosY[i], 36, 36))) {
				return false;
			}
		}
		for (int i = 0; i < greenPosX.length; i++) {
			if (new Rectangle(x, y, 36, 36).intersects(new Rectangle(greenPosX[i], greenPosY[i], 36, 36))) {
				return false;
			}
		}

		return x >= 10 && x <= (TankClient.GAME_WIDTH - TANK_WIDTH) && y >= UP_LIMIT
				&& y <= TankClient.GAME_HEIGHT - TANK_HEIGHT;
	}

	public boolean checkEdgeTank(int x, int y) {
		List<Tank> enermyTanks = tankClient.enemyTanks;
		
		for (Tank tankEnermy : enermyTanks) {
			if (tankEnermy.isLive() && new Rectangle(x, y, 36, 36)
					.intersects(new Rectangle(tankEnermy.tankX, tankEnermy.tankY, 36, 36)) && (this.id != tankEnermy.id)) {
				this.checkTankEdge = direction;
				return false;
			}
		}
		return true;
	}
	
	private void move() {
		int x, y;
		switch (direction) {

		case U:
			y = tankY - SPEED;
			if (checkEdge(tankX, y)) {
				tankY = y;
			}
			break;
		case RU:
			x = tankX + SPEED;
			y = tankY - SPEED;
			if (checkEdge(x, y)) {
				tankX = x;
				tankY = y;
			}
			break;
		case R:
			x = tankX + SPEED;
			
			if (checkEdge(x, tankY)) {
				tankX = x;
			}
			break;
		case RD:
			x = tankX + SPEED;
			y = tankY + SPEED;
			if (checkEdge(x, y)) {
				tankX = x;
				tankY = y;
			}
			break;
		case D:
			y = tankY + SPEED;
			if (checkEdge(tankX, y)) {
				tankY = y;
			}
			break;
		case LD:
			x = tankX - SPEED;
			y = tankY + SPEED;
			if (checkEdge(x, y)) {
				tankX = x;
				tankY = y;
			}
			break;
		case L:
			x = tankX - SPEED;
			if (checkEdge(x, tankY)) {
				tankX = x;
			}
			break;
		case LU:
			x = tankX - SPEED;
			y = tankY - SPEED;
			if (checkEdge(x, y)) {
				tankX = x;
				tankY = y;
			}
			break;
		case STOP:
			break;
		}
		if (this.direction != Direction.STOP) {
			this.barrelDirection = this.direction;
		}
	}

	private void location() {
		Direction oldDirection = this.direction;
		int x=this.tankX,y=this.tankY;
		if (buttonUP && !buttonDown && !buttonLeft && !buttonRight) {
			direction = Direction.U;
			getImage(direction);
		} else if (buttonUP && !buttonDown && !buttonLeft) {
			direction = Direction.RU;
		} else if (!buttonUP && !buttonDown && !buttonLeft && buttonRight) {
			direction = Direction.R;
			getImage(direction);
		} else if (!buttonUP && buttonDown && !buttonLeft && buttonRight) {
			direction = Direction.RD;
		} else if (!buttonUP && buttonDown && !buttonLeft) {
			direction = Direction.D;
			getImage(direction);
		} else if (!buttonUP && buttonDown && !buttonRight) {
			direction = Direction.LD;
		} else if (!buttonUP && !buttonDown && buttonLeft && !buttonRight) {
			direction = Direction.L;
			getImage(direction);
		} else if (buttonUP && !buttonDown && !buttonRight) {
			direction = Direction.LU;
		} else if (!buttonUP && !buttonDown && !buttonLeft) {
			direction = Direction.STOP;
			getImage(oldDirection);
		}
		if (this.direction != oldDirection) {
			getImage(oldDirection);
			TankMoveMsg msg = new TankMoveMsg(id, this.tankX, this.tankY, direction, this.barrelDirection, this.oldPress);
			tankClient.netClient.send(msg);
		}
	}

	void draw(Graphics graphics) {
		if (!this.isLive) {
			return;
		}
		getImage(direction);
		graphics.drawImage(tank_icon, this.tankX, this.tankY, TANK_WIDTH, TANK_HEIGHT, null);

		move();
	}

	public void getImage(Direction oldDirection) {
		String color = "";
		String direct = "";
		TankColorEnum tankColor = TankColorEnum.values()[id];
		switch (tankColor) {
		case RED:
			color = "red";
			break;
		case GREEN:
			color = "green";
			break;
		case BLUE:
			color = "blue";
			break;
		case YELLOW:
			color = "yellow";
			break;
		case ORANGE:
			color = "orange";
			break;
		case PURPLE:
			color = "purple";
			break;
		case SKY:
			color = "sky";
			break;
		case GRAY:
			color = "gray";
			break;
		default:
			break;
		}
		switch (oldDirection) {
		case L:
			try {
				tank_icon = ImageIO.read(new File("images/tank_" + color + "_left.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case U:
			try {
				tank_icon = ImageIO.read(new File("images/tank_" + color + "_up.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case R:
			try {
				tank_icon = ImageIO.read(new File("images/tank_" + color + "_right.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case D:
			try {
				tank_icon = ImageIO.read(new File("images/tank_" + color + "_down.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		default:
			if(oldPress !=null) {
				switch(oldPress) {
				case U: 
					direct = "up";
					break;
				case L: 
					direct = "left";
					break;
				case D: 
					direct = "down";
					break;
				case R: 
					direct = "right";
					break;
				default:
					break;
				}
			}
			if(color=="" && direct=="") {
				try {
					tank_icon = ImageIO.read(new File("images/tank.png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(color!="" && direct=="") {
				try {
					tank_icon = ImageIO.read(new File("images/tank_" + color + "_down.png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					tank_icon = ImageIO.read(new File("images/tank_" + color + "_"+direct+".png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}

	void keyPressed(KeyEvent keyEvent) {
		int key = keyEvent.getKeyCode();
		if(this.checkTankEdge !=null) {
			switch(this.checkTankEdge) {
				case U:
					if(key != KeyEvent.VK_W  ) {
						this.checkTankEdgeOld= this.checkTankEdge;
						this.checkTankEdge=null;
					}
					break;
				case L:
					if(key != KeyEvent.VK_A ) {
						this.checkTankEdgeOld= this.checkTankEdge;
						this.checkTankEdge=null;
					}
					break;
				case D:
					if(key != KeyEvent.VK_S ) {
						this.checkTankEdgeOld= this.checkTankEdge;
						this.checkTankEdge=null;
					}
					break;
				case R:
					if(key != KeyEvent.VK_D ) {
						this.checkTankEdgeOld= this.checkTankEdge;
						this.checkTankEdge=null;
					}
					break;
				default:
						break;
			}
		}
		switch (key) {
			case KeyEvent.VK_W:
				if(this.checkTankEdge!=Direction.U) {
					buttonUP = true;
					oldPress = Direction.U;
				}
				else {
					buttonUP = false;
				}
				break;
			case KeyEvent.VK_S:
				if(this.checkTankEdge!=Direction.D) {
					buttonDown = true;
					oldPress = Direction.D;
				}
				else {
					buttonDown = false;
				}
				break;
			case KeyEvent.VK_A:
				if(this.checkTankEdge!=Direction.L) {
					buttonLeft = true;
					oldPress = Direction.L;
				}
				else {
					buttonLeft = false;
				}
				break;
			case KeyEvent.VK_D:
				if(this.checkTankEdge!=Direction.R) {
					buttonRight = true;
					oldPress = Direction.R;
				}
				else {
					buttonRight = false;
				}
				break;
		}
		location();
	}

	private void fire() {
		if (!this.isLive) {
			return;
		}

		int x = this.tankX + Tank.TANK_WIDTH / 2 - Missile.WIDTH / 2;
		int y = this.tankY + Tank.TANK_HEIGHT / 2 - Missile.HEIGHT / 2;

		Missile missile = new Missile(id, x, y, this.barrelDirection, this.tankClient);
		tankClient.missiles.add(missile);
		tankClient.netClient.send(new MissileNewMsg(missile));
	}

	void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_SPACE:
			fire();
			break;
		case KeyEvent.VK_W:
			oldPress = Direction.U;
			buttonUP = false;
			break;
		case KeyEvent.VK_S:
			oldPress = Direction.D;
			buttonDown = false;
			break;
		case KeyEvent.VK_A:
			oldPress = Direction.L;
			buttonLeft = false;
			break;
		case KeyEvent.VK_D:
			oldPress = Direction.R;
			buttonRight = false;
			break;
		}
		location();
	}
	
	Rectangle getRect() {
		return new Rectangle(tankX, tankY, TANK_WIDTH, TANK_HEIGHT);
	}
}
