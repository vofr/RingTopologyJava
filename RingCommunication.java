import java.io.*;
import java.net.*;

public class RingCommunication implements Runnable {

    private int id;
    private int port;
    private int nextPort;
    private String address;
    private ServerSocket serverSocket;
    private Socket prevSocket;
    private Socket nextSocket;
    private String nextAddress;

    public RingCommunication(String address,int id, int port, int nextPort, String nextAddress) {
        this.address = address;
        this.id = id;
        this.port = port;
        this.nextPort = nextPort;
        this.nextAddress = nextAddress;
    }
    public void startServer(){
        try {
        	//initiate server
			serverSocket = new ServerSocket(port);
	        System.out.println("Instance " + id + " started on port " + port);
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void startSocket() {
    	try {
    		//initiate socket to transmit info from next node
    		nextSocket = new Socket(nextAddress, nextPort);
            System.out.println("socket info ("+this.id+"): "+ nextSocket);
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void run() {
        BufferedReader in = null;
        DataOutputStream out = null;
        BufferedReader c = null;
        try {
        	//initiate socket to receive info from prev node
        	prevSocket = serverSocket.accept();
            System.out.println("Client conectat: ("+id+"): " + prevSocket);
            String line = null;
            int value=0;
            while (value<100) {
            	//read from prev
                in = new BufferedReader(new InputStreamReader(prevSocket.getInputStream()));
                //send to next
                out = new DataOutputStream(nextSocket.getOutputStream());
                //data from command line
                c = new BufferedReader(new InputStreamReader(System.in));
            	if (in.ready()) {
                    line = in.readLine();
                    value=Integer.parseInt(line);
                    if(value>=100) {
                    	out.writeBytes(value + "\n");
                        out.flush();
                    	continue;
                    }
                    System.out.println("Instance " + id + " received value " + value);
                    value++;
                    out.writeBytes(value + "\n");
                    out.flush();
                    System.out.println("Instance " + id + " sent value " + value + " to instance " + (id % 3 + 1)); //1->2 2->3 3->1
                    }
            	 else {
                	if (c.ready()) {
                        line = c.readLine();
                        value=Integer.parseInt(line);
                        if(value>=100) {
                        	out.writeBytes(value + "\n");
                            out.flush();
                        	continue;
                        }
                        System.out.println("Instance " + id + " received value " + value);
                        value++;
                        out.writeBytes(value + "\n");
                        out.flush();
                        System.out.println("Instance " + id + " sent value " + value + " to instance " + (id % 3 + 1));
                                      
                    }
                }
                Thread.sleep(100);
            }
        } catch (IOException e) {
            System.out.println("Instance " + id + " encountered an exception: " + e);
        } catch (InterruptedException e) {
            System.out.println("Instance " + id + " interrupted: " + e);
        } finally {
            // Close connections and server socket
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (c != null) c.close();
                if (prevSocket != null) prevSocket.close();
                if (nextSocket != null) nextSocket.close();
                if (serverSocket != null) serverSocket.close();
                System.out.println("Instance: " + id + " terminated");
            } catch (IOException e) {
                System.out.println("Instance " + id + " failed to close connections: " + e);
            }
        }
    }

    public static void main(String args[]) {
        int id1 = 1;
        int port1 = 180;
        String address1="127.0.0.7";
        int id2 = 2;
        int port2 = 181;
        String address2="127.0.0.8";
        int id3 = 3;
        int port3 = 182;
        String address3="127.0.0.9";

        RingCommunication instance1 = new RingCommunication(address1,id1, port1, port2,address2);
        RingCommunication instance2 = new RingCommunication(address2,id2, port2, port3,address3);
        RingCommunication instance3 = new RingCommunication(address3,id3, port3, port1,address1);
        
        instance1.startServer();
        instance2.startServer();
        instance3.startServer();
        
        instance1.startSocket();
        instance2.startSocket();
        instance3.startSocket();

        Thread thread1 = new Thread(instance1);
        Thread thread2 = new Thread(instance2);
        Thread thread3 = new Thread(instance3);

        thread1.start();
        thread2.start();
        thread3.start();
    }
}

