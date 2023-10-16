import java.io.*; 
import java.net.*; 
  
class UDPClient { 
    public static void main(String args[]) throws Exception 
    { 
  
    //String message = "HTTP/1.1"; // original message
      String key = "10001001";    // initial vector
      String nextKey = "";        // key used for the next block
      String messageBin = "";     // original message in binary
      String encryptedBin = "";   // encrypted binary message
      String encryptedHex = "";   // encrypted hex message

      BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 

      DatagramSocket clientSocket = new DatagramSocket(); //create a client socket 
  
      InetAddress IPAddress = InetAddress.getByName("111.111.11.111");
  
      byte[] sendData = new byte[64]; 
      byte[] receiveData = new byte[64]; 
  
      String sentence = inFromUser.readLine();      

      // Convert original message to binary
      for (char c : sentence.toCharArray()) {
        String binaryChar = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
        messageBin += binaryChar;
      }

      // XOR in 8-bit blocks beginning with the initial vector
      for (int i = 0; i < messageBin.length(); i += 8) {
        initVect += key + " ";
        String block = messageBin.substring(i, i + 8);
        nextKey = blockXOR(block, key);
        encryptedBin += nextKey;
        key = nextKey;
      }
    
      // Convert encrypted binary string to hex string
      StringBuilder hexBuilder = new StringBuilder();
      for (int i = 0; i < encryptedBin.length(); i += 8) {
        String block = encryptedBin.substring(i, i + 8);
        int decimal = Integer.parseInt(block, 2);
        String hex = String.format("%02X", decimal);
        hexBuilder.append(hex);
        origHex += hex + " ";
      }
      encryptedHex = hexBuilder.toString();

      sendData = encryptedHex.getBytes();

      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 5000); 
  
      clientSocket.send(sendPacket); 
  
      DatagramPacket receivePacket =  new DatagramPacket(receiveData, receiveData.length); 
  
      clientSocket.receive(receivePacket); 
  
      String modifiedSentence =  new String(receivePacket.getData()); 

      System.out.println("FROM SERVER:" + modifiedSentence); 
      clientSocket.close(); 
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
