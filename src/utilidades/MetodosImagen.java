/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilidades;

import conexion.Conexion;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Lic. Arnaldo Cantero
 */
public class MetodosImagen {

    JFileChooser fc;
    Boolean fcEstaCargado = false;

    public void CargarImagenDesdeFC(JLabel ElLabel) {
        CambiarLookSwing("windows"); //Cambiamos el look a Windows

        //Traducir
        UIManager.put("FileChooser.saveButtonText", "Guardar");
        UIManager.put("FileChooser.openButtonText", "Abrir");
        UIManager.put("FileChooser.cancelButtonText", "Cancelar");
        UIManager.put("FileChooser.updateButtonText", "Actualizar");
        UIManager.put("FileChooser.helpButtonText", "Ayuda");
        UIManager.put("FileChooser.saveButtonToolTipText", "Guardar fichero");

        String userDir = System.getProperty("user.home"); //Directorio
        //JFileChooser fc = new JDirectoryChooser(); //Para directorios
        fc = new JFileChooser(userDir + "/Desktop"); //Para archivos

        //Vista previa de imagenes del Fc
        VistaPreviaEnFC vistapreviaenfc = new VistaPreviaEnFC(); //File Chooser FC
        fc.setAccessory(vistapreviaenfc);
        fc.addPropertyChangeListener(vistapreviaenfc);

        fc.setDialogTitle("Buscar imagen o foto"); //El titulo de la ventana buscador
        fc.setFileFilter(new FileNameExtensionFilter("JPG", "jpg"));
        fc.setFileFilter(new FileNameExtensionFilter("PNG", "png"));
        fc.setFileFilter(new FileNameExtensionFilter("JPG & PNG", "jpg", "png"));

        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File imagenseleccionada = fc.getSelectedFile();
            String ruta = imagenseleccionada.getPath();
            ImageIcon imagenImageIcon = new ImageIcon(ruta);
            imagenImageIcon = new ImageIcon(EscalarImage(imagenImageIcon.getImage(), ElLabel));
            ElLabel.setIcon(imagenImageIcon);
            ElLabel.setText("");
            System.out.println("Se cargó la imagen desde el filechooser: " + ruta);
            fcEstaCargado = true;
        } else {
            System.out.println("Cargar Imagen Cancelado");
            fcEstaCargado = false;
        }
        System.out.println("CargarImagenFC esta cargado " + fcEstaCargado);
        CambiarLookSwing("nimbus"); //Cambiamos el look a Nimbus otra vez
    }

    public void GuardarImagen(String rutadestino) {
        //Guardar nuevo imagen
        try {
            if (fcEstaCargado == true) { //Si la FileChooser tiene cargado un file
                String filenombre = fc.getSelectedFile().getName(); //Nombre del archivo
                String fileextension = filenombre.substring(filenombre.lastIndexOf(".") + 1,
                        fc.getSelectedFile().getName().length()); //Extension del archivo
                BufferedImage biImagen = ImageIO.read(fc.getSelectedFile());

                ImageIcon icon = new ImageIcon(biImagen); //BufferedImage a ImageIcon
                Graphics2D g2 = biImagen.createGraphics();
                g2.drawImage(icon.getImage(), 0, 0, icon.getImageObserver());
                g2.dispose();
                // Escribe la imagen
                try {
                    rutadestino = rutadestino + "." + fileextension;

                    File fileimagen = new File(rutadestino);
                    if (fileimagen.exists() == true) {
                        System.out.println("Ya existe, eliminando antes de guardar la nueva imagen");
                        EliminarImagen(rutadestino); //Elimina la imagen por si ya existe, sucede en el caso de modificar imagen 
                    }

                    System.out.println("Guardando imagen... " + rutadestino);
                    ImageIO.write(biImagen, fileextension, new File(rutadestino));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error al guardar imagen... " + ex, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (HeadlessException | IOException e) {
            System.out.println("Error al Guardar Imagen del registro" + e);
        }
    }

    public void LeerImagenExterna(JLabel elLabel, String rutaimagen, String PorDefault) {
        Image imagenExterna;
        File imagefile;
        String ruta;

        imagefile = new File(rutaimagen + ".png");
        ruta = rutaimagen + ".png";
        System.out.println("\nBuscar imagen png: " + ruta + " exist: " + imagefile.exists());
        if (imagefile.exists() == false) {
            imagefile = new File(rutaimagen + ".jpg");
            ruta = rutaimagen + ".jpg";
            System.out.println("Buscar imagen jpg: " + ruta + " exist: " + imagefile.exists());
            if (imagefile.exists() == false) {
                ruta = null;
            }
        }
        if (ruta == null) { //Si no existe la foto
            elLabel.setIcon(null);
            elLabel.setText(PorDefault);
        } else {
            imagenExterna = new ImageIcon(ruta).getImage();
            imagenExterna = EscalarImage(imagenExterna, elLabel);
            elLabel.setIcon(new javax.swing.ImageIcon(imagenExterna));
            elLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); //Centra la imagen
            elLabel.setText(""); //Borra el texto del label
            System.out.println("Se cargó la imagen: " + ruta + "\n");
        }
    }

    public void LeerImagenInterna(JLabel elLabel, String rutaimagen) {
        Image imagenInterna;
        String ruta = "C:\\VentaRopas\\fotoproductos\\imageproducto_0.png";

        try {
            imagenInterna = new ImageIcon(getClass().getResource(rutaimagen + ".png")).getImage();
            if (imagenInterna != null) { //Si png existe
                ruta = rutaimagen + ".png";
            }
        } catch (Exception e) {
            try {
                imagenInterna = new ImageIcon(getClass().getResource(rutaimagen + ".jpg")).getImage();
                if (imagenInterna != null) { //Si jpg existe
                    ruta = rutaimagen + ".jpg";
                }
            } catch (Exception e2) {
                imagenInterna = new ImageIcon(ruta).getImage(); //Si no existe ninguno se pone la imagen por defecto
            }
        }

        imagenInterna = EscalarImage(imagenInterna, elLabel);
        elLabel.setIcon(new javax.swing.ImageIcon(imagenInterna));
        elLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); //Centra la imagen
        System.out.println("Se cargó la imagen: " + ruta);
    }

    private Image EscalarImage(Image imagenInterna, JLabel elLabel) {
        //Mantener relacion
        ImageIcon tmpImagen = new ImageIcon(imagenInterna);
        float delta = ((elLabel.getWidth() * 100) / tmpImagen.getIconWidth()) / 100f;
        if (tmpImagen.getIconHeight() > elLabel.getHeight()) {
            delta = ((elLabel.getHeight() * 100) / tmpImagen.getIconHeight()) / 100f;
        }
        int ancho = (int) (tmpImagen.getIconWidth() * delta);
        int alto = (int) (tmpImagen.getIconHeight() * delta);
        imagenInterna = imagenInterna.getScaledInstance(ancho, alto, Image.SCALE_AREA_AVERAGING);
        return imagenInterna;
    }

    public void EliminarImagen(String rutaimagen) {
        File fichero;

        fichero = new File(rutaimagen + ".png");
        if (fichero.exists() == false) {
            fichero = new File(rutaimagen + ".jpg");
        }

        try {
            System.out.println("Eliminar imagen: " + rutaimagen);
            if (fichero.delete()) { //Eliminar imagen
                System.out.println("La imagen ha sido borrado satisfactoriamente");
            }
        } catch (Exception e) {
            System.out.println("Error al querer eliminar imagen " + e);
        }
    }

    public void CambiarLookSwing(String look) {
        if (look.equals("windows")) {
            look = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        }
        if (look.equals("nimbus")) {
            look = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
        }
        try {
            javax.swing.UIManager.setLookAndFeel(look);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {

        }
    }

    /* public void EscalarImagenAjButton(String rutaimagen, JButton EljButton) {
        // Crea un icono que referencie a la imagen en disco
        ImageIcon iconoOriginal = new ImageIcon(rutaimagen);

        int ancho = 32; // ancho en pixeles que tendra el icono escalado
        int alto = -1; // alto (para que conserve la proporcion pasamos -1)

// Obtiene un icono en escala con las dimensiones especificadas
        ImageIcon iconoEscala = new ImageIcon(iconoOriginal.getImage().getScaledInstance(EljButton.getho, largojbutton, java.awt.Image.SCALE_DEFAULT));
    }*/
    public String ObtenerUltimoID() {
        Metodos metodos = new Metodos();
        String idultimoproducto = "";
        try {
            Conexion con = new Conexion();
            con = con.ObtenerRSSentencia("SELECT MAX(pro_codigo) AS idultimoproducto FROM producto");
            while (con.rs.next()) {
                idultimoproducto = con.rs.getString("idultimoproducto");
            }
            con.DesconectarBasedeDatos();

        } catch (SQLException ex) {
            Logger.getLogger(MetodosImagen.class
                    .getName()).log(Level.SEVERE, null, ex);
            System.out.println(
                    "No se pudo obtener el idultimoproducto: " + idultimoproducto);
        }
        return idultimoproducto;
    }
}
