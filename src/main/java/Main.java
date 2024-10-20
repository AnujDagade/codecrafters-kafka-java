import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args){
    System.err.println("Logs from your program will appear here!");

    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    int port = 9092;
    try {
      serverSocket = new ServerSocket(port);
      serverSocket.setReuseAddress(true);
      System.out.println("Server started, waiting for client connection...");
      clientSocket = serverSocket.accept();
      System.out.println("Client connected!");

      InputStream is = clientSocket.getInputStream();

      byte[] buffer = new byte[8];
      byte[] requestApikey = new byte[2];
      byte[] requestApiVersion = new byte[2];
      byte[] correlationId = new byte[4];
      int bytesRead = is.read(buffer,0,12);

      if(bytesRead != -1){
        System.out.println("bytes read "+bytesRead);
        throw new IOException("Not enough data");
      }
      System.arraycopy(buffer, 4, requestApikey, 0, 2);
      System.arraycopy(buffer, 6, requestApiVersion, 0, 2);
      System.arraycopy(buffer, 8, correlationId, 0, 4);

      System.out.println("Received data: " + bytesToHex(buffer));


      OutputStream stream = clientSocket.getOutputStream();
//      byte[] data1 = new byte[] {0,0,0,5};
//      byte[] data2 = new byte[] {0,0,0,7};
//      stream.write(requestApikey);
//      stream.write(requestApiVersion);
      stream.write(correlationId);

    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    } finally {
      try {
        if (clientSocket != null) {
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
    }
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }
}
