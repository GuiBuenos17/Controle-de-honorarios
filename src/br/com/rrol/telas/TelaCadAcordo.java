/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rrol.telas;

import java.sql.*;
import br.com.rrol.dal.ModuloConexao;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JOptionPane;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Advogados
 */
public class TelaCadAcordo extends javax.swing.JFrame {

    /**
     * Creates new form TelaCadAcordo
     */
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    private String tipo;
    private String quit;
    public static int processoSelecionado;

    public TelaCadAcordo() {
        initComponents();
        conexao = ModuloConexao.conector();
        obterProximoId();
    }

    private void adicionar() {

        String sqlAcordo = "INSERT INTO tbacordo(idprocesso, tipoacordo, valoracordo, valorentrada, carteira, contrato, nparcela, dtpvenc, txjuros, vlrparcela, quitado) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        try {
            // Iniciar uma transação
            conexao.setAutoCommit(false);

            // Primeiro insert na tabela tbacordo
            pst = conexao.prepareStatement(sqlAcordo);
            pst.setInt(1, Integer.parseInt(TelaProcesso.txtId.getText()));
            pst.setString(2, tipo);
            pst.setString(3, txtValAcordo.getText());
            pst.setString(4, txtValEntrada.getText());
            pst.setString(5, txtCarteira.getText());
            pst.setString(6, txtContrato.getText());
            pst.setInt(7, Integer.parseInt(txtNParcela.getText()));
            pst.setString(8, txtDtPVenc.getText());
            pst.setString(9, txtTxJuros.getText());
            pst.setString(10, txtVParcelas.getText());
            pst.setString(11, quit);

            int adicionar = pst.executeUpdate();
            if (adicionar > 0) {
                JOptionPane.showMessageDialog(null, "Acordo adicionado com sucesso");

            }

            // Commit da transação
            conexao.commit();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, "Erro ao adicionar acordo: " + e.getMessage());
            System.out.println(e);
        }
    }

    private void adicionarParcelas() {

        String sqlParcelas = "INSERT INTO tbparcelas(idacordo,idprocesso, valoracordo, carteira, contrato, nparcela, dtpvenc, txjuros, vlrparcela, quitado, prclatual, dataatual, descontado) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            // Iniciar uma transação
            conexao.setAutoCommit(false);

            // Preparar a instrução SQL para inserção das parcelas
            try (PreparedStatement pst = conexao.prepareStatement(sqlParcelas)) {
                // Preencher os valores dos campos do acordo
                int idAcordo = !txtId.getText().isEmpty() ? Integer.parseInt(txtId.getText()) : 0;
                int idProcesso = !TelaProcesso.txtId.getText().isEmpty() ? Integer.parseInt(TelaProcesso.txtId.getText()) : 0;
                float valorAcordo = !txtValAcordo.getText().isEmpty() ? Float.parseFloat(txtValAcordo.getText().replace(".", "").replace(",", ".")) : 0.0f;
                String carteira = txtCarteira.getText();
                int contrato = !txtContrato.getText().isEmpty() ? Integer.parseInt(txtContrato.getText()) : 0;
                int nParcela = !txtNParcela.getText().isEmpty() ? Integer.parseInt(txtNParcela.getText()) : 0;
                LocalDate dataInicial = !txtDtPVenc.getText().isEmpty() ? LocalDate.parse(txtDtPVenc.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy")) : LocalDate.now();
                LocalDate data = !txtDtPVenc.getText().isEmpty() ? LocalDate.parse(txtDtPVenc.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy")) : LocalDate.now();
                float txJuros = !txtTxJuros.getText().isEmpty() ? Float.parseFloat(txtTxJuros.getText().replace(".", "").replace(",", ".")) : 0.0f;
                String vlrParcela = txtVParcelas.getText();
                String quitado = quit;
                float descontado = 0.0f;

                // Adicionar cada parcela
                if (rbParcel.isSelected()) {
                    for (int prclatual = 1; prclatual <= nParcela; prclatual++) {
                        pst.setInt(1, idAcordo);
                        pst.setInt(2, idProcesso);
                        pst.setFloat(3, valorAcordo);
                        pst.setString(4, carteira);
                        pst.setInt(5, contrato);
                        pst.setInt(6, nParcela);
                        pst.setTimestamp(7, java.sql.Timestamp.valueOf(LocalDateTime.of(dataInicial, LocalTime.MIDNIGHT))); // Correctly set TIMESTAMP
                        pst.setFloat(8, txJuros);
                        pst.setString(9, vlrParcela);
                        pst.setString(10, quitado);
                        pst.setInt(11, prclatual);
                        pst.setDate(12, java.sql.Date.valueOf(data)); // Incremented date
                        pst.setFloat(13, descontado); // Correct index for the last parameter

                        // Adicionar a instrução preparada ao lote
                        pst.addBatch();

                        // Avançar a data para o próximo mês para a próxima parcela
                        data = data.plusMonths(1);
                    }

                    // Executar o lote de instruções
                    int[] resultados = pst.executeBatch();

                    // Verificar se todas as inserções foram bem-sucedidas
                    for (int resultado : resultados) {
                        if (resultado == PreparedStatement.EXECUTE_FAILED) {
                            throw new SQLException("Falha ao executar a instrução SQL.");
                        }
                    }

                    // Commit da transação
                    conexao.commit();

                    int confirma = JOptionPane.showConfirmDialog(null, "Deseja fechar esta janela ?", "Atenção!", JOptionPane.YES_NO_OPTION);

                    if (confirma == JOptionPane.YES_OPTION) {
                        this.dispose();
                    } else {
                        limpar();
                    }
                }
            }
        } catch (SQLException e) {
            // Rollback em caso de erro
            try {
                if (conexao != null && !conexao.isClosed()) {
                    conexao.rollback();
                }
            } catch (SQLException ex) {
                System.out.println("Erro ao fazer rollback: " + ex.getMessage());
            }
            JOptionPane.showMessageDialog(null, "Erro ao adicionar parcelas: " + e.getMessage());
            System.out.println(e);
        } finally {
            // Reativar o autocommit e fechar a conexão
            try {
                if (conexao != null && !conexao.isClosed()) {
                    conexao.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                System.out.println("Erro ao reativar o autocommit: " + ex.getMessage());
            }
        }
    }

    private void obterProximoId() {

        try {
            String sql = "SELECT MAX(idacordo) FROM tbacordo";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {
                int ultimoId = rs.getInt(1);
                int proximoId = ultimoId + 1;
                txtId.setText(String.valueOf(proximoId));
            } else {
                // Se não há nenhum registro na tabela, comece com o ID 1
                txtId.setText("1");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void parcelado() {
        txtValEntrada.setEditable(true);
        txtNParcela.setEditable(true);
        txtVParcelas.setEditable(true);
        txtTxJuros.setEditable(true);
        txtDtPVenc.setEditable(true);
        rbSelEntrad.setEnabled(true);
    }

    private void avista() {
        txtValEntrada.setEditable(false);
        txtNParcela.setEditable(false);
        txtVParcelas.setEditable(false);
        txtTxJuros.setEditable(false);
        lblVenc.setText("Data da Baixa:");
        txtDtPVenc.setEditable(true);
        rbSelEntrad.setEnabled(false);
    }

    private void levantamento() {
        txtValEntrada.setEditable(false);
        txtNParcela.setEditable(false);
        txtVParcelas.setEditable(false);
        txtTxJuros.setEditable(false);
        rbSelEntrad.setEnabled(false);
    }

    private void limpar() {
        txtValAcordo.setText(null);
        txtValEntrada.setText(null);
        txtNParcela.setText(null);
        txtDtPVenc.setText(null);
        txtCarteira.setText(null);
        txtContrato.setText(null);
        rbParcel.setSelected(false);
        rbVista.setSelected(true);
        rbLevan.setSelected(true);
        rbtSim.setSelected(false);
        rbtNao.setSelected(false);
        txtTxJuros.setText(null);
        txtVParcelas.setText(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        rbParcel = new javax.swing.JRadioButton();
        rbVista = new javax.swing.JRadioButton();
        rbLevan = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtNParcela = new javax.swing.JTextField();
        lblVenc = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        rbSelEntrad = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtValEntrada = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtTxJuros = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        rbtSim = new javax.swing.JRadioButton();
        rbtNao = new javax.swing.JRadioButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtCarteira = new javax.swing.JTextField();
        txtContrato = new javax.swing.JTextField();
        txtValAcordo = new javax.swing.JFormattedTextField();
        txtDtPVenc = new javax.swing.JFormattedTextField();
        txtId = new javax.swing.JTextField();
        txtVParcelas = new javax.swing.JTextField();
        btnSalvar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cadastro de acordo");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Tipo de acordo"));

        buttonGroup1.add(rbParcel);
        rbParcel.setText("Parcelado");
        rbParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbParcelActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbVista);
        rbVista.setText("À vista");
        rbVista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbVistaActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbLevan);
        rbLevan.setText("Levantamento");
        rbLevan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbLevanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(rbParcel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbVista)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbLevan))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(rbParcel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(rbVista)
                .addComponent(rbLevan))
        );

        jLabel1.setText("Valor do acordo:");

        jLabel4.setText("Número de parcelas:");

        txtNParcela.setEditable(false);
        txtNParcela.setText("0");

        lblVenc.setText("Data do primeiro vencimento:");

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        rbSelEntrad.setEnabled(false);
        rbSelEntrad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbSelEntradActionPerformed(evt);
            }
        });

        jLabel3.setText("Selecione se houver entrada :");

        jLabel2.setText("Valor da entrada:");

        txtValEntrada.setEditable(false);
        txtValEntrada.setEnabled(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rbSelEntrad))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtValEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(rbSelEntrad, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtValEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel8.setText("Taxa de juros:");

        txtTxJuros.setEditable(false);
        txtTxJuros.setText("0,0");

        jLabel9.setText("Valor das Parcelas:");

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setText("Este acordo já foi quitado ?");

        buttonGroup2.add(rbtSim);
        rbtSim.setText("Sim");
        rbtSim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtSimActionPerformed(evt);
            }
        });

        buttonGroup2.add(rbtNao);
        rbtNao.setText("Não");
        rbtNao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtNaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbtSim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(rbtNao)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(rbtSim)
                    .addComponent(rbtNao))
                .addGap(5, 5, 5))
        );

        jLabel12.setText("Carteira:");

        jLabel13.setText("Contrato:");

        txtValAcordo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));

        txtDtPVenc.setEditable(false);
        txtDtPVenc.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));

        txtId.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(txtCarteira, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(jLabel13)
                        .addGap(23, 23, 23)
                        .addComponent(txtContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtValAcordo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel4)))
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNParcela, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTxJuros, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtVParcelas)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblVenc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDtPVenc, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtValAcordo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCarteira, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13)
                            .addComponent(jLabel12))
                        .addGap(160, 160, 160))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNParcela, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txtVParcelas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTxJuros, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblVenc)
                            .addComponent(txtDtPVenc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(293, 293, 293)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSalvar)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void rbLevanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbLevanActionPerformed
        // TODO add your handling code here:
        tipo = "Levantamento";
        levantamento();
    }//GEN-LAST:event_rbLevanActionPerformed

    private void rbParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbParcelActionPerformed
        // TODO add your handling code here:
        tipo = "Parcelado";
        parcelado();
    }//GEN-LAST:event_rbParcelActionPerformed

    private void rbVistaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbVistaActionPerformed
        // TODO add your handling code here:
        tipo = "A vista";
        avista();
    }//GEN-LAST:event_rbVistaActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        // TODO add your handling code here:
        adicionar();
        adicionarParcelas();

    }//GEN-LAST:event_btnSalvarActionPerformed

    private void rbSelEntradActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbSelEntradActionPerformed
        // TODO add your handling code here:
        if (rbSelEntrad.isSelected()) {
            txtValEntrada.setEnabled(true);
        } else {
            txtValEntrada.setEnabled(false);
            txtValEntrada.setText(null);
        }


    }//GEN-LAST:event_rbSelEntradActionPerformed

    private void rbtSimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtSimActionPerformed
        // TODO add your handling code here:
        quit = "sim";
    }//GEN-LAST:event_rbtSimActionPerformed

    private void rbtNaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtNaoActionPerformed
        // TODO add your handling code here:
        quit = "nao";
    }//GEN-LAST:event_rbtNaoActionPerformed

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
            java.util.logging.Logger.getLogger(TelaCadAcordo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaCadAcordo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaCadAcordo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaCadAcordo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaCadAcordo().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSalvar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lblVenc;
    private javax.swing.JRadioButton rbLevan;
    private javax.swing.JRadioButton rbParcel;
    private javax.swing.JRadioButton rbSelEntrad;
    private javax.swing.JRadioButton rbVista;
    private javax.swing.JRadioButton rbtNao;
    private javax.swing.JRadioButton rbtSim;
    private javax.swing.JTextField txtCarteira;
    private javax.swing.JTextField txtContrato;
    private javax.swing.JFormattedTextField txtDtPVenc;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNParcela;
    private javax.swing.JTextField txtTxJuros;
    private javax.swing.JTextField txtVParcelas;
    private javax.swing.JFormattedTextField txtValAcordo;
    private javax.swing.JTextField txtValEntrada;
    // End of variables declaration//GEN-END:variables
}
