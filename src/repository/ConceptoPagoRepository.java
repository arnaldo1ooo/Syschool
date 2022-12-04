/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repository;

import dao.DAO;

public class ConceptoPagoRepository {
    private DAO con = new DAO();
    
    public String sqlConsultarAllConceptosPago(){                
        return  " SELECT con_codigo, con_descripcion, con_tipoimporte, con_importe, con_tipopago, con_numpagos,\n" 
                + " con_ene, con_feb, con_mar, con_abr, con_may, con_jun, con_jul, con_ago, con_sep, con_oct, con_nov, con_dic, con_considera_cant_alumno\n" 
                + " FROM concepto\n" 
                + " ORDER BY con_codigo\n";
    }
    
    public String sqlRegistrarConceptoPago(String descripcion, String tipoImporte, double importe, String tipopago, int numpagos, 
            int ene, int feb, int mar, int abr, int may, int jun, int jul, int ago, int sep, int oct, int nov, int dic, int consideraCantAlumno){                
       return  " INSERT INTO concepto VALUES\n" 
               + " (con_codigo, '" + descripcion + "', '" + tipoImporte + "', " + importe + ", '" + tipopago + "', " + numpagos + ", \n" 
               + ene + ", " + feb + ", " + mar + ", " + abr + ", " + may + ", " + jun + ", " + jul + ", " + ago + ", " + sep + ", \n" 
               + oct + ", " + nov + ", " + dic + ", " + consideraCantAlumno + ")\n";
    }
    
    public String sqlModificarConceptoPago(String codigo, String descripcion, String tipoImporte, double importe, String tipopago, int numpagos, 
            int ene, int feb, int mar, int abr, int may, int jun, int jul, int ago, int sep, int oct, int nov, int dic, int consideraCantAlumno){                
       return " UPDATE concepto\n"
               + " SET con_codigo=" + codigo + ", con_descripcion='" + descripcion + "', con_tipoimporte='" + tipoImporte + "', con_importe=" + importe + ", \n"
               + "     con_tipopago='" + tipopago + "', con_numpagos=" + numpagos + ", \n" 
               + "     con_ene=" + ene + ", con_feb=" + feb + ", con_mar=" + mar + ", con_abr=" + abr + ", con_may=" + may + ", con_jun=" + jun + ", con_jul=" + jul + ", \n"
               + "     con_ago=" + ago + ", con_sep=" + sep + ", con_oct=" + oct + ", con_nov=" + nov + ", con_dic=" + dic + ", con_considera_cant_alumno=" + consideraCantAlumno + "\n"
               + " WHERE con_codigo=" + codigo;
    }
    
    public boolean isConsideraCantidadAlumnos(String codigoConcepto){
        String sentencia = "SELECT con_considera_cant_alumno FROM concepto WHERE con_codigo=" + codigoConcepto;
        con = con.ObtenerRSSentencia(sentencia);
        int consideraCantAlumno = 0;
            
        try {
            while (con.getResultSet().next()) {
                consideraCantAlumno = con.getResultSet().getInt("con_considera_cant_alumno");
            }           
        } catch (Exception e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
        
        return consideraCantAlumno == 1 ? true : false;
        
    }
    
    
    
}
