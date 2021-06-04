package tancky;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.List;

class Tank {
    int tankX;
    int tankY;
    int id;
    int numKill=0;

    private boolean buttonUP = false;
    private boolean buttonDown = false;
    private boolean buttonLeft = false;
    private boolean buttonRight = false;
    private boolean isLive = true;
    private Brick brick;

    private static final int TANK_WIDTH = 36;
    private static final int TANK_HEIGHT = 36;
    private static final int SPEED = 12;
    private static final int UP_LIMIT = 16;

    Direction direction;
    Direction barrelDirection = Direction.D;

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
    }

    private boolean checkEdge(int x, int y) {
    	int[] brickPosX = brick.bricksXPos;
    	int[] brickPosY = brick.bricksYPos;
    	int[] solidPosX = brick.solidBricksXPos;
    	int[] solidPosY = brick.solidBricksYPos;
    	int[] greenPosX = brick.greenBricksXPos;
    	int[] greenPosY = brick.greenBricksYPos;
    	int[] brickON   = brick.brickON;
    	
    	List<Tank> enermyTanks = tankClient.enemyTanks;
    	
    	for(Tank tankEnermy:enermyTanks) {
    		if(tankEnermy.isLive() && new Rectangle(x, y, 36, 36).intersects(new Rectangle(tankEnermy.tankX, tankEnermy.tankY, 36, 36))) {
    			return false;
    		}
    	}
    	
    	for(int i=0; i<brickPosX.length; i++) {
    		if( (brickON[i]==1) && (new Rectangle(x, y, 36, 36).intersects(new Rectangle(brickPosX[i], brickPosY[i], 36, 36))) ) {
    			return false;
    		}
		}
		for(int i=0; i<solidPosX.length; i++) {
			if(new Rectangle(x, y, 36, 36).intersects(new Rectangle(solidPosX[i], solidPosY[i], 36, 36))) {
    			return false;
    		}
		}
		for(int i=0; i<greenPosX.length; i++) {
			if(new Rectangle(x, y, 36, 36).intersects(new Rectangle(greenPosX[i], greenPosY[i], 36, 36))) {
    			return false;
    		}
		}
    	
        return x >= 10 && x <= (TankClient.GAME_WIDTH - TANK_WIDTH)
                && y >= UP_LIMIT && y <= TankClient.GAME_HEIGHT - TANK_HEIGHT;
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

        if (buttonUP && !buttonDown && !buttonLeft && !buttonRight) {
            direction = Direction.U;
        } else if (buttonUP && !buttonDown && !buttonLeft) {
            direction = Direction.RU;
        } else if (!buttonUP && !buttonDown && !buttonLeft && buttonRight) {
            direction = Direction.R;
        } else if (!buttonUP && buttonDown && !buttonLeft && buttonRight) {
            direction = Direction.RD;
        } else if (!buttonUP && buttonDown && !buttonLeft) {
            direction = Direction.D;
        } else if (!buttonUP && buttonDown && !buttonRight) {
            direction = Direction.LD;
        } else if (!buttonUP && !buttonDown && buttonLeft && !buttonRight) {
            direction = Direction.L;
        } else if (buttonUP && !buttonDown && !buttonRight) {
            direction = Direction.LU;
        } else if (!buttonUP && !buttonDown && !buttonLeft) {
            direction = Direction.STOP;
        }
        if (this.direction != oldDirection) {
            TankMoveMsg msg = new TankMoveMsg(id, this.tankX, this.tankY, direction, this.barrelDirection);
            tankClient.netClient.send(msg);
        }
    }

    void draw(Graphics graphics) {
        if (!this.isLive) {
            return;
        }
        TankColorEnum tankColor= TankColorEnum.values()[id];
        switch (tankColor) {
        case WHITE:
			graphics.setColor(Color.WHITE);
			break;
		case RED:
			graphics.setColor(new Color(255, 105, 105));
			break;
		case GREEN:
			graphics.setColor(new Color(106, 212, 85));
			break;
		case BLUE:
			graphics.setColor(new Color(0, 128, 255));
			break;
		case YELLOW:
			graphics.setColor(Color.YELLOW);
			break;
		case ORANGE:
			graphics.setColor(new Color(255, 153, 51));
			break;
		case PINK:
			graphics.setColor(Color.PINK);
			break;
		case SKY:
			graphics.setColor(new Color(111, 242, 242));
			break;
		case GRAY:
			graphics.setColor(new Color(192,192,192));
			break;
		default:
			break;
		}
        
        graphics.fillRect(this.tankX, this.tankY, TANK_WIDTH, TANK_HEIGHT);

        int x1 = this.tankX + TANK_WIDTH / 2;
        int y1 = this.tankY + TANK_HEIGHT / 2;
        int x2 = countX2();
        int y2 = countY2();

        Graphics2D g = (Graphics2D) graphics;
        g.setStroke(new BasicStroke(10));
        g.drawLine(x1, y1, x2, y2);
        graphics.setColor(graphics.getColor());
        graphics.setFont (new Font ("Courier New", Font.BOLD, 16));
        graphics.setColor(Color.BLACK);
        graphics.drawString(""+this.id, this.tankX+12, this.tankY + 24);
        move();
    }

    private int countX2() {
        double x = 0;
        switch (barrelDirection) {
            case U:
                x = this.tankX + TANK_WIDTH / 2 ;
                break;
            case RU:
                x = this.tankX + TANK_WIDTH * 1.5 -16;
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
                x = this.tankX - TANK_WIDTH / 2 +16;
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
                y = this.tankY + TANK_HEIGHT * 1.5 -16 ;
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
                y = this.tankY - TANK_HEIGHT / 2 + 16 ;
                break;
            default:
                break;
        }
        return (int) y;
    }

    void keyPressed(KeyEvent keyEvent) {
        int key = keyEvent.getKeyCode();
        switch (key) {
            case KeyEvent.VK_W:
                buttonUP = true;
                break;
            case KeyEvent.VK_S:
                buttonDown = true;
                break;
            case KeyEvent.VK_A:
                buttonLeft = true;
                break;
            case KeyEvent.VK_D:
                buttonRight = true;
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

        Missile missile = new Missile(id, x, y,this.barrelDirection, this.tankClient);
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
                buttonUP = false;
                break;
            case KeyEvent.VK_S:
                buttonDown = false;
                break;
            case KeyEvent.VK_A:
                buttonLeft = false;
                break;
            case KeyEvent.VK_D:
                buttonRight = false;
                break;
        }
        location();
    }

    Rectangle getRect() {
        return new Rectangle(tankX, tankY, TANK_WIDTH, TANK_HEIGHT);
    }
}
