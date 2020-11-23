/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import principal.Principal;

/**
 *
 * @author Arnaldo_Cantero
 */
public class SplashScreen extends javax.swing.JFrame implements Runnable {

    public SplashScreen(java.awt.Frame parent, boolean modal) {
        initComponents();
    }


    public void run() {
        try {
            this.setLocationRelativeTo(null);
            this.setVisible(true);
            Thread.sleep(3000); //Esta en pantalla por 3 segundos
            this.dispose(); //Desaparece
            
            Principal principal = new Principal();
            principal.setVisible(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnPrincipal = new org.edisoncor.gui.panel.Panel();
        panelCurves1 = new org.edisoncor.gui.panel.PanelCurves();
        lmCargando = new org.edisoncor.gui.label.LabelMetric();
        panelImage1 = new org.edisoncor.gui.panel.PanelImage();
        rSProgressMaterial1 = new rojerusan.componentes.RSProgressMaterial();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cargando sistema...");
        setUndecorated(true);
        setResizable(false);

        pnPrincipal.setColorPrimario(new java.awt.Color(0, 51, 102));

        lmCargando.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lmCargando.setText("Cargando sistema...");
        lmCargando.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N

        panelImage1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logo_syschool.png"))); // NOI18N

        javax.swing.GroupLayout panelImage1Layout = new javax.swing.GroupLayout(panelImage1);
        panelImage1.setLayout(panelImage1Layout);
        panelImage1Layout.setHorizontalGroup(
            panelImage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 377, Short.MAX_VALUE)
        );
        panelImage1Layout.setVerticalGroup(
            panelImage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 162, Short.MAX_VALUE)
        );

        rSProgressMaterial1.setForeground(new java.awt.Color(0, 204, 204));
        rSProgressMaterial1.setAnchoProgress(7);

        javax.swing.GroupLayout panelCurves1Layout = new javax.swing.GroupLayout(panelCurves1);
        panelCurves1.setLayout(panelCurves1Layout);
        panelCurves1Layout.setHorizontalGroup(
            panelCurves1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCurves1Layout.createSequentialGroup()
                .addGroup(panelCurves1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCurves1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lmCargando, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelCurves1Layout.createSequentialGroup()
                        .addGap(229, 229, 229)
                        .addComponent(rSProgressMaterial1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 228, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(panelCurves1Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(panelImage1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelCurves1Layout.setVerticalGroup(
            panelCurves1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCurves1Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addComponent(lmCargando, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rSProgressMaterial1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelImage1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnPrincipalLayout = new javax.swing.GroupLayout(pnPrincipal);
        pnPrincipal.setLayout(pnPrincipalLayout);
        pnPrincipalLayout.setHorizontalGroup(
            pnPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelCurves1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnPrincipalLayout.setVerticalGroup(
            pnPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelCurves1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.edisoncor.gui.label.LabelMetric lmCargando;
    private org.edisoncor.gui.panel.PanelCurves panelCurves1;
    private org.edisoncor.gui.panel.PanelImage panelImage1;
    private org.edisoncor.gui.panel.Panel pnPrincipal;
    private rojerusan.componentes.RSProgressMaterial rSProgressMaterial1;
    // End of variables declaration//GEN-END:variables
}
