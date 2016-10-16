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
public class Client {

    private static class RPort {
        private String value;

        public RPort() {
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getValue() {
            return Integer.parseInt(value);
        }

        public boolean isEmpty() {
            return value == null;
        }
    }

    private static void negotiateUsingTcpSocket(String serverAddress, int nPort, String requestCode, RPort rPort)
            throws IOException {

        // Create a TCP connection
        Socket clientTcpSocket = new Socket(serverAddress, nPort);

        // Send requestCode
        DataOutputStream outToServer = new DataOutputStream(clientTcpSocket.getOutputStream());
        outToServer.writeBytes(requestCode + '\n');

        // Receive rPort number
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientTcpSocket.getInputStream()));
        rPort.setValue(inFromServer.readLine());

        // Close TCP socket
        clientTcpSocket.close();
    }

    private static void transactUsingUdpSocket(String serverAddress, RPort rPort, String message)
            throws IOException {

        // Create a UDP socket
        DatagramSocket clientUdpSocket = new DatagramSocket();

        // Send packet with message
        byte[] sendData = message.getBytes();
        InetAddress ipAddress = InetAddress.getByName(serverAddress);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, rPort.getValue());
        clientUdpSocket.send(sendPacket);

        // Receive packet
        byte[] receiveData = new byte[sendData.length];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientUdpSocket.receive(receivePacket);

        // Print out reversed message from the packet
        System.out.println(new String(receivePacket.getData()));

        // Close UDP socket
        clientUdpSocket.close();
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 4) {
            System.err.println("Required command line parameters: <server_address> <n_port> <req_code> <msg>");
            return;
        }

        String serverAddress = args[0];
        int nPort = Integer.parseInt(args[1]);
        String requestCode = args[2], message = args[3];
        RPort rPort = new RPort();

        negotiateUsingTcpSocket(serverAddress, nPort, requestCode, rPort);

        if (rPort.isEmpty()) {
            System.out.println("Error: incorrect request code");
            System.exit(1);
        }

        transactUsingUdpSocket(serverAddress, rPort, message);
    }
}
