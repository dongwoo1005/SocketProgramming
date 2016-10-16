import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * SocketProgramming
 * Created by dwson Son (20420487)
 * on 10/16/16
 * d3son@uwaterloo.ca
 */
public class Server {

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Required command line parameters: <req_code>");
            return;
        }

        String requestCode = args[0];
        int nPort = 9000;

        ServerSocket serverTcpSocket = new ServerSocket(nPort);
        System.out.println("SERVER_PORT=" + nPort);


        while (true) {
            Socket connectionTcpSocket = serverTcpSocket.accept();
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(connectionTcpSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionTcpSocket.getOutputStream());

            String requestCodeFromClient = inFromClient.readLine();
            if (requestCodeFromClient.equals(requestCode)) {
                int rPort = 8999;   // TODO: generate it randomly
                outToClient.writeBytes(String.valueOf(rPort) + '\n');

                DatagramSocket serverUdpSocket = new DatagramSocket(rPort);

                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverUdpSocket.receive(receivePacket);

                String receivedString = new String(receivePacket.getData());

                StringBuilder stringBuilder = new StringBuilder(receivedString.replaceAll("\0", ""));
                System.out.println(receivedString.replaceAll("\0", ""));

                byte[] sendData = stringBuilder.reverse().toString().getBytes();
                InetAddress ipAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
                serverUdpSocket.send(sendPacket);

                serverUdpSocket.close();
            } else {
                connectionTcpSocket.close();
            }
        }
    }
}
