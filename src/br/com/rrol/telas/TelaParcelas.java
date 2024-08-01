/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rrol.telas;

import br.com.rrol.dal.ModuloConexao;
import static br.com.rrol.telas.TelaProcesso.txtId;
import static br.com.rrol.telas.TelaProcesso.txtVVolu;
import java.awt.Color;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Advogados
 */
public class TelaParcelas extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    public static int contratoSelecionado;
    public static int acordoSelecionado;
    public static String tipoAcordoSelecionado;
    public static int parcelaSelecionada;
    public static String denunc;
    public static String stsAcordo;
    public static int idAcordo;

    /**
     * Creates new form TelaParcelas
     */
    public TelaParcelas() {
        initComponents();
        conexao = ModuloConexao.conector();
        denunc = "N";
        stsAcordo = "Em dia";
    }

    private void setarParcelas() {

        int tipoAcordo = TelaProcesso.tblAcordos.getSelectedRow();
        Object valor = TelaProcesso.tblAcordos.getModel().getValueAt(tipoAcordo, 4);
        tipoAcordoSelecionado = valor.toString();

        if (tipoAcordoSelecionado.equals("Parcelado")) {

            String sql = "SELECT pago, idparcelas, contrato, prclatual, vlrparcela, notafiscal FROM tbparcelas WHERE idacordo = ?";

            try {
                pst = conexao.prepareStatement(sql);

                int setar = TelaProcesso.tblAcordos.getSelectedRow();
                acordoSelecionado = Integer.parseInt(TelaProcesso.tblAcordos.getModel().getValueAt(setar, 0).toString());

                pst.setInt(1, acordoSelecionado);

                rs = pst.executeQuery();

                // Cria um modelo de tabela personalizado
                DefaultTableModel model = new DefaultTableModel() {
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        if (columnIndex == 0) {
                            return Boolean.class;
                        }
                        return super.getColumnClass(columnIndex);
                    }
                };

                // Preenche o modelo com os dados do ResultSet
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Adiciona colunas ao modelo
                for (int i = 1; i <= columnCount; i++) {
                    model.addColumn(metaData.getColumnLabel(i));
                }

                // Adiciona linhas ao modelo
                while (rs.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        if (i == 0) {
                            row[i] = rs.getInt(i + 1) == 1; // Converte 0/1 para booleano
                        } else {
                            row[i] = rs.getObject(i + 1);
                        }
                    }
                    model.addRow(row);
                }

                // Define o modelo na tabela
                tblParc.setModel(model);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                System.out.print(e);
            }
        } else {
            String sql = "SELECT idacordo,valoracordo,dtpvenc as Dt_da_Baixa FROM tbacordo WHERE idacordo = ?";

            try {
                pst = conexao.prepareStatement(sql);

                int setar = TelaProcesso.tblAcordos.getSelectedRow();
                acordoSelecionado = Integer.parseInt(TelaProcesso.tblAcordos.getModel().getValueAt(setar, 0).toString());

                pst.setInt(1, acordoSelecionado);

                rs = pst.executeQuery();

                tblParc.setModel(DbUtils.resultSetToTableModel(rs));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                System.out.print(e);
            }
        }

    }

    private void setarText() {

        String sql = "select idparcelas, prclatual, vlrparcela, observacao from TBPARCELAS where idparcelas = ?";

        try {
            pst = this.conexao.prepareStatement(sql);
            int setar = tblParc.getSelectedRow();
            parcelaSelecionada = Integer.parseInt(tblParc.getModel().getValueAt(setar, 1).toString());

            pst.setInt(1, parcelaSelecionada);
            rs = pst.executeQuery();

            if (rs.next()) {
                // Debug: verificar número de colunas no ResultSet
                try {
                    // Testar leitura de cada coluna individualmente
                    String idParcelas = rs.getString(1);
                    int nParcela = rs.getInt(2);
                    String vlrParcela = rs.getString(3);
                    String obs = rs.getString(4);

                    // Atualize os campos com base nos dados do banco de dados
                    txtId.setText(idParcelas);
                    txtNParcela.setText(Integer.toString(nParcela));
                    txtVlrParc.setText(vlrParcela);
                    txtObs.setText(obs);

                } catch (SQLException e) {
                    System.out.println("Erro ao acessar coluna no ResultSet:");
                    System.out.println("Número de colunas: ");
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void setarCbx() {

        String sql = "select denunciar,stsacordo from tbacordo where idacordo = ?";

        try {
            pst = this.conexao.prepareStatement(sql);

            int setar = TelaProcesso.tblAcordos.getSelectedRow();
            acordoSelecionado = Integer.parseInt(TelaProcesso.tblAcordos.getModel().getValueAt(setar, 0).toString());

            pst.setInt(1, acordoSelecionado);

            rs = pst.executeQuery();

            if (rs.next()) {

                String denunciar = rs.getString(1);
                String pago = rs.getString(2);

                if (denunciar.equals("S")) {
                    cbxDenunciar.setSelected(true);
                    denunc = "S";
                    stsAcordo = "Em atraso";
                }

                if (pago.equals("Pago")) {
                    cbxQuit.setSelected(true);
                    stsAcordo = "Pago";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void salvarPagamento() {

        String sqlParcelas = "UPDATE tbparcelas SET pago = ? WHERE idparcelas = ?";

        try {
            for (int i = 0; i < tblParc.getRowCount(); i++) {
                boolean pago = (Boolean) tblParc.getValueAt(i, 0);
                int idParcela = (Integer) tblParc.getValueAt(i, 1);

                // Atualiza o status de pagamento
                pst = this.conexao.prepareStatement(sqlParcelas);
                pst.setInt(1, pago ? 1 : 0);
                pst.setInt(2, idParcela);
                pst.executeUpdate();

                // Se a parcela foi paga, calcula e atualiza o desconto
                if (pago) {
                    float desconto;
                    String vlrParcelaTxt;
                    float vlrParcela;
                    float prctVolu;

                    String sql2 = "SELECT Pa.descontado, Pa.vlrparcela, Po.porcentvolu FROM tbparcelas AS Pa INNER JOIN tbprocessos AS Po ON (Pa.idprocesso = Po.idprocesso) WHERE idparcelas = ?";
                    try {
                        PreparedStatement pst2 = this.conexao.prepareStatement(sql2);
                        pst2.setInt(1, idParcela);
                        ResultSet rs2 = pst2.executeQuery();

                        if (rs2.next()) {
                            desconto = rs2.getFloat(1);
                            vlrParcelaTxt = rs2.getString(2);
                            vlrParcela = Float.parseFloat(vlrParcelaTxt.replace(".", "").replace(",", "."));
                            prctVolu = rs2.getFloat(3);

                            desconto = vlrParcela * (prctVolu / 100);

                            //Verificar o valor desconto: System.out.println(desconto);
                            String sql3 = "UPDATE tbparcelas SET descontado = ? WHERE idparcelas = ?";
                            try {
                                PreparedStatement pst3 = this.conexao.prepareStatement(sql3);
                                pst3.setFloat(1, desconto);
                                pst3.setInt(2, idParcela);
                                pst3.executeUpdate();
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Erro 3: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Erro 2: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    String sql3 = "UPDATE tbparcelas SET descontado = ? WHERE idparcelas = ?";
                    try {
                        PreparedStatement pst3 = this.conexao.prepareStatement(sql3);
                        pst3.setFloat(1, 0.0f);
                        pst3.setInt(2, idParcela);
                        pst3.executeUpdate();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Erro 3: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            JOptionPane.showMessageDialog(null, "Alterações salvas com sucesso!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar as alterações: " + e.getMessage());
            e.printStackTrace();

        }
    }

    private void salvarAltParc() {

        String sql = "update tbparcelas set vlrparcela = ?, observacao = ? where idparcelas = ?";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtVlrParc.getText());
            pst.setString(2, txtObs.getText());
            pst.setString(3, txtId.getText());

            int adicionar = pst.executeUpdate();

            if (adicionar > 0) {
                //System.out.println(adicionar);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            System.out.println(e);
        }
    }

    private void salvarAltAcordo() {

        String sql = "update tbacordo set denunciar = ?, stsacordo = ? where idacordo = ?";

        try {

            int setar = TelaProcesso.tblAcordos.getSelectedRow();
            idAcordo = Integer.parseInt(TelaProcesso.tblAcordos.getModel().getValueAt(setar, 0).toString());
            //System.out.println(idAcordo);
            pst = conexao.prepareStatement(sql);
            pst.setString(1, denunc);
            if (denunc.equals("S")) {
                pst.setString(2, "Denunciar");
            } else {
                pst.setString(2, stsAcordo);
            }
            pst.setInt(3, idAcordo);

            int adicionar = pst.executeUpdate();

            if (adicionar > 0) {
                System.out.println("alterações feitas no acordo");

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            System.out.println(e);
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

        btnSalvar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblParc = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        lblVal = new javax.swing.JLabel();
        txtVlrParc = new javax.swing.JTextField();
        txtNParcela = new javax.swing.JTextField();
        lblNParcela = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtObs = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        cbxDenunciar = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        cbxQuit = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        btnSalvar.setText("Salvar");
        btnSalvar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblParc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Pago", "Id", "Contrato", "Nº Parcela", "Valor da Parcela", "Data da Parcela", "N.F"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblParc.getTableHeader().setResizingAllowed(false);
        tblParc.getTableHeader().setReorderingAllowed(false);
        tblParc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblParcMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblParc);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 819, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblVal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblVal.setText("Valor da Parcela:");

        txtNParcela.setEnabled(false);

        lblNParcela.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblNParcela.setText("Nº da parcela:");

        txtId.setText("0");
        txtId.setEnabled(false);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("ID:");

        txtObs.setColumns(20);
        txtObs.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        txtObs.setLineWrap(true);
        txtObs.setRows(5);
        txtObs.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane2.setViewportView(txtObs);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Denunciar acordo:");

        cbxDenunciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxDenunciarActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Acordo quitado:");

        cbxQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxQuitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblVal)
                            .addComponent(lblNParcela)
                            .addComponent(jLabel3))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(txtVlrParc, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbxDenunciar))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtId, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNParcela, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbxQuit))))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNParcela)
                            .addComponent(txtNParcela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGap(4, 4, 4)
                            .addComponent(jLabel1))
                        .addComponent(cbxQuit, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtVlrParc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblVal))
                    .addComponent(jLabel4)
                    .addComponent(cbxDenunciar, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(193, 193, 193))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(47, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(361, 361, 361)))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(btnSalvar)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        setarParcelas();
        setarCbx();
        System.out.println(denunc);
        System.out.println(stsAcordo);
    }//GEN-LAST:event_formWindowOpened

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        // TODO add your handling code here:
        salvarPagamento();
        salvarAltParc();
        salvarAltAcordo();

    }//GEN-LAST:event_btnSalvarActionPerformed

    private void tblParcMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblParcMouseClicked
        // TODO add your handling code here:
        setarText();
    }//GEN-LAST:event_tblParcMouseClicked

    private void cbxDenunciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxDenunciarActionPerformed
        // TODO add your handling code here:

        if (cbxQuit.isSelected()) {
            int confirma = JOptionPane.showConfirmDialog(null, "O acordo está constando como pago, deseja marca-lo para denuncia ?", "Atenção!", JOptionPane.YES_NO_OPTION);
            if (confirma == JOptionPane.YES_OPTION) {
                if (cbxDenunciar.isSelected()) {
                    denunc = "S";
                    System.out.println(denunc);
                } else {
                    denunc = "N";
                    System.out.println(denunc);
                }
            } else{
                cbxDenunciar.setSelected(false);
            }
        } else {
            if (cbxDenunciar.isSelected()) {
                denunc = "S";
                System.out.println(denunc);
            } else {
                denunc = "N";
                System.out.println(denunc);
            }
        }
    }//GEN-LAST:event_cbxDenunciarActionPerformed

    private void cbxQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxQuitActionPerformed
        // TODO add your handling code here:
        if (cbxQuit.isSelected()) {
            stsAcordo = "Pago";
            System.out.println(stsAcordo);
        } else {
            stsAcordo = "Em dia";
            System.out.println(stsAcordo);
        }
    }//GEN-LAST:event_cbxQuitActionPerformed

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
            java.util.logging.Logger.getLogger(TelaParcelas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaParcelas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaParcelas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaParcelas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaParcelas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSalvar;
    private javax.swing.JCheckBox cbxDenunciar;
    private javax.swing.JCheckBox cbxQuit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblNParcela;
    private javax.swing.JLabel lblVal;
    private javax.swing.JTable tblParc;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNParcela;
    private javax.swing.JTextArea txtObs;
    private javax.swing.JTextField txtVlrParc;
    // End of variables declaration//GEN-END:variables

}
