import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * SocketProgramming
 * Created by Will Son (20420487)
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

    private static boolean isValidMessage(String s) {
        return !s.isEmpty() && s.getBytes().length < 1024;
    }

    private static boolean isValidRequestCode(String s) {

        if (!isValidMessage(s)) return false;
        for (int i=0; i<s.length(); i+=1) {
            if (Character.digit(s.charAt(i), 10) < 0) return false;
        }
        return true;
    }

    private static boolean isValidNPort(String s) {

        if (!isValidRequestCode(s)) return false;
        int port = Integer.parseInt(s);
        return port > 0 && port <= 0xFFFF;
    }

    private static boolean isValidServerAddress(String s) {
        try {
            InetAddress.getByName(s);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
        return true;
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

        // Validate command line parameters
        if (args.length != 4) {
            System.err.println("Required command line parameters: <server_address> <n_port> <req_code> <msg>");
            System.exit(1);
        }

        String serverAddress = isValidServerAddress(args[0]) ? args[0] : null;
        if (serverAddress == null) {
            System.err.println("Error: parameter <server_address> is not valid.");
            System.exit(1);
        }

        int nPort = isValidNPort(args[1]) ? Integer.parseInt(args[1]) : -1;
        if (nPort == -1) {
            System.err.println("Error: parameter <n_port> is not valid.");
            System.exit(1);
        }

        String requestCode = isValidRequestCode(args[2]) ? args[2] : null;
        if (requestCode == null) {
            System.err.println("Error: parameter <req_code> is not valid.");
            System.exit(1);
        }

        String message = isValidMessage(args[3]) ? args[3] : null;
        if (message == null) {
            System.err.println("Error: parameter <msg> is not valid.");
            System.exit(1);
        }

        // Stage 1
        RPort rPort = new RPort();
        negotiateUsingTcpSocket(serverAddress, nPort, requestCode, rPort);

        if (rPort.isEmpty()) {
            System.out.println("Error: incorrect request code");
            System.exit(1);
        }

        // Stage 2
        transactUsingUdpSocket(serverAddress, rPort, message);
    }
}
