/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifba.bean;

import br.edu.ifba.Relatorio.Relatorio;
import br.edu.ifba.Relatorio.Relatorio1;
import br.edu.ifba.entidades.Discentes;
import br.edu.ifba.entidades.Usuarios;
import br.edu.ifba.util.HibernateUtil;
import br.edu.ifba.util.RelatorioUtil;
import com.sun.xml.internal.ws.util.UtilException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primefaces.PrimeFaces;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author wesio
 */

@ManagedBean
@SessionScoped
public class bean {
   private Discentes d = new Discentes();
   private List<Discentes> alunos ;
   
   private Usuarios u = new Usuarios();
   private List<Usuarios> users;
   
   private String login;        
   private String senha;
   private String sair;
   String mensagemLogin="";

    public String getSair() {
        
        return sair;
    }

    public void setSair(String sair) {
        this.sair = sair;
    }
   
   
   
   private StreamedContent arquivoRetorno;   

   private String mat;
   
   private Part file; 
   
  
   
    public bean() {
    }

 
    

    public StreamedContent getArquivoRetorno() {
        
        
        
        Date x = new Date();
        FacesContext context = FacesContext.getCurrentInstance();
        String nomeRelatorio = "acesso";
        String nomeSaida = "_ausente";
        RelatorioUtil relatorioUtil = new RelatorioUtil();
        HashMap parametros = new HashMap();
       
        try {
            this.arquivoRetorno = relatorioUtil.gerarRelatorio(parametros, nomeRelatorio, nomeSaida);
        } catch (UtilException e) {
            System.out.println(e.getMessage());
        
            context.addMessage(null, new FacesMessage(e.getMessage()));
            return null;
        }
        
        return this.arquivoRetorno;
       
    }

    

    public void setArquivoretorno(StreamedContent arquivoretorno) {
        this.arquivoRetorno = arquivoretorno;
    }

    
    public String getMat() {
        return mat;
    }

    public void setMat(String mat) {
        this.mat = mat;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Usuarios getU() {
        return u;
    }

    public void setU(Usuarios u) {
        this.u = u;
    }

    public List<Usuarios> getUsers() {
        return users;
    }

    public void setUsers(List<Usuarios> users) {
        this.users = users;
    }

    public Discentes getD() {
        return d;
    }

    public void setD(Discentes d) {
        this.d = d;
    }

    public List<Discentes> getAlunos() {
        return alunos;
    }

    public void setAlunos(List<Discentes> alunos) {
        this.alunos = alunos;
    }
    
    public String Adm(){
        return "principalAdm.jsf";
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }
 
    public void CadastrarDiscente(){
        Discentes aux = new Discentes();
        Session sessao = HibernateUtil.getSessionFactory().openSession();
        Transaction t = sessao.beginTransaction();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        
        aux.setHora(dateFormat.format(date));
        aux.setSituacao(0);
        aux.setNome(d.getNome());
        aux.setMatricula(d.getMatricula());
        aux.setTurma(d.getTurma());
        aux.setResponsavel(d.getResponsavel());
        aux.setEmailRes(d.getEmailRes());
        aux.setNumRes(d.getNumRes());
        try(InputStream is = file.getInputStream()){
         String fileName = d.getMatricula()+".jpg";
         Files.copy(is,new File("/Users/wesio/Desktop/A-Trabalho e Atividades-IFBA/TRABALHO DE CONCLUSÃO DE CURSO/InputControl/Web/FOTOS DISCENTES", fileName).toPath());
         
         aux.setImagem("FOTOS DISCENTES/"+d.getMatricula()+".jpg");
     }catch(IOException e){
         e.printStackTrace();
     }
        sessao.save(aux);
        t.commit(); 
        sessao.close();
        
        
        d.setNome("");
        d.setMatricula("");
        d.setTurma("");
        d.setResponsavel("");
        d.setEmailRes("");
        d.setNumRes("");
    }
    
    public void cadastrouser(){
        Usuarios aux = new Usuarios();
        Session sessao = HibernateUtil.getSessionFactory().openSession();
        Transaction t = sessao.beginTransaction();
        
        aux.setLogin(u.getLogin());
        aux.setSenha(convertStringToMd5(u.getSenha()));
        aux.setTipo(u.getTipo());
        
        
        sessao.save(aux);
        t.commit(); 
        sessao.close();
        
        u.setLogin("");
        u.setTipo(0);
        u.setSenha("");
        
    }
    
    public void mostrarDis(){
        Session sessao = HibernateUtil.getSessionFactory().openSession();
        Transaction t = sessao.beginTransaction();
        
        Criteria c = sessao.createCriteria(Discentes.class);
        
        alunos = c.list();
        
        t.commit();
        sessao.close();
    }
    public String pesquisar(String mat){
        
        Session sessao = HibernateUtil.getSessionFactory().openSession();
        Transaction t = sessao.beginTransaction();
        Criteria c = sessao.createCriteria(Discentes.class);
        
        
        alunos = c.list();
        for( Discentes x : alunos){
        if(x.getMatricula().equals(this.mat)){
            this.d = x;
            return "listar.jsf";
       
        }
          
        
        
    }
        
        t.commit();
        sessao.close();
            this.mat = "";
            return "principalAdm.jsf";
    }
    public String pesquisar1(String mat){
        
        Session sessao = HibernateUtil.getSessionFactory().openSession();
        Transaction t = sessao.beginTransaction();
        Criteria c = sessao.createCriteria(Discentes.class);
        
        
        alunos = c.list();
        for( Discentes x : alunos){
        if(x.getMatricula().equals(this.mat)){
            this.mat = "";
            this.d = x;
            return "listar_1.jsf";
       
        }
           
        
    }
        
        t.commit();
        sessao.close();
            this.mat = "";
            return "principal.jsf";
    }
     public void finalizar(){
     
     presentes();
      Discentes aux = null;
     Session sessao = HibernateUtil.getSessionFactory().openSession();
     Transaction t = sessao.beginTransaction();
     
     for( Discentes x : alunos){
         x.setSituacao(0);
        sessao.update(x);
         
        
     }
     
     t.commit();
     sessao.close();
 }
    
    public void dialogo() {
      Map<String,Object> options = new HashMap<>();
        options.put("resizable", false);
        PrimeFaces.current().dialog().openDynamic("listar", options, null);
    }
    
    public void excluir(Discentes d){
        Session sessao = HibernateUtil.getSessionFactory().openSession();
        Transaction t = sessao.beginTransaction();
        sessao.delete(d);
        t.commit();
        sessao.close();
        mostrarDis();
    }
    
   
 public String edicao(Discentes arq){
     this.d = arq;
     return "edicao.jsf";
 }

 public void update(){
      Discentes aux;
      aux = d;
     Session sessao = HibernateUtil.getSessionFactory().openSession();
     Transaction t = sessao.beginTransaction();
     
     sessao.update(aux);
     t.commit();
     sessao.close();
     
        aux.setNome("");
        aux.setMatricula("");
        aux.setTurma("");
        aux.setResponsavel("");
        aux.setEmailRes("");
        aux.setNumRes("");
     
     
 }   
public void listarUsuario(){
     Session sessao = HibernateUtil.getSessionFactory().openSession();
     Transaction t = sessao.beginTransaction();
     Criteria c = sessao.createCriteria(Usuarios.class);
     
      users = c.list();
     t.commit();
     sessao.close();
 }
 
 public String entrar(){
     
     listarUsuario();
    for( Usuarios x : users){
        if(x.getLogin().equals(this.getLogin())){
            if(x.getSenha().equals(convertStringToMd5(this.getSenha())) ){
                setSenha("");
                setSair(getLogin());
                setLogin("");
                if(x.getTipo()==1){
             return "principalAdm.jsf";
                }else{
                    if(x.getTipo()==2){
             return "principal.jsf";
                }
                }
        }else{
           this.mensagemLogin="Não é possivel logar, verifique se o login e senha estão corretos!";         
     FacesContext.getCurrentInstance().addMessage("posicao:senha", new FacesMessage(mensagemLogin));
            }
        }
    }
             return "index.jsf";
    
 }
 private String convertStringToMd5(String valor) {
             MessageDigest mDigest;
             try { 
                    mDigest = MessageDigest.getInstance("MD5");
                     
                    byte[] valorMD5 = mDigest.digest(valor.getBytes("UTF-8"));
                     
                    StringBuffer sb = new StringBuffer();
                    for (byte b : valorMD5){
                           sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1,3));
                    }
  
                    return sb.toString();
                     
             } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return null;
             } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
             }
       }
 /*public void upload(){
     try(InputStream is = file.getInputStream()){
         String fileName = file.getSubmittedFileName();
         Files.copy(is,new File("C:\\Users\\wesio\\Desktop\\Trabalho e Atividades-IFBA\\TRABALHO DE CONCLUSÃO DE CURSO\\InputControl", fileName).toPath());
         
         
     }catch(IOException e){
         e.printStackTrace();
     }
 }
 */
 public String p1(){
     return "principalAdm.jsf";
 }
 private String p2(){
     return "principal.jsf";
 }
 private String index(){
     return "index.jsf";
 }

public void situacao(){
    Discentes aux;
      aux = d;
     Session sessao = HibernateUtil.getSessionFactory().openSession();
     Transaction t = sessao.beginTransaction();
     DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    Date date = new Date();
    
       
     aux.setHora(dateFormat.format(date));
     aux.setSituacao(1);
     sessao.update(aux);
     t.commit();
     sessao.close();
}
public void presentes(){
    Session sessao = HibernateUtil.getSessionFactory().openSession();
    Transaction t = sessao.beginTransaction();
    
    alunos = sessao.getNamedQuery("selecionarPresentes").list();
    
    t.commit();
    sessao.close();
}
public void ausentes(){
    Session sessao = HibernateUtil.getSessionFactory().openSession();
    Transaction t = sessao.beginTransaction();
    
    alunos = sessao.getNamedQuery("selecionarAusentes").list();
    
    t.commit();
    sessao.close();
}

public void gererpdf(){
    ausentes();
    Relatorio1 r = new Relatorio1();
       try {
           r.gerarRelatorio(alunos);
       } catch (JRException ex) {
           Logger.getLogger(bean.class.getName()).log(Level.SEVERE, null, ex);
       }
}
public void gerarpdf(){
    Relatorio r = new Relatorio();
    
           r.getRelatorio(alunos);
}
public void exportarPDF() throws JRException, IOException{
    
    File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/ausentes.jasper"));
    JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), null, new JRBeanCollectionDataSource(alunos));
    
    HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
    response.addHeader("Content-disposition", "inline; filename=jsfReporte.pdf");
    ServletOutputStream stream = response.getOutputStream();
    
    JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
    
    stream.flush();
    stream.close();
    FacesContext.getCurrentInstance().responseComplete();
}
}

