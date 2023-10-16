import java.io.*;
import java.net.*;
import java.util.*;

class UDPServer {
	public static void main(String args[]) throws Exception {

		InetAddress srvIP = InetAddress.getByName("111.111.11.111");

		DatagramSocket serverSocket = new DatagramSocket(5000, srvIP);

		byte[] receiveData = new byte[64];
		byte[] sendData = new byte[64];

		while (true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			String sentence = new String(receivePacket.getData());
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();

			Calendar rightNow = Calendar.getInstance();
			sendData = rightNow.getTime().toString().getBytes();

			String key = "10001001"; // initial vector (same as used for encryption)

			// Convert the encrypted hex message to binary
			StringBuilder encryptedBinBuilder = new StringBuilder();
			for (int i = 0; i < sentence.length(); i += 2) {
				String hexBlock = sentence.substring(i, i + 2);
				int decimal = Integer.parseInt(hexBlock, 16);
				String binaryBlock = String.format("%8s", Integer.toBinaryString(decimal)).replace(' ', '0');
				encryptedBinBuilder.append(binaryBlock);
			}
			String encryptedBin = encryptedBinBuilder.toString();
			

			// Perform XOR in 8-bit blocks beginning with the initial vector
			StringBuilder decryptedBinBuilder = new StringBuilder();
			String prevKey = key; // Initialize with the initial vector
			for (int i = 0; i < encryptedBin.length(); i += 8) {
				String encryptedBlock = encryptedBin.substring(i, i + 8);
				String decryptedBlock = blockXOR(encryptedBlock, prevKey);
				decryptedBinBuilder.append(decryptedBlock);
				prevKey = encryptedBlock;
			}
			String decryptedBin = decryptedBinBuilder.toString();
			

			// Convert the decrypted binary to the original message
			StringBuilder decryptedMessageBuilder = new StringBuilder();
			for (int i = 0; i < decryptedBin.length(); i += 8) {
				String binaryChar = decryptedBin.substring(i, i + 8);
				int decimal = Integer.parseInt(binaryChar, 2);
				char decryptedChar = (char) decimal;
				decryptedMessageBuilder.append(decryptedChar);
			}
			String decryptedMessage = decryptedMessageBuilder.toString();
			
		}

		DatagramPacket sendPacket = new DatagramPacket(decryptedMessage, sendData.length, IPAddress, port);
		serverSocket.send(sendPacket);

	}

	// Perform XOR operation on two binary strings of the same length
	public static String blockXOR(String block1, String block2) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < block1.length(); i++) {
			char char1 = block1.charAt(i);
			char char2 = block2.charAt(i);
			result.append(char1 == char2 ? '0' : '1');
		}
		return result.toString();
	}

}
