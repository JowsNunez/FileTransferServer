package com.fileserver.main;

import com.fileserver.server.Main;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author el_fr
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String sCarpAct = System.getProperty("user.dir");
        File carpeta = new File(sCarpAct);

        File[] archivos = carpeta.listFiles();
        if (archivos == null || archivos.length == 0) {
            System.out.println("No hay elementos dentro de la carpeta actual");
            return;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            for (int i = 0; i < archivos.length; i++) {
                File archivo = archivos[i];

                System.out.println(String.format(" %s (%s) - %.2f kb - %s",
                        archivo.getName(),
                        archivo.isDirectory() ? "Carpeta" : "Archivo",
                        (double) archivo.length() / 1024,
                        sdf.format(archivo.lastModified())
                ));
            }
        }

        FileInputStream fi = null;
        try {
            // buffer 
            byte[] buffer = new byte[5000];
            File file = new File("build.xml");

            fi = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fi);
            
            int bytesRead = 0;
            File fout = new File("build.txt");

            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fout))) {
                while ((bytesRead = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                bos.flush();
// TODO code application logic here
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fi.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
