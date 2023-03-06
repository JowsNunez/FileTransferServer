
package com.fileserver.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JOSÉ ALFREDO NÚÑEZ AGUIRRE 
 * HIRAM GARCIA HERMOSILLO
 * KEVIN DANIEL RIOS RANCANO
 * GABRIEL FRANCISCO PINUELAS RAMOS
 */
public class Server implements Runnable {

    private int MAX_CONNECTIONS = 10;
    private static int PORT = 9090;
    private final ExecutorService executor;
    private byte[] buffer = new byte[1024];
    private static Server server;
    private DatagramSocket serverSocket;

    private Server() throws SocketException {
        this.executor = Executors.newFixedThreadPool(MAX_CONNECTIONS);
        this.serverSocket = new DatagramSocket(PORT);
    }

    private static Server getInstance() throws SocketException {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    @Override
    public void run() {

        while (true) {
            try {

                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

                // Crear una tarea para procesar la conexión entrante
                Runnable task = new ServerWorker(serverSocket, receivePacket, buffer);

                // Enviar la tarea al executor para que se ejecute en un hilo
                executor.execute(task);
                Thread.sleep(400);

            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void initServer() {
        try {

            new Thread(getInstance()).start();
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
