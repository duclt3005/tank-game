package tancky;

import java.awt.*;

class Explode {
    private int x, y;
    private int tankID;
    private int type;
    private int step = 0;
    private int[] diameter = {1, 4, 7, 12, 18, 26, 32, 40, 49, 30, 20, 14, 6};

    private boolean live = true;

    private TankClient tankClient;

    Explode(int x, int y, int type,int tankID, TankClient tc) {
        this.x = x;
        this.y = y;
        this.tankClient = tc;
        this.tankID = tankID;
        this.type = type;
    }
    
    public int getTankId() {
    	return this.tankID;
    }
    
    public int getType() {
    	return this.type;
    }
    
    void draw(Graphics graphics) {
        if (!this.live) {
//            tankClient.explodes.remove(this);
            return;
        }

        if (step == diameter.length) {
            live = false;
            step = 0;
            return;
        }
        
        Color color = graphics.getColor();
        graphics.setColor(Color.ORANGE);
        graphics.fillOval(x, y, diameter[step], diameter[step]);
        graphics.setColor(color);
        step++;
    }
}
