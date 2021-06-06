package tancky;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

class Missile {
    int x, y;
    int tankID;
    int id;

    static final int WIDTH = 10;
    static final int HEIGHT = 10;
    private static int ID = 1;
    private static final int SPEED = 36;

    boolean live = true;

    Direction direction;

    private TankClient tankClient;

    private Missile(int tankID, int x, int y, Direction direction) {
        this.tankID = tankID;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.id = ID++;
    }

    Missile(int tankID, int x, int y, Direction direction, TankClient tankClient) {
        this(tankID, x, y, direction);
        this.tankClient = tankClient;
    }

    void draw(Graphics g) {
        if (!this.live) {
            tankClient.missiles.remove(this);
            return;
        }
        Color c = g.getColor();
        TankColorEnum tankColor= TankColorEnum.values()[this.tankID];
        switch (tankColor) {
        case WHITE:
			g.setColor(Color.WHITE);
			break;
		case RED:
			g.setColor(new Color(255, 105, 105));
			break;
		case GREEN:
			g.setColor(new Color(106, 212, 85));
			break;
		case BLUE:
			g.setColor(new Color(0, 128, 255));
			break;
		case YELLOW:
			g.setColor(Color.YELLOW);
			break;
		case ORANGE:
			g.setColor(new Color(255, 153, 51));
			break;
		case PURPLE:
			g.setColor(Color.PINK);
			break;
		case SKY:
			g.setColor(new Color(111, 242, 242));
			break;
		case GRAY:
			g.setColor(new Color(192,192,192));
			break;
		default:
			break;
		}

        g.fillOval(x, y, WIDTH, HEIGHT);
        g.setColor(c);
        move();
    }

    private void move() {
        switch (direction) {
            case U:
                y -= SPEED;
                break;
            case RU:
                x += SPEED;
                y -= SPEED;
                break;
            case R:
                x += SPEED;
                break;
            case RD:
                x += SPEED;
                y += SPEED;
                break;
            case D:
                y += SPEED;
                break;
            case LD:
                x -= SPEED;
                y += SPEED;
                break;
            case L:
                x -= SPEED;
                break;
            case LU:
                x -= SPEED;
                y -= SPEED;
                break;
            case STOP:
                break;
        }
      
        if (x < 0 || y < 0 || y > TankClient.GAME_HEIGHT || x > TankClient.GAME_WIDTH) {
            this.live = false;
        }
    }

    private Rectangle getRect() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    boolean hitTank(Tank tank) {
        if (this.live && this.getRect().intersects(tank.getRect()) && tank.isLive() && this.tankID != tank.getId()) {
            tank.setLive();
            this.live = false;
            tankClient.explodes.add(new Explode(x, y, 0, this.tankID,tankClient));
            return true;
        }
        return false;
    }
    
    public int checkCollision(Brick brick)
	{
    	int[] brickPosX = brick.bricksXPos;
    	int[] brickPosY = brick.bricksYPos;
    	int[] brickON   = brick.brickON;
    	
		for(int i=0; i< brickON.length;i++)
		{
			if(brickON[i]==1)
			{
				if(new Rectangle(x, y, this.WIDTH, this.HEIGHT).intersects(new Rectangle(brickPosX[i], brickPosY[i], 36, 36)) && this.live)
				{
					brickON[i] = 0;
					return i;
				}
			}
		}
		
		return -1;
	}
	
	public boolean checkSolidCollision()
	{
		boolean collided = false;
		Brick brick = new Brick();
    	int[] solidPosX = brick.solidBricksXPos;
    	int[] solidPosY = brick.solidBricksYPos;
      	int[] greenPosX = brick.greenBricksXPos;
    	int[] greenPosY = brick.greenBricksYPos;
		for(int i=0; i< solidPosX.length;i++)
		{		
			if(new Rectangle(x, y, this.WIDTH, this.HEIGHT).intersects(new Rectangle(solidPosX[i], solidPosY[i], 36, 36)))
			{		
				collided = true;
				break;
			}			
		}
		for(int i=0; i< greenPosX.length;i++)
		{		
			if(new Rectangle(x, y, this.WIDTH, this.HEIGHT).intersects(new Rectangle(greenPosX[i], greenPosY[i], 36, 36)))
			{		
				collided = true;
				break;
			}			
		}
		
		return collided;
	}
}
