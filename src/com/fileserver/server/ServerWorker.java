
package com.fileserver.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

/**
 *
 * @author Jose Alfredo Nuñez Aguirre
 */
public class ServerWorker implements Runnable {

    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    private DatagramSocket socket;
    private byte[] buffer;

    ServerWorker(DatagramSocket socket, DatagramPacket receivePacket, byte[] buffer) {
        this.buffer = buffer;
        this.socket = socket;
        this.receivePacket = receivePacket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                this.receivePacket = new DatagramPacket(buffer, buffer.length);
                this.socket.receive(receivePacket);
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                // si el meensaje es Connection indica que el cliente se acaba de conectar
                if (message.equals("Connection")) {
                    System.out.println("Client: " +this.receivePacket.getSocketAddress());

                    // se muestra el directorio raíz 
                    String directorio = System.getProperty("user.dir");

                    File carpeta = new File(directorio);
                    File[] archivos = carpeta.listFiles();
                    String msg = "";
                    for (File archivo : archivos) {
                        msg += String.format(" %s (%s) - %.2f kb \n",
                                archivo.getName(),
                                archivo.isDirectory() ? "Carpeta" : "Archivo",
                                (double) archivo.length() / 1024);

                    }
                    // se envia los archivos y carpetas del directorio raiz (del proyecto)
                    this.sendPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, receivePacket.getAddress(), receivePacket.getPort());
                    this.socket.send(sendPacket);

                } else {
                    
                    System.out.println("Client:" +this.receivePacket.getSocketAddress() + "* * * File Request " + message);
                    // se verificara si el archivo solicitado existe, creando una instancia de File  
                    File file = new File(message);
                    if (file.exists()) {

                        byte[] data = new byte[(int) file.length()];
                        FileInputStream fis = new FileInputStream(file);
                        fis.read(data);
                        int offset = 0;
                        while (offset < data.length) {
                            // se calcula el tamanio de las partes del paquete de acuerdo al buffer
                            int length = Math.min(buffer.length, data.length - offset);
                            // se crean los bytes de la parte calculada desde el offset + el tamanio del parte del paquete
                            byte[] packet = Arrays.copyOfRange(data, offset, offset + length);
                            // se envia el paquete ya dividido 
                            sendPacket = new DatagramPacket(packet, length, receivePacket.getAddress(), receivePacket.getPort());
                            this.socket.send(sendPacket);
                            // el offset aumenta hasta llegar al tamanio mas pequenio de los datos del paquete
                            offset += length;
                        }

                    } else {
                        // si el archivo no existe se envia un datagramPacket con el ensaje File NotFound
                        String msg = "File Not Found";
                        sendPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, receivePacket.getAddress(), receivePacket.getPort());
                        this.socket.send(sendPacket);
                    }

                }
                //

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
