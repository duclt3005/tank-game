package tancky;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ImageIcon;


public class Brick {
	public int bricksXPos[] = {360,432,468,936,972,    				// 1 - 5
						72,360,396,432,468,972,						// 2 - 6
						72,756,792,828,								// 3 - 4
						72,180,756,792,828,864,900,					// 4 - 7
						180,216,468,504,756,792,828,				// 5 - 7
						468,504,									// 6 - 2
						684,										// 7 - 1
						180,216,252,684,							// 8 - 4
						684,828,864,1008,							// 9 - 4
						396,432,468,504,972, 						// 10 - 5
						504,540,576,972, 							// 11 - 4
						108,144,									// 12 - 2
						108,144,1116,								// 13 - 3
						180,648,1080,1116,							// 14 - 4
						180,324,360,396,612,648,1044,1080,1116,		// 15 - 9
						144,648,864,900,1080,1116,					// 16 - 6
						144,864,900,1116,							// 17 - 4
						864,900,									// 18 - 2
						396,432,576,612,							// 19 - 4
						396,432,576,612,							// 20 - 4
						396,432,468,504,540,576,612,900,			// 21 - 8
						108,144,180,396,432,576,612,828,864,900		// 22 - 10
					};
	public int bricksYPos[] = {20,20,20,20,20,						// 1
						56,56,56,56,56,56,							// 2
						92,92,92,92,								// 3
						128,128,128,128,128,128,128,				// 4
						164,164,164,164,164,164,164,				// 5
						200,200,									// 6
						236,										// 7
						272,272,272,272,							// 8
						308,308,308,308,							// 9
						344,344,344,344,344,						// 10
						380,380,380,380,							// 11
						416,416,									// 12
						452,452,452,								// 13
						488,488,488,488,							// 14
						524,524,524,524,524,524,524,524,524,		// 15
						560,560,560,560,560,560,					// 16
						596,596,596,596,							// 17
						632,632,									// 18
						668,668,668,668,							// 19
						704,704,704,704,							// 20
						740,740,740,740,740,740,740,740,			// 21
						776,776,776,776,776,776,776,776,776,776,	// 22
					};
	
	public int solidBricksXPos[] = {648,684,720,			// 1
									324,612,				// 4
									324,1008,				// 5
									972,1008,				// 6
									72,						// 8
									396,432,				// 9
									828, 					// 12
									828,864,				// 13
									504,					// 15
									504,					// 16
									468,504,540,720,			// 19
									144,180,720,			// 20
									
						};
	public int solidBricksYPos[] = {20,20,20,				// 1
									128,128,				// 4
									164,164,				// 5
									200,200,				// 6
									272,					// 8
									308,308,				// 9
									416,					// 12
									452,452,				// 13
									524, 					// 15
									560,					// 16
									668,668,668,668,			// 19
									704,704,704,		// 20
						};
	
	public int greenBricksXPos[] = {180,216,396,864,900,			// 1
									216,							// 4
									72,612,648,						// 7
									108,828,864,					// 8
									72,216,252,						// 9
									756,792,864,1116,				// 12
									324,792,							// 13
									108,144,288,324,504,			// 14
									108,							// 20
									936,							// 21
									468,504,540,936,				// 22
						};
	public int greenBricksYPos[] = {20,20,20,20,20, 				// 1
									128,							// 4
									236,236,236,					// 7
									272,272,272,					// 8
									308,308,308,					// 9
									416,416,416,416,				// 12
									452,452,							// 13
									488,488,488,488,488,			// 14
									704,							// 20
									740,							// 21
									776,776,776,776,				// 22		
						};
	
	int brickON[] = new int[105];
	
	private ImageIcon breakBrickImage;
	private ImageIcon solidBrickImage;
	private ImageIcon greenBrickImage;
	
	public Brick()
	{
		breakBrickImage=new ImageIcon("break_brick.jpg");
		solidBrickImage=new ImageIcon("solid_brick.jpg");	
		greenBrickImage=new ImageIcon("grass.jpg");
		
		for(int i=0; i< brickON.length;i++)
		{
			brickON[i] = 1;
		}
	}
	
	public void draw(Component c, Graphics g)
	{
		for(int i=0; i< brickON.length;i++)
		{
			if(brickON[i]==1)
			{
				breakBrickImage.paintIcon(c, g, bricksXPos[i],bricksYPos[i]);
			}
		}
	}
	
	public void drawSolids(Component c, Graphics g)
	{
		for(int i=0; i< solidBricksXPos.length;i++)
		{			
			solidBrickImage.paintIcon(c, g, solidBricksXPos[i],solidBricksYPos[i]);
		}
		for(int i=0; i< greenBricksXPos.length;i++)
		{			
			greenBrickImage.paintIcon(c, g, greenBricksXPos[i],greenBricksYPos[i]);
		}
	}
	
	public boolean checkCollision(int x, int y)
	{
		boolean collided = false;
		for(int i=0; i< brickON.length;i++)
		{
			if(brickON[i]==1)
			{
				if(new Rectangle(x, y, 10, 10).intersects(new Rectangle(bricksXPos[i], bricksYPos[i], 50, 50)))
				{
					brickON[i] = 0;
					collided = true;
					break;
				}
			}
		}
		
		return collided;
	}
	
	public boolean checkSolidCollision(int x, int y)
	{
		boolean collided = false;
		for(int i=0; i< solidBricksXPos.length;i++)
		{		
			if(new Rectangle(x, y, 10, 10).intersects(new Rectangle(solidBricksXPos[i], solidBricksYPos[i], 50, 50)))
			{		
				collided = true;
				break;
			}			
		}
		
		return collided;
	}
}
