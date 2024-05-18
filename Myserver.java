package server;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Myserver implements Runnable{ 

    private ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;
    public Myserver(){
        connections = new ArrayList<>();
        done =false;
    }
     public static void main(String[] args){
        Myserver s = new Myserver();
        s.run();
    }
    
    @Override
    public void run(){
        try {
            server = new ServerSocket(80);
            pool = Executors.newCachedThreadPool();
            while(!done){
            Socket client = server.accept();
            ConnectionHandler handler = new ConnectionHandler(client);
            connections.add(handler);
            pool.execute(handler);
            }
        } catch (IOException ex) {
            try {
                //ex.printStackTrace();
                shutdown();
            } catch (IOException ex1) {
               // Logger.getLogger(Myserver.class.getName()).log(Level.SEVERE, null, ex1);
            }
            
        }
        
    }
    public void broadcast(String messge){
        for(ConnectionHandler ch: connections){
            if(ch!=null){
                ch.sendMessage(messge);
            }
        }
    }
    public void shutdown() throws IOException{
        done = true;
        if(!server.isClosed()){
            server.close();
        }
        
    }
    
    class ConnectionHandler implements Runnable{
        private Socket client;
        private BufferedReader in;
        private PrintWriter ps;
        private String nickname;
        public ConnectionHandler(Socket client){
            this.client = client;
        }
        
        @Override
        public void run(){
            try{
                ps = new PrintWriter(client.getOutputStream(),true);
                in = new BufferedReader (new InputStreamReader(client.getInputStream()));
                
                ps.println("please enter a nickname:");
                nickname = in . readLine();
                
                System.out.println(nickname+"connected!");
                broadcast(nickname+"joined the chat!");
                String message;
                
                while((message= in.readLine())!=null){
                    if(message.startsWith("/nick")){
                        // TODO: handle nickname
                        String[] messageSplit = message.split(" ",2);
                        if(messageSplit.length == 2){
                            broadcast (nickname+" renamed themselves to "+ messageSplit[1]);
                            System.out.println(nickname+" renamed themselves to "+ messageSplit[1]);
                            nickname = messageSplit[1];
                            ps.println("Successfully changed nickname to "+ nickname);
                            
                        }else{
                            ps.println("no nickname provided.");
                        }
                    }else if(message.startsWith("/quit")){
                        // TODO: quit
                        broadcast(nickname+"left the chat!");
                        shutdown();
                    }else{
                        broadcast(nickname+": "+message);
                    }
                }
                
            }catch(IOException e){
                try {
                    shutdown();
                } catch (IOException ex) {
                   // Logger.getLogger(Myserver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        public void sendMessage(String message){
            ps.println(message);
        }
        public void shutdown() throws IOException{
            in.close();
            ps.close();
            if(client.isClosed()){
            } else {
                client.close();
            }
        }
        
    }
   
}
