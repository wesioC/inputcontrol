/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifba.util;

import com.sun.xml.internal.ws.util.UtilException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author wesio
 */
public class RelatorioUtil {
    public  StreamedContent gerarRelatorio(HashMap parametro, String nomeRelatorio, String nomeSaida)throws UtilException{
        StreamedContent arqRetorno = null;
        
        try {
            Connection conexaobd = this.getConexaobd();
            FacesContext contextoFaces = FacesContext.getCurrentInstance();
            ExternalContext contextoExteno = contextoFaces.getExternalContext();
            ServletContext contextoServlet = (ServletContext) contextoExteno.getContext();
            
            //String caminho = contextoServlet.getRealPath("C:/Users/wesio/Desktop/A-Trabalho e Atividades-IFBA/TRABALHO DE CONCLUSÃO DE CURSO/InputControl/src/java/br/edu/ifba/report");
            //String caminho = contextoServlet.getRealPath("C:/Users/wesio/Desktop/A-Trabalho e Atividades-IFBA/TRABALHO DE CONCLUSÃO DE CURSO/InputControl/src/java/br/edu/ifba/util/relatorios");
            //System.out.println(caminho);
            String caminhoJasper = "C:/Users/wesio/Desktop/A-Trabalho e Atividades-IFBA/TRABALHO DE CONCLUSÃO DE CURSO/InputControl/src/java/br/edu/ifba/report/acesso.jasper";
            //String caminhoArq = caminho + File.separator + nomeSaida;
            String caminhoArq = "C:/Users/wesio/Desktop/A-Trabalho e Atividades-IFBA/TRABALHO DE CONCLUSÃO DE CURSO/InputControl/src/java/br/edu/ifba/report/" + nomeSaida;
            
            JasperReport relatoriojasper = (JasperReport) JRLoader.loadObjectFromFile(caminhoJasper);
            JasperPrint print = JasperFillManager.fillReport(relatoriojasper , parametro , conexaobd);
            
            String extensao = null;
            File ArqG = null;
            
            JRPdfExporter pdfExportado = new JRPdfExporter();
            extensao ="pdf";
            ArqG = new java.io.File(caminhoArq + "." + extensao);
            pdfExportado.setExporterInput(new SimpleExporterInput(print));
            pdfExportado.setExporterOutput(new SimpleOutputStreamExporterOutput(ArqG));
            pdfExportado.exportReport();
            ArqG.deleteOnExit();
            
            InputStream conteudo = new FileInputStream(ArqG);
            arqRetorno = new DefaultStreamedContent(conteudo, "application/" + extensao,  nomeSaida + "." + extensao);
            
        } catch (JRException e) {
            throw new UtilException("Relatorio não gerado",e);
            
        }catch (FileNotFoundException e) {
         
            throw new UtilException("Relatorio não encontrado",e);
            
        }
        
        return arqRetorno;
    }
    
    private Connection getConexaobd() throws UtilException{
        try {
            Context iniContext = new InitialContext();
            Context envContext = (Context) iniContext.lookup("java:comp/env");
            javax.sql.DataSource ds = (javax.sql.DataSource) envContext.lookup("jdbc/acessoifba");
            return  (java.sql.Connection) ds.getConnection();
        } catch (NamingException e) {
            throw new UtilException("Não foi encontrado o nome do banco de dados!",e);
        }catch (SQLException e){
            throw new UtilException("Erro de SQL!",e);
        }
    
    }
}
