/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilidades;

/**
 *
 * @author Lic. Arnaldo Cantero
 */
public class VistaCompletaImagen extends javax.swing.JDialog {

    public VistaCompletaImagen(String rutaimagen) {
        System.out.println("Form Padre " + this.getParent().getName());
        initComponents();
        this.setLocationRelativeTo(null); //Centrar ventana

        MetodosImagen metodosimagen = new MetodosImagen();
        metodosimagen.LeerImagenExterna(lblImagen, rutaimagen, "PRODUCTO SIN FOTO");
        System.out.println("Se cargo la imagen a la VistaCompleta " + rutaimagen);

        //this.setSize(this.getToolkit().getScreenSize());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        lblImagen = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Vista Completa");
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(600, 600));
        setModal(true);
        setName("dgVistaCompleta"); // NOI18N
        setSize(new java.awt.Dimension(800, 600));
        setType(java.awt.Window.Type.POPUP);

        jScrollPane1.setBorder(null);
        jScrollPane1.setFocusable(false);

        lblImagen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImagen.setToolTipText("");
        lblImagen.setBorder(null);
        lblImagen.setFocusable(false);
        jScrollPane1.setViewportView(lblImagen);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @SuppressWarnings("override")
            public void run() {
                VistaCompletaImagen dialog = new VistaCompletaImagen(null);

                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblImagen;
    // End of variables declaration//GEN-END:variables
}
