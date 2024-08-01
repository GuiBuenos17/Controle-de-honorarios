/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rrol.telas;

import java.sql.*;
import br.com.rrol.dal.ModuloConexao;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Advogados
 */
public class TelaPrincipal extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    public static int idClienteSelecionado;
    public static String func;
    private TelaProcesso telaCliente;

    /**
     * Creates new form TelaPrincipal
     */
    public TelaPrincipal() {
        initComponents();
        conexao = ModuloConexao.conector();
        
    }

    private void setarIdCli() {
        int setar = tblCasos.getSelectedRow();
        idClienteSelecionado = Integer.parseInt(tblCasos.getModel().getValueAt(setar, 0).toString());

        TelaProcesso cliente = new TelaProcesso();
        cliente.setVisible(true);
    }

    private void pesquisarCliente() {
        
        if (func == null){
        String sql = "SELECT idprocesso as Id, nome as Nome, gcpj, contrato as Contrato, advogado as Advogado, stsacordo as Sts_Acordo FROM tbprocessos WHERE LOWER(nome) LIKE LOWER(?) OR LOWER(gcpj) LIKE LOWER(?) OR LOWER(contrato) LIKE LOWER(?)";

        try {
            pst = conexao.prepareStatement(sql);
            // passando o conteúdo da caixa de pesquisa para o ?
            // atenção ao "%" - continuação da string sql

            String searchText = txtPesquisar.getText() + "%";
            pst.setString(1, searchText.toLowerCase());
            pst.setString(2, searchText.toLowerCase());
            pst.setString(3, searchText.toLowerCase());

            rs = pst.executeQuery();

            // a linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela
            tblCasos.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            System.out.print(e);
        }
        } else {
            
                    String sql = "SELECT idprocesso as Id, nome as Nome, gcpj, contrato as Contrato, advogado as Advogado, stsacordo as Sts_Acordo FROM tbprocessos WHERE LOWER(advogado) LIKE LOWER (?) AND LOWER(nome) LIKE LOWER(?) OR LOWER(gcpj) LIKE LOWER(?) OR LOWER(contrato) LIKE LOWER(?)";

        try {
            pst = conexao.prepareStatement(sql);
            // passando o conteúdo da caixa de pesquisa para o ?
            // atenção ao "%" - continuação da string sql
            String advBanco = cboAdv.getSelectedItem().toString();
            pst.setString(1, advBanco);
            String searchText = txtPesquisar.getText() + "%";            
            pst.setString(2, searchText.toLowerCase());
            pst.setString(3, searchText.toLowerCase());
            pst.setString(4, searchText.toLowerCase());

            rs = pst.executeQuery();

            // a linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela
           if (rs.next()){
           String advTbl = rs.getString(5);
           if (advTbl.equals(advBanco)){
            tblCasos.setModel(DbUtils.resultSetToTableModel(rs));
            }
           }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            System.out.print(e);
        }
            
            
        }
    }

    private void pesquisarClieAdv() {
        String sql = "SELECT idprocesso, nome, gcpj, contrato, advogado, stsacordo FROM tbprocessos WHERE advogado = ?";

        try {
            pst = conexao.prepareStatement(sql);

            pst.setString(1, cboAdv.getSelectedItem().toString());

            rs = pst.executeQuery();

            tblCasos.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            System.out.print(e);
        }
    }

    private void pesquisarMax() {
        String sql = "SELECT COUNT(*) FROM TBPROCESSOS where advogado = ?";

        try {
            pst = conexao.prepareStatement(sql);

            pst.setString(1, cboAdv.getSelectedItem().toString());

            rs = pst.executeQuery();
            
            if (rs.next()) {  // Mova o cursor para a primeira linha
                String count = rs.getString(1); // Obtenha o valor da primeira coluna
                lblCount.setText(count);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            System.out.print(e);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblCasos = new javax.swing.JTable();
        txtPesquisar = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cboAdv = new javax.swing.JComboBox<>();
        lblCount = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        menCad = new javax.swing.JMenu();
        menCadUsu = new javax.swing.JMenuItem();
        menCadPro = new javax.swing.JMenuItem();
        menRel = new javax.swing.JMenu();
        menRelSolc = new javax.swing.JMenuItem();
        menRelImp = new javax.swing.JMenuItem();
        menAdv = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Home");
        setBackground(new java.awt.Color(204, 204, 204));
        setForeground(new java.awt.Color(0, 51, 204));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblCasos = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblCasos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nome", "GCPJ", "Contrato", "Advogado", "ST.Acordo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCasos.getTableHeader().setResizingAllowed(false);
        tblCasos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCasosMouseClicked(evt);
            }
        });
        tblCasos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblCasosKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblCasos);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(187, 200, 530, 320));

        txtPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarKeyReleased(evt);
            }
        });
        getContentPane().add(txtPesquisar, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 134, 300, 24));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/rrol/icones/pesquisar.png"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 134, -1, -1));

        jLabel2.setBackground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Advogado:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 557, -1, -1));

        cboAdv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboAdvActionPerformed(evt);
            }
        });
        getContentPane().add(cboAdv, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 552, 140, -1));

        lblCount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCount.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        getContentPane().add(lblCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(432, 610, 38, 37));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/rrol/icones/fundo.png"))); // NOI18N
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        menCad.setText("Cadastro");

        menCadUsu.setText("Cadastro Usuário");
        menCadUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menCadUsuActionPerformed(evt);
            }
        });
        menCad.add(menCadUsu);

        menCadPro.setText("Cadastro Processo");
        menCadPro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menCadProActionPerformed(evt);
            }
        });
        menCad.add(menCadPro);

        jMenuBar1.add(menCad);

        menRel.setText("Telas Honor.");

        menRelSolc.setText("Solicitação de tela");
        menRelSolc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menRelSolcActionPerformed(evt);
            }
        });
        menRel.add(menRelSolc);

        menRelImp.setText("Impressão de tela");
        menRelImp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menRelImpActionPerformed(evt);
            }
        });
        menRel.add(menRelImp);

        jMenuBar1.add(menRel);

        menAdv.setText("Relatório Adv.");
        jMenuBar1.add(menAdv);

        setJMenuBar(jMenuBar1);

        setSize(new java.awt.Dimension(922, 720));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyReleased
        // TODO add your handling code here:
        pesquisarCliente();
    }//GEN-LAST:event_txtPesquisarKeyReleased

    private void menCadUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menCadUsuActionPerformed
        // TODO add your handling code here:
        new TelaCadUsuário().setVisible(true);
    }//GEN-LAST:event_menCadUsuActionPerformed

    private void menCadProActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menCadProActionPerformed
        // TODO add your handling code here:
        new TelaCadProcesso().setVisible(true);
    }//GEN-LAST:event_menCadProActionPerformed

    private void tblCasosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCasosKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_tblCasosKeyPressed

    private void tblCasosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCasosMouseClicked
        // TODO add your handling code here:
        setarIdCli();


    }//GEN-LAST:event_tblCasosMouseClicked

    private void menRelImpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menRelImpActionPerformed
        // TODO add your handling code here:
        new TelaImpressao().setVisible(true);
    }//GEN-LAST:event_menRelImpActionPerformed

    private void menRelSolcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menRelSolcActionPerformed
        // TODO add your handling code here:
        new TelaMudMes().setVisible(true);
    }//GEN-LAST:event_menRelSolcActionPerformed

    private void cboAdvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboAdvActionPerformed
        // TODO add your handling code here:
        pesquisarClieAdv();
        pesquisarMax();
    }//GEN-LAST:event_cboAdvActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        System.out.println(func);
    }//GEN-LAST:event_formWindowOpened

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JComboBox<String> cboAdv;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCount;
    private javax.swing.JMenu menAdv;
    public static javax.swing.JMenu menCad;
    public static javax.swing.JMenuItem menCadPro;
    public static javax.swing.JMenuItem menCadUsu;
    public static javax.swing.JMenu menRel;
    public static javax.swing.JMenuItem menRelImp;
    public static javax.swing.JMenuItem menRelSolc;
    public static javax.swing.JTable tblCasos;
    private javax.swing.JTextField txtPesquisar;
    // End of variables declaration//GEN-END:variables
}
