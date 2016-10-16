import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * SocketProgramming
 * Created by dwson Son (20420487)
 * on 10/16/16
 * d3son@uwaterloo.ca
 */
public class Client {

    public static void main(String[] args) throws IOException {

        if (args.length != 4) return;

        String serverAddress = args[0], message = args[3], requestCode = args[2];
        int nPort = Integer.parseInt(args[1]);
        String reversedString, rPort;

        // Stage 1. Negotiation using TCP sockets
        Socket clientTcpSocket = new Socket(serverAddress, nPort);

        DataOutputStream outToServer = new DataOutputStream(clientTcpSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientTcpSocket.getInputStream()));

        outToServer.writeBytes(requestCode + '\n');
        rPort = inFromServer.readLine();
        clientTcpSocket.close();
        if (rPort == null) {
            System.out.println("Error: incorrect request code");
            return;
        }

        // Stage 2. Transaction using UDP sockets
        DatagramSocket clientUdpSocket = new DatagramSocket();

        InetAddress ipAddress = InetAddress.getByName(serverAddress);
        byte[] sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, Integer.parseInt(rPort));
        clientUdpSocket.send(sendPacket);

        byte[] receiveData = new byte[sendData.length];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientUdpSocket.receive(receivePacket);

        reversedString = new String(receivePacket.getData());
        System.out.println(reversedString);

        clientUdpSocket.close();
    }
}
