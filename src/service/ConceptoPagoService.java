/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import repository.ConceptoPagoRepository;

/**
 *
 * @author Arnaldo_Cantero
 */
public class ConceptoPagoService {
    private ConceptoPagoRepository conceptoPagoRepository = new ConceptoPagoRepository();
        
    public String consultarAllConceptosPago(){                
        return conceptoPagoRepository.sqlConsultarAllConceptosPago();
    }
    
    public String registrarConceptoPago(String descripcion, String tipoImporte, double importe, String tipopago, int numpagos, 
            int ene, int feb, int mar, int abr, int may, int jun, int jul, int ago, int sep, int oct, int nov, int dic, int consideraCantAlumno){                
       return  conceptoPagoRepository.sqlRegistrarConceptoPago(descripcion, tipoImporte, importe, tipopago, numpagos, 
               ene, feb, mar, abr, may, jun, jul, ago, sep, oct, nov, dic, consideraCantAlumno);
    }
    
    public String modificarConceptoPago(String codigo, String descripcion, String tipoImporte, double importe, String tipopago, int numpagos, 
            int ene, int feb, int mar, int abr, int may, int jun, int jul, int ago, int sep, int oct, int nov, int dic, int consideraCantAlumno){                
       return  conceptoPagoRepository.sqlModificarConceptoPago(codigo, descripcion, tipoImporte, importe, tipopago, numpagos, 
               ene, feb, mar, abr, may, jun, jul, ago, sep, oct, nov, dic, consideraCantAlumno);
    }
    
    public boolean isConsideraCantidadAlumnos(String codigoConcepto){
        return conceptoPagoRepository.isConsideraCantidadAlumnos(codigoConcepto);
    }
}


