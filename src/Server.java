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

    private static void negotiateUsingTcpSocket(String requestCode) throws IOException {

        // Create a TCP socket on nPort
        ServerSocket serverTcpSocket = new ServerSocket(0);
        System.out.println("SERVER_PORT=" + serverTcpSocket.getLocalPort());


        while (true) {
            // Listen for a connection to be made
            Socket connectionTcpSocket = serverTcpSocket.accept();

            // Get request code from client
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(connectionTcpSocket.getInputStream()));
            String requestCodeFromClient = inFromClient.readLine();

            if (requestCodeFromClient.equals(requestCode)) {    // Request code is verified
                // Reply back with rPort
                int rPort = getRandomFreePort();
                DataOutputStream outToClient = new DataOutputStream(connectionTcpSocket.getOutputStream());
                outToClient.writeBytes(String.valueOf(rPort) + '\n');

                transactUsingUdpSocket(rPort);
            } else {                                            // Request code is not verified
                connectionTcpSocket.close();                    // Close the TCP connection
            }
        }
    }

    private static int getRandomFreePort() throws IOException {

        ServerSocket socket = new ServerSocket(0);
        socket.setReuseAddress(true);
        int port = socket.getLocalPort();
        socket.close();
        return port;
    }

    private static void transactUsingUdpSocket(int rPort) throws IOException {

        // Create a UDP socket on rPort
        DatagramSocket serverUdpSocket = new DatagramSocket(rPort);

        // Receive a packet
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        serverUdpSocket.receive(receivePacket);

        // Reverse a string from the packet
        String receivedString = new String(receivePacket.getData());
        String reversedString = reverseString(receivedString.replaceAll("\0", ""));

        // Reply back the reversed string
        byte[] sendData = reversedString.getBytes();
        InetAddress ipAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
        serverUdpSocket.send(sendPacket);

        // Close the UDP socket
        serverUdpSocket.close();
    }

    private static String reverseString(String s) {

        int maxIndex = s.length() - 1;
        if (maxIndex < 1) return s;

        char[] charArr = s.toCharArray();
        // Reverse by swapping
        for (int i=0; i<=maxIndex/2; i+=1) {
            char temp = charArr[i];
            charArr[i] = charArr[maxIndex - i];
            charArr[maxIndex - i] = temp;
        }

        return String.valueOf(charArr);
    }

    public static void main(String[] args) throws IOException {

        // Validate command line parameter
        if (args.length != 1) {
            System.err.println("Required command line parameters: <req_code>");
            return;
        }

        String requestCode = isValidRequestCode(args[0]) ? args[0] : null;
        if (requestCode == null) {
            System.err.println("Error: parameter <req_code> is not valid.");
            System.exit(1);
        }

        // Begin signaling
        negotiateUsingTcpSocket(requestCode);
    }
}
