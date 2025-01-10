package ru.mono.pong;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class test {
    public static void main(String[] args) {
        try {
            String serverAddress = "95.181.27.100"; // Адрес сервера
            int serverPort = 9876;              // Порт сервера

            DatagramSocket clientSocket = new DatagramSocket();

            String message = "Привет, сервер!";
            byte[] sendData = message.getBytes();

            InetAddress serverInetAddress = InetAddress.getByName(serverAddress);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverInetAddress, serverPort);

            clientSocket.send(sendPacket);
            System.out.println("Сообщение отправлено серверу.");

            // Прием ответа от сервера
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Ответ от сервера: " + receivedMessage);

            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
