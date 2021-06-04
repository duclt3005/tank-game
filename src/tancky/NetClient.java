package tancky;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class NetClient {
    private int udpPort;
    private TankClient tankClient;
    private String IP;

    private static final String IPV4_REGEX =
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    private static final Pattern IPv4_PATTERN = Pattern.compile(IPV4_REGEX);
    
    void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    private DatagramSocket datagramSocket;

    NetClient(TankClient tankClient) {
        this.tankClient = tankClient;
    }

    boolean connect(String IP, int port) {
        this.IP = IP;
        Matcher matcher = IPv4_PATTERN.matcher(IP);
        
        if(!matcher.matches()) {
        	JOptionPane.showMessageDialog(null, "Invalid IP", "ERROR",JOptionPane.ERROR_MESSAGE);
        	return false;
        }
        try {
            datagramSocket = new DatagramSocket(udpPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        Socket socket = null;
        try {
            socket = new Socket(IP, port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream.writeInt(this.udpPort);

            int readInt = dataInputStream.readInt();
            this.tankClient.tank.id = readInt;

            dataOutputStream.flush();
            dataOutputStream.close();
            dataInputStream.close();
        } catch (IOException e) {
        	JOptionPane.showMessageDialog(null, "No more player", "Error",JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
            	JOptionPane.showMessageDialog(null, "Invalid IP", "Error",JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        send(new TankNewMsg(tankClient.tank));
        new Thread(new UDPReceiveThread()).start();
        return true;
    }


    public void send(Msg msg) {
        msg.send(datagramSocket, IP, TankServer.UDP_SERVER_PORT);
    }

    private class UDPReceiveThread implements Runnable {
        byte[] buffer = new byte[1024];

        public void run() {
            while (datagramSocket != null) {
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                try {
                    datagramSocket.receive(datagramPacket);
                    parse(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void parse(DatagramPacket datagramPacket) {
            DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(buffer, 0, datagramPacket.getLength()));
            try {
                int msgType = dataInputStream.readInt();
                Msg msg;
                switch (msgType) {
                    case Msg.TANK_NEW_MSG:
                        msg = new TankNewMsg(NetClient.this.tankClient);
                        msg.parse(dataInputStream);
                        break;
                    case Msg.TANK_MOVE_MSG:
                        msg = new TankMoveMsg(NetClient.this.tankClient);
                        msg.parse(dataInputStream);
                        break;
                    case Msg.MISSILE_NEW_MSG:
                        msg = new MissileNewMsg(NetClient.this.tankClient);
                        msg.parse(dataInputStream);
                        break;
                    case Msg.TANK_DEAD_MSG:
                        msg = new TankDeadMsg(NetClient.this.tankClient);
                        msg.parse(dataInputStream);
                        break;
                    case Msg.MISSILE_DEAD_MSG:
                        msg = new MissileDeadMsg(NetClient.this.tankClient);
                        msg.parse(dataInputStream);
                        break;
                    case Msg.BRICK_DEAD_MSG:
                        msg = new BrickDeadMsg(NetClient.this.tankClient);
                        msg.parse(dataInputStream);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
