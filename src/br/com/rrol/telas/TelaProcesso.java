/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rrol.telas;

import java.sql.*;
import br.com.rrol.dal.ModuloConexao;
import static br.com.rrol.telas.TelaCadProcesso.txtId;
import java.awt.Color;
import java.util.Locale;
import java.text.NumberFormat;
import java.awt.HeadlessException;
import java.util.Locale;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Advogados
 */
public class TelaProcesso extends javax.swing.JFrame {

    static String txtId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    public static int idAcordoSelecionado;
    public static int processoSelecionado;

    // Componentes visuais declarados como públicos
    public TelaProcesso() {
        initComponents();
        conexao = ModuloConexao.conector();
        atualizarIdUsuario();
        setarCampos();
        setarAcordo();
        stsVolu();

    }

    private void setarCampos() {

        if (this.conexao == null) {

        }

        String sql = "select idprocesso, nome, gcpj, advogado, cpf_cnpj, nprocesso, carteira, contrato, agencia, conta, porcentvolu from tbprocessos where idprocesso = ?";

        try {
            pst = this.conexao.prepareStatement(sql);
            int setar = TelaPrincipal.tblCasos.getSelectedRow();
            processoSelecionado = Integer.parseInt(TelaPrincipal.tblCasos.getModel().getValueAt(setar, 0).toString());

            pst.setInt(1, processoSelecionado);
            rs = pst.executeQuery();

            if (rs.next()) {
                // Debug: verificar número de colunas no ResultSet
                try {
                    // Testar leitura de cada coluna individualmente
                    String idProcesso = rs.getString(1);
                    String nome = rs.getString(2);
                    String gcpj = rs.getString(3);
                    String advogado = rs.getString(4);
                    String cpfCnpj = rs.getString(5);
                    String nprocesso = rs.getString(6);
                    String carteira = rs.getString(7);
                    String contrato = rs.getString(8);
                    String agencia = rs.getString(9);
                    String conta = rs.getString(10);
                    float porcentvolu = rs.getFloat(11);

                    String porcentString = String.format("%.2f", porcentvolu);
                    // Atualize os campos com base nos dados do banco de dados
                    txtId.setText(idProcesso);
                    txtNome.setText(nome);
                    txtGcpj.setText(gcpj);
                    cboAdv.setSelectedItem(advogado);
                    txtDoc.setText(cpfCnpj);
                    txtNProcesso.setText(nprocesso);
                    txtCarteira.setText(carteira);
                    txtContrato.setText(contrato);
                    txtAg.setText(agencia);
                    txtConta.setText(conta);
                    txtPorcent.setText(porcentString);
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

    private void setarAcordo() {

        String sql = "SELECT idacordo, valoracordo, carteira, contrato, tipoacordo FROM tbacordo WHERE idprocesso = ?";

        try {
            pst = conexao.prepareStatement(sql);

            String searchText = txtId.getText();
            pst.setString(1, searchText.toLowerCase());

            rs = pst.executeQuery();

            tblAcordos.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            System.out.print(e);
        }
    }

    private void stsVolu() {
        String searchText = txtId.getText().toLowerCase();
        float totalDescontado = 0.0f;
        float vlrVolu = 0.0f;

        String sql = "SELECT descontado FROM tbparcelas WHERE idprocesso = ?";

        try (PreparedStatement pst = conexao.prepareStatement(sql)) {
            pst.setString(1, searchText);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    
                    totalDescontado += rs.getFloat("descontado");
                    //System.out.println(totalDescontado);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            e.printStackTrace();
            return;  // Encerra o método em caso de erro
        }

        String sql2 = "SELECT valorvolu FROM tbprocessos WHERE idprocesso = ?";
        try (PreparedStatement pst2 = conexao.prepareStatement(sql2)) {
            pst2.setString(1, searchText);
            try (ResultSet rs2 = pst2.executeQuery()) {
                if (rs2.next()) {
                    vlrVolu = rs2.getFloat("valorvolu");
                   //Verificar o valor da volumetria: System.out.println("Valor Volumetria: " + vlrVolu);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            e.printStackTrace();
            return;  // Encerra o método em caso de erro
        }

        float vlrFinal = vlrVolu - totalDescontado;

        String vlrFinalString = String.format(Locale.US, "%.2f", vlrFinal).replace('.', ',');

        //Verificar o valor final da volumetria: System.out.println("Valor final: " + vlrFinalString);

        txtVVolu.setText(vlrFinalString);

        if (vlrVolu > 0 && vlrFinal > 0) {
            txtVVolu.setForeground(Color.red);
            cboStsVolu.setSelectedItem("Pendente");
        } else if (vlrVolu > 0 && vlrFinal <= 0) {
            txtVVolu.setForeground(Color.green);
            cboStsVolu.setSelectedItem("Pago");
        }
    }

    private void salvar() {
        String sql = "update tbprocessos set nome = ?, cpf_cnpj = ?, nprocesso = ?, advogado = ?, gcpj = ?, agencia  = ?,conta = ?, carteira = ?, contrato = ?, stsvolu = ?, valorvolu = ?, stsacordo = ?, valoracordo = ?, porcentvolu = ? where idprocesso = ?";

        try {
            PreparedStatement pst = conexao.prepareStatement(sql);

            pst.setString(1, txtNome.getText());
            pst.setString(2, txtDoc.getText());
            pst.setString(3, txtNProcesso.getText());
            pst.setString(4, cboAdv.getSelectedItem().toString());
            pst.setString(5, txtGcpj.getText());
            pst.setString(6, txtAg.getText());
            pst.setString(7, txtConta.getText());
            pst.setString(8, txtCarteira.getText());
            pst.setString(9, txtContrato.getText());
            pst.setString(10, cboStsVolu.getSelectedItem().toString());

            // Remover pontos de separação de milhar e substituir a vírgula decimal por ponto
            String valorString = txtVVolu.getText().replace(".", "").replace(",", ".");
            float valor = Float.parseFloat(valorString);

            if (valor > 0) {
                txtVVolu.setForeground(Color.red);
                cboStsVolu.setSelectedItem("Pendente");
            }
            pst.setFloat(11, valor);

            pst.setString(12, cboStsAcordo.getSelectedItem().toString());
            pst.setString(13, txtVAcordo.getText());

            // Remover pontos de separação de milhar e substituir a vírgula decimal por ponto para porcentagem
            String porcentString = txtPorcent.getText().replace(".", "").replace(",", ".");
            pst.setFloat(14, Float.parseFloat(porcentString));

            pst.setString(15, txtId.getText());

            int adicionar = pst.executeUpdate();

            if (adicionar > 0) {
                JOptionPane.showMessageDialog(null, "Processo editado com sucesso");
                travar();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            System.out.println(e);
        }
    }

    private void atualizarIdUsuario() {
        String sql = "SELECT iduser FROM tbusuarios WHERE nome = ?";

        try {

            pst = conexao.prepareStatement(sql);

            pst.setString(1, cboAdv.getSelectedItem().toString());

            rs = pst.executeQuery();

            if (rs.next()) {
                // Atualize os campos com base nos dados do banco de dados
                lblIdAdv.setText(String.valueOf(rs.getInt(1)));
            } else {
                lblIdAdv.setText(" ");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            System.out.println(e);
        }

    }

    private void excluir() {
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a exclusão deste processo ?", "Atenção!", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tbprocessos where idprocesso=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtId.getText());
                int excluido = pst.executeUpdate();

                if (excluido > 0) {

                    JOptionPane.showMessageDialog(null, "Processo excluido com sucesso");
                    conexao.close();
                    this.dispose();
                }

            } catch (HeadlessException | SQLException e) {
                JOptionPane.showMessageDialog(null, e);
                System.out.println(e);
            }
        }
    }

    private void abrirAcordo() {
        int setar = tblAcordos.getSelectedRow();
        idAcordoSelecionado = Integer.parseInt(tblAcordos.getModel().getValueAt(setar, 0).toString());

        TelaParcelas parcelas = new TelaParcelas();
        parcelas.setVisible(true);
    }

    private void editar() {
        txtNome.setEditable(true);
        txtDoc.setEditable(true);
        txtNProcesso.setEditable(true);
        cboAdv.setEditable(true);
        txtGcpj.setEditable(true);
        txtAg.setEditable(true);
        txtConta.setEditable(true);
        txtCarteira.setEditable(true);
        txtContrato.setEditable(true);
        cboStsVolu.setEditable(true);
        txtVVolu.setEditable(true);
        cboStsAcordo.setEditable(true);
        txtPorcent.setEditable(true);

        cboAdv.setEditable(true);
        btnSalvar.setEnabled(true);
        btnEditar.setEnabled(false);
    }

    private void travar() {
        txtNome.setEditable(false);
        txtDoc.setEditable(false);
        txtNProcesso.setEditable(false);
        cboAdv.setEditable(false);
        txtGcpj.setEditable(false);
        txtAg.setEditable(false);
        txtConta.setEditable(false);
        txtCarteira.setEditable(false);
        txtContrato.setEditable(false);
        cboStsVolu.setEditable(false);
        txtVVolu.setEditable(false);
        cboStsAcordo.setEditable(false);
        cboAdv.setEditable(false);
        txtPorcent.setEditable(false);

        btnSalvar.setEnabled(false);
        btnEditar.setEnabled(true);
    }

    /**
     * Creates new form TelaCliente
     */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        processo = new javax.swing.JLabel();
        cboAdv = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        txtGcpj = new javax.swing.JTextField();
        txtDoc = new javax.swing.JTextField();
        txtNProcesso = new javax.swing.JTextField();
        lblIdAdv = new javax.swing.JLabel();
        menAcord = new javax.swing.JTabbedPane();
        menResumo = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        menAcordo = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAcordos = new javax.swing.JTable();
        menBusca = new javax.swing.JPanel();
        menMle = new javax.swing.JPanel();
        menIrrecu = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtCarteira = new javax.swing.JTextField();
        txtAg = new javax.swing.JTextField();
        txtConta = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtContrato = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtVAcordo = new javax.swing.JTextField();
        cboStsAcordo = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        btnSalvar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        txtVVolu = new javax.swing.JTextField();
        cboStsVolu = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtPorcent = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        menMenu = new javax.swing.JMenu();
        menNf = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Processo");
        setResizable(false);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Nome:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel2.setText("GCPJ:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("CPF/CNPJ:");

        processo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        processo.setText("Nº Processo:");

        cboAdv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboAdvActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Advogado:");

        txtNome.setEditable(false);

        txtGcpj.setEditable(false);

        txtDoc.setEditable(false);

        txtNProcesso.setEditable(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9)
                    .addComponent(processo)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(cboAdv, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblIdAdv, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .addComponent(txtDoc)
                    .addComponent(txtNProcesso)
                    .addComponent(txtGcpj, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGcpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(processo)
                    .addComponent(txtNProcesso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboAdv, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(lblIdAdv)
                        .addGap(13, 13, 13)))
                .addGap(17, 17, 17))
        );

        menAcord.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        menAcord.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menAcordMouseClicked(evt);
            }
        });

        menResumo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout menResumoLayout = new javax.swing.GroupLayout(menResumo);
        menResumo.setLayout(menResumoLayout);
        menResumoLayout.setHorizontalGroup(
            menResumoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menResumoLayout.createSequentialGroup()
                .addContainerGap(261, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(197, 197, 197))
        );
        menResumoLayout.setVerticalGroup(
            menResumoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menResumoLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        menAcord.addTab("Histórico", menResumo);

        menAcordo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton1.setText("Novo");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        tblAcordos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Id", "Valor do Acordo", "Carteira", "Contrato", "Tipo do acordo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblAcordos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAcordosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblAcordos);

        javax.swing.GroupLayout menAcordoLayout = new javax.swing.GroupLayout(menAcordo);
        menAcordo.setLayout(menAcordoLayout);
        menAcordoLayout.setHorizontalGroup(
            menAcordoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menAcordoLayout.createSequentialGroup()
                .addContainerGap(204, Short.MAX_VALUE)
                .addGroup(menAcordoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menAcordoLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(186, 186, 186))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menAcordoLayout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(408, 408, 408))))
        );
        menAcordoLayout.setVerticalGroup(
            menAcordoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menAcordoLayout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addContainerGap(91, Short.MAX_VALUE))
        );

        menAcord.addTab("Acordos", menAcordo);

        menBusca.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout menBuscaLayout = new javax.swing.GroupLayout(menBusca);
        menBusca.setLayout(menBuscaLayout);
        menBuscaLayout.setHorizontalGroup(
            menBuscaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 910, Short.MAX_VALUE)
        );
        menBuscaLayout.setVerticalGroup(
            menBuscaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 317, Short.MAX_VALUE)
        );

        menAcord.addTab("Busca e apreensão", menBusca);

        menMle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout menMleLayout = new javax.swing.GroupLayout(menMle);
        menMle.setLayout(menMleLayout);
        menMleLayout.setHorizontalGroup(
            menMleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 910, Short.MAX_VALUE)
        );
        menMleLayout.setVerticalGroup(
            menMleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 317, Short.MAX_VALUE)
        );

        menAcord.addTab("MLE", menMle);

        menIrrecu.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout menIrrecuLayout = new javax.swing.GroupLayout(menIrrecu);
        menIrrecu.setLayout(menIrrecuLayout);
        menIrrecuLayout.setHorizontalGroup(
            menIrrecuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 910, Short.MAX_VALUE)
        );
        menIrrecuLayout.setVerticalGroup(
            menIrrecuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 317, Short.MAX_VALUE)
        );

        menAcord.addTab("Irrecuperável", menIrrecu);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Carteira:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Agência:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Conta:");

        txtCarteira.setEditable(false);

        txtAg.setEditable(false);

        txtConta.setEditable(false);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Contrato:");

        txtContrato.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(txtCarteira, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtAg, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 4, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtConta, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(txtContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtCarteira, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(txtContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtAg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtConta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Status Acordo:");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Valor Acordo:");

        txtVAcordo.setEditable(false);

        cboStsAcordo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Em dia", "Pago" }));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel12))
                .addGap(28, 28, 28)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtVAcordo, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboStsAcordo, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(cboStsAcordo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtVAcordo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/rrol/icones/salvar.png"))); // NOI18N
        btnSalvar.setEnabled(false);
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnEditar.setBackground(new java.awt.Color(255, 255, 255));
        btnEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/rrol/icones/edit.png"))); // NOI18N
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/rrol/icones/del.png"))); // NOI18N
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEditar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("ID :");

        txtId.setEnabled(false);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Status Volumetria:");

        txtVVolu.setEditable(false);
        txtVVolu.setText("0.0");

        cboStsVolu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Pendente", "Pago" }));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Valor Volumetria:");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("% Volumetria:");

        txtPorcent.setEditable(false);
        txtPorcent.setText("0.0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(txtVVolu, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPorcent, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboStsVolu, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboStsVolu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVVolu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPorcent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        menMenu.setText("Menu");
        menMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menMenuActionPerformed(evt);
            }
        });

        menNf.setText("Nota Fiscal");
        menNf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menNfActionPerformed(evt);
            }
        });
        menMenu.add(menNf);

        jMenuBar1.add(menMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(95, 95, 95)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(124, 124, 124)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(menAcord, javax.swing.GroupLayout.PREFERRED_SIZE, 923, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addGap(7, 7, 7)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(menAcord, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        new TelaCadAcordo().setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void menMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menMenuActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_menMenuActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        // TODO add your handling code here:
        excluir();
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        // TODO add your handling code here:
        editar();
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        // TODO add your handling code here:
        salvar();
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void cboAdvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboAdvActionPerformed
        // TODO add your handling code here:
        atualizarIdUsuario();
    }//GEN-LAST:event_cboAdvActionPerformed

    private void menAcordMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menAcordMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_menAcordMouseClicked

    private void tblAcordosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAcordosMouseClicked
        // TODO add your handling code here:
        abrirAcordo();
    }//GEN-LAST:event_tblAcordosMouseClicked

    private void menNfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menNfActionPerformed
        // TODO add your handling code here:
        new TelaNf().setVisible(true);
    }//GEN-LAST:event_menNfActionPerformed

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
            java.util.logging.Logger.getLogger(TelaProcesso.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaProcesso.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaProcesso.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaProcesso.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaProcesso().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JComboBox<String> cboAdv;
    private javax.swing.JComboBox<String> cboStsAcordo;
    private javax.swing.JComboBox<String> cboStsVolu;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel lblIdAdv;
    private javax.swing.JTabbedPane menAcord;
    private javax.swing.JPanel menAcordo;
    private javax.swing.JPanel menBusca;
    private javax.swing.JPanel menIrrecu;
    private javax.swing.JMenu menMenu;
    private javax.swing.JPanel menMle;
    private javax.swing.JMenuItem menNf;
    private javax.swing.JPanel menResumo;
    private javax.swing.JLabel processo;
    public static javax.swing.JTable tblAcordos;
    private javax.swing.JTextField txtAg;
    private javax.swing.JTextField txtCarteira;
    private javax.swing.JTextField txtConta;
    private javax.swing.JTextField txtContrato;
    private javax.swing.JTextField txtDoc;
    private javax.swing.JTextField txtGcpj;
    public static javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNProcesso;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtPorcent;
    private javax.swing.JTextField txtVAcordo;
    public static javax.swing.JTextField txtVVolu;
    // End of variables declaration//GEN-END:variables

    void setar_acordo(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
