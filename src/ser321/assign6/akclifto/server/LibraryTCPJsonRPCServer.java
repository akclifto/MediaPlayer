package ser321.assign6.akclifto.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Copyright 2020 Adam Clifton, akclifto@asu.edu
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * This module acts as the View layer for your application.
 * The 'MediaLibraryGui' class actually builds the Gui with all
 * the components - buttons, text fields, text areas, panels etc.
 * This class should be used to write the logic to add functionality
 * to the Gui components.
 * You are free add more files and further modularize this class's
 * functionality.
 * <p>
 * Purpose: LibraryTCPJsonRPCServer is the class to create a threaded socket
 * server that implements JsonRPC method calls that will be called on the LibraryServer.
 *
 * @author Adam Clifton (akclifto@asu.edu)
 * Software Engineering, ASU
 * @version April 2020
 */
public class LibraryTCPJsonRPCServer extends Thread {

    private Socket conn;
    private int id;
    private LibraryServerSkeleton skeleton;

    /**
     * Constructor
     *
     * @param sock     : socket used for thread
     * @param id       : unique id to track thread
     * @param sLibrary : interface of library to pass to Server
     */
    public LibraryTCPJsonRPCServer(Socket sock, int id, Library sLibrary) {
        this.conn = sock;
        this.id = id;
        skeleton = new LibraryServerSkeleton(sLibrary);
    }

    /**
     * Method to implement input and output streams from socket server connection.
     *
     * @return void.
     */
    public void run() {
        try {
            OutputStream outSock = conn.getOutputStream();
            InputStream inSock = conn.getInputStream();
            byte[] clientInput = new byte[4096];
            int num = inSock.read(clientInput, 0, 4096);
            if (num != -1) {

                String request = new String(clientInput, 0, num);
                System.out.println("Request is: " + request);

                String response = skeleton.callMethod(request);
                byte[] clientOutput = response.getBytes();
                outSock.write(clientOutput, 0, clientOutput.length);
                System.out.println("response is: " + response);
            }
            inSock.close();
            outSock.close();
            conn.close();
        } catch (Exception ex) {
            System.out.println("Exception in Server Run: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    /**
     * Entry point of server program.  Method will initiate LibraryServer, set up socket and port,
     * wait for connection, then send connections to a new thread.
     *
     * @param args : command line args input
     * @return void
     */
    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {

        Socket sock;
        Library sLibrary = LibraryServer.getInstance();
        int id = 0;
        try {

            if (args.length != 1) {
                System.out.println("Usage: java ser321.assign6.akclifto.server.LibraryTCPJsonRPCServer {portNum}");
                System.exit(0);
            }

            int portNum = Integer.parseInt(args[0]);
            if (portNum <= 8000) {
                portNum = 8888;
            }
            ServerSocket server = new ServerSocket(portNum);

            while (true) {
                System.out.println("Library Server is waiting for connection to port " + portNum);
                sock = server.accept();  //will wait for connection here
                System.out.println("Library Server connect to client: " + id);
                LibraryTCPJsonRPCServer serverThread = new LibraryTCPJsonRPCServer(sock, id++, sLibrary);
                serverThread.start();
            }
        } catch (Exception ex) {
            System.out.println("Exception in RPCServer Main: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
