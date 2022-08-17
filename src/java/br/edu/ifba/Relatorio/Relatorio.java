/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifba.Relatorio;

import br.edu.ifba.entidades.Discentes;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author wesio
 */
public class Relatorio {
     private HttpServletResponse response;
     private FacesContext context;
     private ByteArrayOutputStream baos;
     private InputStream stream;
     private Connection con;

    public Relatorio() {
        this.context = FacesContext.getCurrentInstance();
        this.response = (HttpServletResponse) context.getExternalContext().getResponse();
    }
    
    public void getRelatorio(List<Discentes> lista){
        stream = this.getClass().getResourceAsStream("/report/ausentes.jasper");
        Map<String, Object> params = new HashMap<String, Object>();
        baos = new ByteArrayOutputStream();
        
         try {
             
             JasperReport report = (JasperReport) JRLoader.loadObject(stream);
             JasperPrint print = JasperFillManager.fillReport(report, params, new JRBeanCollectionDataSource(lista));
             JasperExportManager.exportReportToPdfStream(print,baos);
             
             response.reset();
             response.setContentType("application/pdf");
             response.setContentLength(baos.size());
             response.setHeader("Content-disposition", "inline: filename=relatorio.pdf");
             response.getOutputStream().write(baos.toByteArray());
             response.getOutputStream().flush();
             response.getOutputStream().close();
             
             context.responseComplete();
            // fecharConexao();
             
         } catch (JRException ex) {
             Logger.getLogger(Relatorio.class.getName()).log(Level.SEVERE, null, ex);
         } catch (IOException ex) {
             Logger.getLogger(Relatorio.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
     
     public Connection getConexao(){
         try {
             Class.forName("com.mysql.jdbc.Driver");
             con = DriverManager.getConnection("jdbc:mysql://localhost:3306/acessoifba", "root", "4466");
             
             return con;
         } catch (SQLException ex) {
             Logger.getLogger(Relatorio.class.getName()).log(Level.SEVERE, null, ex);
         } catch (ClassNotFoundException ex) {
             Logger.getLogger(Relatorio.class.getName()).log(Level.SEVERE, null, ex);
         }
         return con;
     }
     public void fecharConexao(){
         try {
             con.close();
         } catch (SQLException ex) {
             Logger.getLogger(Relatorio.class.getName()).log(Level.SEVERE, null, ex);
         }
     }
}
