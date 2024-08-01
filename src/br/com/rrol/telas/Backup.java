/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rrol.telas;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JOptionPane;

/**
 *
 * @author Advogados
 */
public class Backup extends javax.swing.JPanel {

    /**
     * Creates new form Backup
     */
    public Backup() {
        initComponents();
    }
    
//      private void adicionar() {
//    String sql = "INSERT INTO tbacordo(idprocesso,tipoacordo, valoracordo, valorentrada, carteira, contrato,nparcela, dtpvenc, txjuros, vlrparcela, quitado, prclatual, dataatual) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
//    try {
//        pst = conexao.prepareStatement(sql);
//        pst.setString(1, TelaProcesso.txtId.getText());
//        pst.setString(2, tipo);
//        pst.setString(3, txtValAcordo.getText());
//        pst.setString(4, txtValEntrada.getText());
//        pst.setString(5, txtCarteira.getText());
//        pst.setString(6, txtContrato.getText());
//        pst.setString(7, txtNParcela.getText());
//        pst.setString(9, txtTxJuros.getText());
//        pst.setString(10, txtVParcelas.getText());
//        pst.setString(11, quit);
//        
//
//        // Obter o número de parcelas
//        int numParcelas = Integer.parseInt(txtNParcela.getText());
//
//        // Obter a data de início
//        String dataInicioStr = txtDtPVenc.getText();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        java.util.Date dataUtil = sdf.parse(dataInicioStr); // Parsing string para java.util.Date
//        long timestamp = dataUtil.getTime(); // Obtendo o timestamp
//        java.sql.Date sqlDataInicio = new java.sql.Date(timestamp); // Criando java.sql.Date a partir do timestamp
//
//        // Definir a data do primeiro vencimento (mesma para todas as parcelas)
//        pst.setDate(8, sqlDataInicio);
//
//        // Adicionar as parcelas
//        Calendar calendarioParcela = Calendar.getInstance();
//        calendarioParcela.setTime(dataUtil); 
//        
//        Definir a data da parcela como a data de início
//
//        for (int i = 0; i < numParcelas; i++) {
//            // Definir a parte da parcela atual
//            pst.setInt(12, i + 1);
//
//            // Definir a data da parcela
//            if (i > 0) {
//                calendarioParcela.add(Calendar.MONTH, 1); // Adicionar um mês
//            }
//            java.sql.Date dataParcela = new java.sql.Date(calendarioParcela.getTimeInMillis());
//            pst.setDate(13, dataParcela); // Definir a data atual
//
//            // Executar o comando de inserção para esta parcela
//            pst.executeUpdate();
//        }
//
//        JOptionPane.showMessageDialog(null, "Parcelas adicionadas com sucesso");
//        limpar();
//    } catch (Exception e) {
//        JOptionPane.showMessageDialog(null, "Erro ao adicionar parcelas: " + e.getMessage());
//        System.out.println(e);
//    }
//}
    
    
//    private void adicionar() {
//        try {
//            // Iniciar uma transação
//            conexao.setAutoCommit(false);
//
//            // Primeiro insert na tabela tbacordo
//            String sqlAcordo = "INSERT INTO tbacordo(idprocesso, tipoacordo, valoracordo, valorentrada, carteira, contrato, nparcela, dtpvenc, txjuros, vlrparcela, quitado) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
//            pst = conexao.prepareStatement(sqlAcordo, Statement.RETURN_GENERATED_KEYS);
//            pst.setString(1, TelaProcesso.txtId.getText());
//            pst.setString(2, tipo);
//            pst.setString(3, txtValAcordo.getText());
//            pst.setString(4, txtValEntrada.getText());
//            pst.setString(5, txtCarteira.getText());
//            pst.setString(6, txtContrato.getText());
//
//            String textoNParcela = txtNParcela.getText().trim(); // Remover espaços em branco extras
//            if (!textoNParcela.isEmpty()) {
//                pst.setInt(7, Integer.parseInt(textoNParcela));
//            } else {
//                pst.setNull(7, Types.INTEGER); // Definir como NULL se o campo estiver vazio
//            }
//
//            pst.setString(8, txtDtPVenc.getText());
//            pst.setString(9, txtTxJuros.getText());
//
//            // Verificar se o campo txtVParcelas não está vazio
//            String textoParcelas = txtVParcelas.getText().trim();
//            if (!textoParcelas.isEmpty()) {
//                textoParcelas = textoParcelas.replace(',', '.'); // Substituir vírgula por ponto
//                double valorParcelas = Double.parseDouble(textoParcelas);
//                pst.setDouble(10, valorParcelas);
//            } else {
//                pst.setNull(10, Types.DOUBLE); // Definir como NULL se o campo estiver vazio
//            }
//
//            pst.setString(11, quit);
//
//            int adicionar = pst.executeUpdate();
//            if (adicionar > 0) {
//                JOptionPane.showMessageDialog(null, "Acordo  adicionado com sucesso");
//                limpar();
//            }
//
//            // Commit da transação
//            conexao.commit();
//
//        } catch (Exception e) {
//            // Rollback em caso de erro
//            try {
//                conexao.rollback();
//            } catch (SQLException ex) {
//                System.out.println("Erro ao fazer rollback: " + ex.getMessage());
//            }
//            JOptionPane.showMessageDialog(null, "Erro ao adicionar acordo: " + e.getMessage());
//            System.out.println(e);
//        } finally {
//            // Reativar o autocommit
//            try {
//                conexao.setAutoCommit(true);
//            } catch (SQLException ex) {
//                System.out.println("Erro ao reativar o autocommit: " + ex.getMessage());
//            }
//        }
//    }
//
//private void adicionarParcela() {
//    String sql = "INSERT INTO tbparcelas(idprocesso, valoracordo, valorentrada, carteira, contrato, nparcela, dtpvenc, txjuros, vlrparcela, quitado, prclatual, dataatual) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
//    try {
//        pst = conexao.prepareStatement(sql);
//        pst.setString(1, txtId.getText());
//        pst.setString(2, txtValAcordo.getText());
//        pst.setString(3, txtValEntrada.getText());
//        pst.setString(4, txtCarteira.getText());
//        pst.setString(5, txtContrato.getText());
//
//        String textoNParcela = txtNParcela.getText().trim(); // Remover espaços em branco extras
//        int numParcelas = 0; // Inicializa com zero
//        if (!textoNParcela.isEmpty()) {
//            try {
//                numParcelas = Integer.parseInt(textoNParcela); // Converte o texto para inteiro
//            } catch (NumberFormatException ex) {
//                JOptionPane.showMessageDialog(null, "Número de parcelas inválido.");
//                return; // Sai do método se não puder converter para inteiro
//            }
//        }
//
//        pst.setString(8, txtTxJuros.getText());
//        pst.setString(9, txtVParcelas.getText());
//        pst.setString(10, quit);
//
//        // Obter a data de início diretamente como string
//        String dataInicioStr = txtDtPVenc.getText().trim(); // Remover espaços em branco extras
//        if (!dataInicioStr.isEmpty()) {
//            try {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//                LocalDate dataInicio = LocalDate.parse(dataInicioStr, formatter); // Parsing string para LocalDate
//
//                // Adicionar as parcelas
//                for (int i = 0; i < numParcelas; i++) {
//                    // Definir a parte da parcela atual
//                    pst.setInt(11, i + 1);
//
//                    // Definir a data da parcela
//                    LocalDate dataParcela = dataInicio.plusMonths(i);
//                    pst.setDate(7, java.sql.Date.valueOf(dataParcela)); // Definir a data de vencimento da parcela
//
//                    // Definir a data atual
//                    LocalDate dataAtual = LocalDate.now(); // Data atual
//                    pst.setDate(12, java.sql.Date.valueOf(dataAtual)); // Definir a data atual
//
//                    // Executar o comando de inserção para esta parcela
//                    pst.executeUpdate();
//                }
//
//                JOptionPane.showMessageDialog(null, "Parcelas adicionadas com sucesso");
//                limpar();
//            } catch (DateTimeParseException e) {
//                JOptionPane.showMessageDialog(null, "Formato de data inválido. Use o formato dd/MM/yyyy.");
//                return; // Retorna se houver erro de parsing da data
//            }
//        } else {
//            JOptionPane.showMessageDialog(null, "Campo de data de início não pode estar vazio.");
//            return; // Retorna se o campo de data de início estiver vazio
//        }
//    } catch (SQLException e) {
//        JOptionPane.showMessageDialog(null, "Erro ao adicionar parcelas: " + e.getMessage());
//        System.out.println(e);
//    }
//}
    
    


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
