package tancky;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

public class TankClient extends JFrame {
	static final int GAME_WIDTH = 1160;
	static final int GAME_HEIGHT = 820;
	static final int SCREEN_WIDTH = 1360;
	static final int SCREEN_HEIGHT = 848;
	int[] numKill = new int[10];
	int totalPlayer = 0;

	Brick bricks = new Brick();;
	List<Missile> missiles = new ArrayList<>();
	List<Explode> explodes = new ArrayList<>();
	List<Tank> enemyTanks = new ArrayList<>();

	private Image offScreenImage = null;

	NetClient netClient = new NetClient(this);

	private ConnectDialog connectDialog = new ConnectDialog();

	Tank tank;

	public void paint(Graphics g) {
		this.setTitle("Tank War - Player : " + tank.getId());
		if (offScreenImage == null) {
			offScreenImage = this.createImage(SCREEN_WIDTH, SCREEN_HEIGHT);
		}

		Graphics gOffScreen = offScreenImage.getGraphics();

		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		super.paint(gOffScreen);

		tank.draw(gOffScreen);

		for (int i = 0; i < 10; i++) {
			numKill[i] = 0;
		}
		;
		for (Explode explode : explodes) {
			if (explode.getType() == 0 && explode.getTankId() == tank.getId()) {
				numKill[explode.getTankId()]++;
			}
		}

		gOffScreen.setColor(Color.WHITE);
		gOffScreen.fillRect(GAME_WIDTH, 0, 200, GAME_HEIGHT);
		gOffScreen.setColor(Color.BLACK);
		gOffScreen.setFont(new Font("Courier New", Font.BOLD, 24));
		gOffScreen.drawString("TANK GAME ", GAME_WIDTH + 20, 150);
		gOffScreen.setFont(new Font("Courier New", Font.BOLD, 16));
		gOffScreen.drawString("Kill: " + numKill[tank.getId()], GAME_WIDTH + 20, 200);
		gOffScreen.drawString("Enemies: " + enemyTanks.size(), GAME_WIDTH + 20, 250);
		gOffScreen.drawString("Missiles: " + missiles.size(), GAME_WIDTH + 20, 300);

		bricks.drawSolids(this, gOffScreen);
		bricks.draw(this, gOffScreen);

		for (int i = 0; i < enemyTanks.size(); i++) {
//			if(enemyTanks.get(i).checkEdgeTank(enemyTanks.get(i).tankX, enemyTanks.get(i).tankY)) {
				enemyTanks.get(i).draw(gOffScreen);
//			}
		}
		for (int i = 0; i < missiles.size(); i++) {
			if (missiles.get(i).hitTank(tank)) {
				netClient.send(new TankDeadMsg(tank.id));
				netClient.send(new MissileDeadMsg(missiles.get(i).tankID, missiles.get(i).id, -1));
			}

			if (missiles.get(i).checkSolidCollision()) {
				netClient.send(new MissileDeadMsg(missiles.get(i).tankID, missiles.get(i).id, -2));
			}

			int brickCollissionId = missiles.get(i).checkCollision(bricks);
			if (brickCollissionId != -1) {
				netClient.send(new BrickDeadMsg(brickCollissionId));
				netClient.send(new MissileDeadMsg(missiles.get(i).tankID, missiles.get(i).id, brickCollissionId));
			}

			missiles.get(i).draw(gOffScreen);
		}

		for (int i = 0; i < explodes.size(); i++) {
			explodes.get(i).draw(gOffScreen);
		}

		gOffScreen.setColor(Color.BLACK);
		g.drawImage(offScreenImage, 0, 25, null);
	}

	private boolean checkStartPos(int startX, int startY) {
		Brick brick = new Brick();
		int[] brickPosX = brick.bricksXPos;
		int[] brickPosY = brick.bricksYPos;
		int[] solidPosX = brick.solidBricksXPos;
		int[] solidPosY = brick.solidBricksYPos;
		int[] greenPosX = brick.greenBricksXPos;
		int[] greenPosY = brick.greenBricksYPos;
		for (int i = 0; i < brickPosX.length; i++) {
			if (startX == brickPosX[i] && startY == brickPosY[i]) {
				return true;
			}
		}
		for (int i = 0; i < solidPosX.length; i++) {
			if (startX == solidPosX[i] && startY == solidPosY[i]) {
				return true;
			}
		}
		for (int i = 0; i < greenPosX.length; i++) {
			if (startX == greenPosX[i] && startY == greenPosY[i]) {
				return true;
			}
		}
		return false;
	}

	private void launchFrame() {
		int startX = 0, startY = 0;
		boolean check = true;
		Random rnd = new Random();
		while (check) {
			int rand = rnd.nextInt(4);
			if (rand == 0) {
				startX = 36 * rnd.nextInt(31);
				startY = 20;
				check = checkStartPos(startX, startY);
			} else if (rand == 1) {
				startX = 1116;
				startY = 36 * rnd.nextInt(20) + 20;
				check = checkStartPos(startX, startY);
			} else if (rand == 2) {
				startX = 36 * rnd.nextInt(31);
				startY = 776;
				check = checkStartPos(startX, startY);
			} else {
				startX = 10;
				startY = 36 * rnd.nextInt(20) + 20;
				check = checkStartPos(startX, startY);
			}
		}
		;
		tank = new Tank(startX, startY, Direction.STOP, this, bricks);

		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setLocation((screenWidth - SCREEN_WIDTH) / 2, (screenHeight - SCREEN_HEIGHT) / 2);
		this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		this.setResizable(false);
		this.getContentPane().setBackground(Color.BLACK);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setTitle("TankWar");
		this.setVisible(true);
		this.addKeyListener(new KeyMonitor());
		this.connectDialog.setVisible(true);
		Thread paintThread = new Thread(new PaintThread());
		paintThread.start();
	}

	public static void main(String[] args) {
		TankClient tankClient = new TankClient();
		tankClient.launchFrame();
	}

	private class PaintThread implements Runnable {
		public void run() {
			while (true) {
				for (int i = 0; i < 10; i++) {
					totalPlayer += numKill[i];
				}
				;
				if (totalPlayer == enemyTanks.size() && totalPlayer != 0) {

					int gameOver = 0;
					int playerAlive = tank.getId();
					for (Tank tankEnermy : enemyTanks) {
						if (tankEnermy.isLive()) {
							gameOver++;
							playerAlive = tankEnermy.getId();
						}
					}
					if (gameOver == 0) {
						JOptionPane.showMessageDialog(null, "Player " + playerAlive + " win", "Game over",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				repaint();
				try {
					Thread.sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	private void enterIP(String IP) {
		int TCPPort = 46464;
		int port = (int) (Math.random() * 10000);
		if (port <= 1000) {
			port = port + 1000;
		}
		netClient.setUdpPort(port);
		connectDialog.setVisible(false);
		connectDialog.setVisible(!netClient.connect(IP, TCPPort));
	}

	private class KeyMonitor extends KeyAdapter {
		public void keyReleased(KeyEvent keyEvent) {
			tank.keyReleased(keyEvent);
		}

		public void keyPressed(KeyEvent keyEvent) {
			switch (keyEvent.getKeyCode()) {
			case KeyEvent.VK_C:
				connectDialog.setVisible(true);
				break;
			case KeyEvent.VK_ESCAPE:
				connectDialog.setVisible(false);
			case KeyEvent.VK_ENTER:
				if (connectDialog.isVisible()) {
					enterIP(connectDialog.textFieldIP.getText().trim());
				}
				break;
			default:
				if (!connectDialog.isVisible()) {
					tank.keyPressed(keyEvent);
				}
				break;
			}
		}
	}

	class ConnectDialog extends Dialog {
		Button button = new Button("Connect");
		TextField textFieldIP = new TextField("127.0.0.1", 16);

		ConnectDialog() {
			super(TankClient.this, true);
			textFieldIP.addKeyListener(new KeyMonitor());
			this.setLayout(new FlowLayout());
			this.add(new Label("Host IP :"));
			this.add(textFieldIP);
			this.add(button);
			button.addActionListener(e -> enterIP(textFieldIP.getText().trim()));
			this.pack();
			this.setLocationRelativeTo(null);
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			});
		}
	}
}
