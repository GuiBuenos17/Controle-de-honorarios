/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rrol.dal;

import java.sql.*;

/**
 *
 * @author Advogados
 */
public class ModuloConexao {
    
    public static Connection conector() {
        Connection conexao = null;
        // A linha abaixo "chama" o driver
        String driver = "";
        // Armazenando informações referente ao banco
        String url = "";
        String user = "";
        String password = "";
        // Estabelecendo a conexão com o banco
        try {
            Class.forName(driver);
            conexao = DriverManager.getConnection(url, user, password);
            return conexao;
        } catch (Exception e) {
            //  a linha abaixo ser como apoio para esclarecer o erro
            System.out.println(e);
            return null;
        }

    }

    public static Connection conectar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

}
