/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Interfaz;

import java.io.File;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

/**
 *
 * @author ivanl
 */
public class PantallaVideo extends javax.swing.JPanel {

    private int vol = 50;
    private boolean AccionProgres = true;
    private int x = 0;

    private EmbeddedMediaPlayerComponent player = new EmbeddedMediaPlayerComponent();


    String ruta;

    public void start() {
        this.setLayout(new GridLayout());
        this.add(player);

        //JFileChooser fileChooser = new JFileChooser("Escritorio");
        //FileNameExtensionFilter filter = new FileNameExtensionFilter("Videos", "swf", "avi", "mkv", "mp4", "mp3", "mpg", "wmv", "ogv", "webm");		//
        //fileChooser.addChoosableFileFilter(filter);
        // if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        //file = fileChooser.getSelectedFile();
        if (new File(ruta).isFile()) {

            player.getMediaPlayer().playMedia(ruta);

            player.getMediaPlayer().setVolume(vol);

            //jSlider_progreso.setValue(0);
            //setTitle(file.getName() + " - VLCJ Player");
            player.getMediaPlayer().setBrightness(2f);
        }

        //}
    }

    public void setDireccion(String ruta) {
        this.ruta = ruta;

    }

    public PantallaVideo() {

        initComponents();

        setVisible(true);

        player.getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            public void positionChanged(MediaPlayer mp, float posicion) {

                if (AccionProgres) {
                    int value = Math.min(100, Math.round(posicion * 100.0f));
                    //jSlider_progreso.setValue(value);

                }
            }

            public void finished(MediaPlayer mediaPlayer) {
            }//Este metodo no se utiliza....

        });

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        //setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 204, 204));
        //setBounds(new java.awt.Rectangle(300, 300, 0, 0));
        setMinimumSize(new java.awt.Dimension(500, 500));
        //setSize(new java.awt.Dimension(150, 150));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(this);
        this.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 543, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 455, Short.MAX_VALUE)
        );

        //getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
        //pack();
    }// </editor-fold>                        

    // Esta parte es esencial para que el programa fluya bien y no de errores... tienes que tener instalado el VideoLaN en 
    // esta ruta.. el caso de no tenerlas necesitas libvlc.dll y libvlccore.dll librerias del programa Muy IMPORTANTE!!!
    //Sin estas dos dll el programa no funciona y saltan errores de JAVA....
    static {
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:/Program Files/VideoLAN/VLC/");
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
    }

}
