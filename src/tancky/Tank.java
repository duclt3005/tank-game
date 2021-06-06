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
	
	Direction oldPress = null ;

	private boolean buttonUP = false;
	private boolean buttonDown = false;
	private boolean buttonLeft = false;
	private boolean buttonRight = false;
	private boolean isLive = true;
	private Brick brick;
	private int oldPosX=0, oldPosY=0;


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
		this.tankClient = tankClient;
		this.brick = brick;
		try {
			tank_icon = ImageIO.read(new File("images/tank.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void initTank() {
		String color = "";
		TankColorEnum tankColor = TankColorEnum.values()[id];
		switch (tankColor) {
//         case WHITE:
// 			graphics.setColor(Color.WHITE);
// 			break;
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
		try {
			tank_icon = ImageIO.read(new File("images/tank_" + color + "_left.png"));
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
		
//		if(enermyTanks.size()==0) {
//			return true;
//		}
		for (Tank tankEnermy : enermyTanks) {
			if (tankEnermy.isLive() && new Rectangle(x, y, 36, 36)
					.intersects(new Rectangle(tankEnermy.tankX, tankEnermy.tankY, 36, 36)) && (this.id != tankEnermy.id)) {
//				System.out.println("===enermy:"+tankEnermy.tankX+"-"+tankEnermy.tankY);
//				System.out.println("===tank:"+this.tankX+"-"+this.tankY);
//				System.out.println("===pos:"+x+"-"+y);
//				System.out.println("====================current= "+this.id+"~~~~~enermy= "+tankEnermy.id);
				this.checkTankEdge = direction;
				this.oldPosX= tankX;
				this.oldPosY = tankY;
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
//		if(this.checkTankEdge!=null) {
//			switch(this.checkTankEdge) {
//			case U: 
//				buttonUP = false;
//				break;
//			case D: 
//				buttonDown = false;
//				break;
//			case L: 
//				buttonLeft = false;
//				break;
//			case R: 
//				buttonRight = false;
//				break;
//			default: 
//				break;
//			}
//		}
		if (buttonUP && !buttonDown && !buttonLeft && !buttonRight) {
			direction = Direction.U;
//			x= this.tankX-SPEED;
			getImage(direction);
		} else if (buttonUP && !buttonDown && !buttonLeft) {
			direction = Direction.RU;
//			x = this.tankX - SPEED;
//			y = this.tankY + SPEED;
		} else if (!buttonUP && !buttonDown && !buttonLeft && buttonRight) {
			direction = Direction.R;
//			x = this.tankX - SPEED;
			getImage(direction);
		} else if (!buttonUP && buttonDown && !buttonLeft && buttonRight) {
			direction = Direction.RD;
//			x = this.tankX - SPEED;
//			y = this.tankY - SPEED;
		} else if (!buttonUP && buttonDown && !buttonLeft) {
			direction = Direction.D;
//			y = this.tankY - SPEED;
			getImage(direction);
		} else if (!buttonUP && buttonDown && !buttonRight) {
			direction = Direction.LD;
//			x = tankX + SPEED;
//			y = tankY - SPEED;
		} else if (!buttonUP && !buttonDown && buttonLeft && !buttonRight) {
			direction = Direction.L;
//			x = tankX + SPEED;
			getImage(direction);
		} else if (buttonUP && !buttonDown && !buttonRight) {
			direction = Direction.LU;
//			x = tankX + SPEED;
//			y = tankY + SPEED;
		} else if (!buttonUP && !buttonDown && !buttonLeft) {
			direction = Direction.STOP;
//			this.checkTankEdge= null;
			getImage(oldDirection);
		}
		if (this.direction != oldDirection) {
			if(this.checkTankEdge!=null && this.oldPress!=null && this.checkTankEdge == this.oldPress && this.oldPress == this.direction) {
//				this.checkTankEdge=null;
				return;
			}
//			if( this.oldPress != this.direction ) {
//				this.checkTankEdge=null;
//			}
				getImage(oldDirection);
				TankMoveMsg msg = new TankMoveMsg(id, this.tankX, this.tankY, direction, this.barrelDirection);
				tankClient.netClient.send(msg);
//			}
		}
//		else {
//			this.checkTankEdge=null;
//		}
	}

	void draw(Graphics graphics) {
//		this.checkTankEdge=null;
		if (!this.isLive) {
			return;
		}
		TankColorEnum tankColor = TankColorEnum.values()[id];
		switch (tankColor) {
		case WHITE:
			graphics.setColor(Color.WHITE);
			break;
		case RED:
			graphics.setColor(new Color(255, 105, 105));
			break;
		case GREEN:
			graphics.setColor(new Color(76, 209, 55));
			break;
		case BLUE:
			graphics.setColor(new Color(0, 168, 255));
			break;
		case YELLOW:
			graphics.setColor(new Color(255, 242, 0));
			break;
		case ORANGE:
			graphics.setColor(new Color(255, 159, 26));
			break;
		case PURPLE:
			graphics.setColor(new Color(156, 136, 255));
			break;
		case SKY:
			graphics.setColor(new Color(103, 230, 220));
			break;
		case GRAY:
			graphics.setColor(new Color(127, 140, 141));
			break;
		default:
			break;
		}

//        graphics.fillRect(this.tankX, this.tankY, TANK_WIDTH, TANK_HEIGHT);
		getImage(direction);
		graphics.drawImage(tank_icon, this.tankX, this.tankY, TANK_WIDTH, TANK_HEIGHT, null);

//		int x1 = this.tankX + TANK_WIDTH / 2;
//		int y1 = this.tankY + TANK_HEIGHT / 2;
//		int x2 = countX2();
//		int y2 = countY2();

//		Graphics2D g = (Graphics2D) graphics;
//        g.setStroke(new BasicStroke(10));
//        g.drawLine(x1, y1, x2, y2);
//        graphics.setColor(graphics.getColor());
//        graphics.setFont (new Font ("Courier New", Font.BOLD, 16));
//        graphics.setColor(Color.BLACK);
//        graphics.drawString(""+this.id, this.tankX+12, this.tankY + 24);
		
		move();
	}

	private void getImage(Direction oldDirection) {
		String color = "";
		TankColorEnum tankColor = TankColorEnum.values()[id];
		switch (tankColor) {
//        case WHITE:
// 			graphics.setColor(Color.WHITE);
// 			break;
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case U:
			try {
				tank_icon = ImageIO.read(new File("images/tank_" + color + "_up.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R:
			try {
				tank_icon = ImageIO.read(new File("images/tank_" + color + "_right.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case D:
			try {
				tank_icon = ImageIO.read(new File("images/tank_" + color + "_down.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
//			try {
//				tank_icon = ImageIO.read(new File("images/tank_" + color + "_down.png"));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			break;
		}
	}

	private int countX2() {
		double x = 0;
		switch (barrelDirection) {
		case U:
			x = this.tankX + TANK_WIDTH / 2;
			break;
		case RU:
			x = this.tankX + TANK_WIDTH * 1.5 - 16;
			break;
		case R:
			x = this.tankX + TANK_WIDTH * 1.5 - 12;
			break;
		case RD:
			x = this.tankX + TANK_WIDTH * 1.5 - 16;
			break;
		case D:
			x = this.tankX + TANK_WIDTH / 2;
			break;
		case LD:
			x = this.tankX - TANK_WIDTH / 2 + 16;
			break;
		case L:
			x = this.tankX - TANK_WIDTH / 2 + 10;
			break;
		case LU:
			x = this.tankX - TANK_WIDTH / 2 + 16;
			break;
		default:
			break;
		}
		return (int) x;
	}

	private int countY2() {
		double y = 0;
		switch (barrelDirection) {
		case U:
			y = this.tankY - TANK_HEIGHT / 2 + 12;
			break;
		case RU:
			y = this.tankY - TANK_HEIGHT / 2 + 16;
			break;
		case R:
			y = this.tankY + TANK_HEIGHT / 2;
			break;
		case RD:
			y = this.tankY + TANK_HEIGHT * 1.5 - 16;
			break;
		case D:
			y = this.tankY + TANK_HEIGHT * 1.5 - 12;
			break;
		case LD:
			y = this.tankY + TANK_HEIGHT * 1.5 - 16;
			break;
		case L:
			y = this.tankY + TANK_WIDTH / 2;
			break;
		case LU:
			y = this.tankY - TANK_HEIGHT / 2 + 16;
			break;
		default:
			break;
		}
		return (int) y;
	}

	void keyPressed(KeyEvent keyEvent) {
		int key = keyEvent.getKeyCode();
		
		if(this.checkTankEdge !=null) {
			switch(this.checkTankEdge) {
				case U:
					if(key != KeyEvent.VK_W  ) {
						this.checkTankEdge=null;
					}
					break;
				case L:
					if(key != KeyEvent.VK_A ) {
						this.checkTankEdge=null;
					}
					break;
				case D:
					if(key != KeyEvent.VK_S ) {
						this.checkTankEdge=null;
					}
					break;
				case R:
					if(key != KeyEvent.VK_D ) {
						this.checkTankEdge=null;
					}
					break;
				default:
						break;
			}
		}
		System.out.println(tankX+"++++"+tankY+"==="+oldPosX+"++++"+oldPosY);
		switch (key) {
		case KeyEvent.VK_W:
			if(this.checkTankEdge!=Direction.U) {
				buttonUP = true;
//				if(this.oldPosX!=0 && this.oldPosY != 0 && Math.abs(this.oldPosY-this.tankY)>60 ) {
//					this.checkTankEdge = Direction.U;
//					buttonUP = false;
//				}
			}
			else {
				buttonUP = false;
			}
			break;
		case KeyEvent.VK_S:
			if(this.checkTankEdge!=Direction.D) {
				buttonDown = true;
//				if(this.oldPosX!=0 && this.oldPosY != 0 && Math.abs(this.oldPosY-this.tankY)>60) {
//					this.checkTankEdge = Direction.D;
//					buttonDown = false;
//				}
			}
			else {
				buttonDown = false;
			}
			break;
		case KeyEvent.VK_A:
			if(this.checkTankEdge!=Direction.L) {
				buttonLeft = true;
//				if(this.oldPosX!=0 && this.oldPosY != 0 && Math.abs(this.oldPosX-this.tankX)>60) {
//					this.checkTankEdge = Direction.L;
//					buttonLeft = false;
//				}
			}
			else {
				buttonLeft = false;
			}
			break;
		case KeyEvent.VK_D:
			if(this.checkTankEdge!=Direction.R) {
				buttonRight = true;
//				if(this.oldPosX!=0 && this.oldPosY != 0 &&Math.abs(this.oldPosX-this.tankX)>60) {
//					this.checkTankEdge = Direction.R;
//					buttonRight = false;
//				}
			}
			else {
				buttonRight = false;
			}
			break;
		}
		
//		switch (key) {
//			case KeyEvent.VK_W:
//					buttonUP = true;
//				break;
//			case KeyEvent.VK_S:
//					buttonDown = true;
//				break;
//			case KeyEvent.VK_A:
//					buttonLeft = true;
//				break;
//			case KeyEvent.VK_D:
//					buttonRight = true;
//				break;
//			}
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
			oldPress = Direction.R;
			buttonLeft = false;
			break;
		case KeyEvent.VK_D:
			oldPress = Direction.L;
			buttonRight = false;
			break;
		}
		
		location();
	}

	Rectangle getRect() {
		return new Rectangle(tankX, tankY, TANK_WIDTH, TANK_HEIGHT);
	}
}
