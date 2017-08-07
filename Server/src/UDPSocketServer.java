package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPSocketServer {
    public static void main(String[] args) {
        DatagramSocket dataSocket = null;
        byte[] buf = new byte[1024];
        try {
            dataSocket = new DatagramSocket(32000);
            DatagramPacket dpReceive = new DatagramPacket(buf, 1024);
            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            System.out.println("Server is on, waiting for client to send data...");
            boolean flag = true;
            while (flag)
            {
                dataSocket.receive(dpReceive);
                System.out.println("Server received data from client:");
                String strReceive = new String(dpReceive.getData(),0, dpReceive.getLength()) +
                        " from " + dpReceive.getAddress().getHostAddress() + ":"+dpReceive.getPort();
                System.out.println(strReceive);

                String str = bufferedReader.readLine();
                DatagramPacket dpSend = new DatagramPacket(str.getBytes(), str.length(), dpReceive.getAddress(),dpReceive.getPort());
                dataSocket.send(dpSend);
                System.out.println("Message " + str +" is sent to "+ dpReceive.getAddress().getHostAddress() +":"+dpReceive.getPort());
                dpReceive.setLength(1024);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (null != dataSocket) dataSocket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
