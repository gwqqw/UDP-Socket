package UDPClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPSocketClient {
    private static final int TIMEOUT = 5000; // timeout value
    private static final int MAXNUM = 5;     // set max resend data account

    public static void main(String[] args) {
        DatagramSocket dataSender = null;
        byte[] buf = new byte[1024];
        try {
            dataSender = new DatagramSocket(33000);
            dataSender.setSoTimeout(TIMEOUT);
            InetAddress inetAddress = InetAddress.getLocalHost();

            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            System.out.println("Start to send message to Server...");
            while (true) {
                String msg = bufferedReader.readLine();
                if (!msg.isEmpty()) {
                    DatagramPacket sendPacket = new DatagramPacket(msg.getBytes(), msg.length(), inetAddress, 32000);
                    DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                    int tries = 0;
                    boolean receivedResponse = false;

                    while (!receivedResponse && tries < MAXNUM) {
                        dataSender.send(sendPacket);
                        System.out.println("Message " + msg +" has sent to server " + sendPacket.getAddress().getHostAddress() +":"+ sendPacket.getPort());
                        try {
                            dataSender.receive(receivePacket);
                            if (!receivePacket.getAddress().equals(inetAddress)) {
                                throw new IOException("Received packet from an unknown source");
                            }
                            receivedResponse = true;
                        } catch (InterruptedIOException e) {
                            ++tries;
                            System.out.println("Time out " + (MAXNUM - tries) + " more tries...");
                        }
                    }

                    if (receivedResponse) {
                        System.out.println("Client received data from server: ");
                        String strReceived = new String(receivePacket.getData(), 0, receivePacket.getLength()) +
                                " from " + receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort();
                        System.out.println(strReceived);
                        receivePacket.setLength(1024);
                    } else {
                        System.out.println("No response -- give up.");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != dataSender) dataSender.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
