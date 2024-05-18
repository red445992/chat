
package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class client implements Runnable {

     public static void main(String[] args){
         client c= new client();
         c.run();
     }
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;
    
    @Override
     public void run(){
         try{
             client = new Socket("LocalHost",80);
             
             out = new PrintWriter(client.getOutputStream(),true);
             in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             
             InputHandler inHandler = new InputHandler();
             Thread t = new Thread(inHandler);
             t.start();
             
             String inMsg;
             
             while((inMsg = in.readLine())!=null){
                 System.out.println(inMsg);
             }
         }catch(Exception e){
             shutdown();
         }
     }
     public void shutdown(){
         done = true;
         try{
             in.close();
             out.close();
             if(!client.isClosed()){
                 client.close();
             }
         }catch(Exception e){
                      shutdown();  
               }
         }
     
     class InputHandler implements Runnable{
         
         @Override
         public void run(){
             try{
                 BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                 while(!done){
                     String msg = inReader.readLine();
                     
                     if(msg.equals("/quit")){
                         out.println(msg);
                         inReader.close();
                         shutdown();
                     }else{
                         out.println(msg);
                     }
                 }
             }catch(Exception e){
                 
             }
         }
     }
     
    
}
