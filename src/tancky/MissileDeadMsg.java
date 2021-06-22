package tancky;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileDeadMsg implements Msg {
    private int ID;
    private int tankId;
    private int checkObject;
    private TankClient tankClient;

    MissileDeadMsg(int tankId, int ID, int checkObject) {
        this.tankId = tankId;
        this.ID = ID;
        this.checkObject = checkObject;
    }

    MissileDeadMsg(TankClient tankClient) {
        this.tankClient = tankClient;
    }

    @Override
    public void send(DatagramSocket datagramSocket, String IP, int udpPort) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeInt(MISSILE_DEAD_MSG);
            dataOutputStream.writeInt(tankId);
            dataOutputStream.writeInt(ID);
            dataOutputStream.writeInt(checkObject);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] buffer = byteArrayOutputStream.toByteArray();
        try {
            datagramSocket.send(new DatagramPacket(buffer, buffer.length, new InetSocketAddress(IP, udpPort)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parse(DataInputStream dataInputStream) {
        try {
            int tankId = dataInputStream.readInt();
            int ID = dataInputStream.readInt();
            int checkObject = dataInputStream.readInt();
            for (int i = 0; i < tankClient.missiles.size(); i++) {
                Missile missile = tankClient.missiles.get(i);
                if (missile.tankID == tankId && missile.id == ID) {
                    missile.live = false;
                    if(checkObject == -1) {
                    	tankClient.explodes.add(new Explode(missile.x, missile.y, 0,tankId));
                    }
                    if(checkObject != -1 && checkObject != -2) {
                    	tankClient.explodes.add(new Explode(tankClient.bricks.bricksXPos[checkObject], tankClient.bricks.bricksYPos[checkObject], 1, tankId));
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
