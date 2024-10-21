import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.util.List;

public class Main {
  public static void main(String[] args){
    System.err.println("Logs from your program will appear here!");

    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    int port = 9092;
    byte[] buffer = new byte[12];
    byte[] reqeustLength = new byte[4];
    byte[] requestApikey = new byte[2];
    byte[] requestApiVersion = new byte[2];
    byte[] correlationId = new byte[4];
    final int[] VALID_VERSIONS = {1,2,3,4};
    try {
      serverSocket = new ServerSocket(port);
      serverSocket.setReuseAddress(true);
      System.out.println("Server started, waiting for client connection...");
      clientSocket = serverSocket.accept();
      System.out.println("Client connected!");

      InputStream is = clientSocket.getInputStream();

      //TODO use readNBytes method
      int bytesRead = is.read(buffer);

      if(bytesRead == -1){
        System.out.println("bytes read "+bytesRead);
        throw new IOException("Not enough data");
      }
      System.arraycopy(buffer, 0, reqeustLength, 0, 4);
      System.arraycopy(buffer, 4, requestApikey, 0, 2);
      System.arraycopy(buffer, 6, requestApiVersion, 0, 2);
      System.arraycopy(buffer, 8, correlationId, 0, 4);

      System.out.println("Received data: " + bytesToHex(buffer));

      boolean isVersionValid =  Arrays.asList(VALID_VERSIONS).contains(ByteBuffer.wrap(requestApiVersion).getShort());

      OutputStream stream = clientSocket.getOutputStream();


      stream.write(reqeustLength);
//      stream.write(requestApikey);
//      stream.write(requestApiVersion);
      stream.write(correlationId);
      if(!isVersionValid){
        System.out.println("Invalid version");
        stream.write(new byte[] {0,35});
        throw new IOException("Invalid version");
      }

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
