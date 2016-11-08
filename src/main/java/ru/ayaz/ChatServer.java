package ru.ayaz;


import javax.jws.soap.SOAPBinding;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

/**
 * Created by ayaz on 11/4/16.
 */
public class ChatServer {

    ServerSocket serverSocket;
    private Map<Long, User> users;
    long newUserNextID = 1;

    public ChatServer(){
        try {
            serverSocket = new ServerSocket(4400);
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public class UserHandler implements Runnable{

        private User user;
        private Socket socket;
        private BufferedReader reader;


        public UserHandler(Socket clientSocket){
            try {
                socket = clientSocket;
                InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(isReader);
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                user = new User();
                user.setWriter(writer);
                user.setId(newUserNextID);
                users.put(newUserNextID, user);
                newUserNextID++;
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        public void run(){

            String message;

            welcomeNewUser();

            try {

                user.setNickName(reader.readLine());
                sendMessageToEveryoneExcept(user.getNickName() + " connected to chat", user.getId());
                while ((message = reader.readLine()) != null) {
                    sendMessageToEveryoneExcept(user.getNickName() + ": " + message, user.getId());
                }
                sendMessageToEveryoneExcept(user.getNickName() + " left chat", user.getId());

                user.getWriter().close();

                Thread.currentThread().interrupt();

            }catch (IOException e){
                e.printStackTrace();
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        }


        private void welcomeNewUser(){
            PrintWriter writer = user.getWriter();
            writer.println("Welcome to Chat");
            writer.println("Please enter your name:\n");
            writer.flush();
        }


        private String  listenForMessage() throws IOException{
            return reader.readLine();
        }


    }


    private void sendMessageToEveryoneExcept(String message, Long exceptId){

        PrintWriter pw;

        System.out.println(message);

        for(Map.Entry<Long,User> entry: users.entrySet()){
            Long key = entry.getKey();
            User value = entry.getValue();
            if((value.getId()) != exceptId){
                pw = value.getWriter();
                pw.println(message);
                pw.flush();
            }
        }

    }




    public void startChat(){
        users = new TreeMap<Long, User>();

        try {
            while (true){
                Socket socket = serverSocket.accept();
                Thread t = new Thread(new UserHandler(socket));
                t.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args){
       ChatServer chat = new ChatServer();
       chat.startChat();

    }

}
