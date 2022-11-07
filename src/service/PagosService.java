/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import repository.PagosRepository;

/**
 *
 * @author Arnaldo_Cantero
 */
public class PagosService {
    private PagosRepository pagosRepository = new PagosRepository();
        
    public String sqlPagosConceptoPorFecha(String fechaDesde, String fechaHasta, int idConcepto){                
        return  pagosRepository.sqlPagosConceptoPorFecha(fechaDesde, fechaHasta, idConcepto);
    }
    
    public String sqlPagosConceptoPorFechaConNombre(String fechaDesde, String fechaHasta, int idConcepto){                        
        return  pagosRepository.sqlPagosConceptoPorFechaConNombre(fechaDesde, fechaHasta, idConcepto);
    }
    
    public String sqlPagosPorFecha(String fechaDesde, String fechaHasta){                        
        return   pagosRepository.sqlPagosPorFecha(fechaDesde, fechaHasta);
    }
    
    public String sqlAllPagos(){                        
        return  pagosRepository.sqlAllPagos();
    }
    
    public String sqlPagoConceptos(int idPago){                        
        return  pagosRepository.sqlPagoConceptos(idPago);
    }
}


