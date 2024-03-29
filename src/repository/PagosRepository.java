/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repository;

/**
 *
 * @author Arnaldo_Cantero
 */
public class PagosRepository {
    public String sqlPagosConceptoPorFecha(String fechaDesde, String fechaHasta, int idConcepto){                
        return  " SELECT pag_numpago, con_descripcion, pag_fechapago,\n"
                    + " CASE\n"
                    + "   WHEN con_considera_cant_alumno = 1\n"
                    + "    THEN (SELECT SUM(pagcon_monto * pagcon_numcuotas * pagcon_cantalumno) AS sumamonto FROM pago_concepto WHERE pagcon_pago=pag_codigo)\n"
                    + "   ELSE  (SELECT SUM(pagcon_monto * pagcon_numcuotas) AS sumamonto FROM pago_concepto WHERE pagcon_pago=pag_codigo)\n"
                    + " END AS totalpago\n"
                    + " FROM pago, concepto, pago_concepto\n"
                    + " WHERE pagcon_pago=pag_codigo AND pagcon_concepto=con_codigo\n"
                    + "  AND pag_fechapago BETWEEN '" + fechaDesde + "' AND '" + fechaHasta + "'\n"
                    +"   AND con_codigo = (CASE\n"
                    + "                      WHEN " + idConcepto + "=-1\n"
                    + "                        THEN con_codigo\n"
                    + "                      ELSE " + idConcepto + "\n"
                    + "                    END)\n";
    }
    
    public String sqlPagosConceptoPorFechaConNombre(String fechaDesde, String fechaHasta, int idConcepto){                        
        return  " SELECT pag_numpago, CONCAT(apo_apellido,', ', apo_nombre) AS apoderado, con_descripcion, pag_fechapago,\n"
                    + " CASE\n"
                    + "   WHEN con_considera_cant_alumno = 1\n"
                    + "    THEN (SELECT SUM(pagcon_monto * pagcon_numcuotas * pagcon_cantalumno) AS sumamonto FROM pago_concepto WHERE pagcon_pago=pag_codigo)\n"
                    + "   ELSE  (SELECT SUM(pagcon_monto * pagcon_numcuotas) AS sumamonto FROM pago_concepto WHERE pagcon_pago=pag_codigo)\n"
                    + " END AS totalpago\n"
                    + " FROM pago, concepto, pago_concepto, apoderado\n"
                    + " WHERE pagcon_pago=pag_codigo AND pagcon_concepto=con_codigo AND pag_fechapago BETWEEN '" + fechaDesde + "' AND '" + fechaHasta + "'\n"
                    + "  AND con_codigo = (CASE\n"
                    + "                      WHEN " + idConcepto + "=-1\n"
                    + "                        THEN con_codigo\n"
                    + "                      ELSE " + idConcepto + "\n"
                    + "                    END) AND apo_codigo = pag_apoderado\n";
    }
    
    public String sqlPagosPorFecha(String fechaDesde, String fechaHasta){                        
        return  " SELECT pag_codigo, pag_numpago, CONCAT(apo_nombre, ' ', apo_apellido) AS nomape,\n"
                    + "DATE_FORMAT(pag_fechapago, '%d/%m/%Y') AS fechapago, pag_importe,\n"
                    + " CASE\n"
                    + "   WHEN con_considera_cant_alumno = 1\n"
                    + "    THEN (SELECT SUM(pagcon_monto * pagcon_numcuotas * pagcon_cantalumno) AS sumamonto FROM pago_concepto WHERE pagcon_pago=pag_codigo)\n"
                    + "   ELSE  (SELECT SUM(pagcon_monto * pagcon_numcuotas) AS sumamonto FROM pago_concepto WHERE pagcon_pago=pag_codigo)\n"
                    + " END AS totalpago, pag_periodo\n"
                    + " FROM pago, apoderado\n"
                    + " WHERE pag_apoderado = apo_codigo '" + fechaDesde + "' AND '" + fechaHasta + "' ORDER BY pag_numpago DESC\n";
    }
    
    public String sqlAllPagos(){                        
        return  " SELECT pag_codigo, pag_numpago, CONCAT(apo_nombre, ' ', apo_apellido) AS nomape,\n"
                    + " DATE_FORMAT(pag_fechapago, '%d/%m/%Y') AS fechapago, pag_importe,\n"
                    + " CASE\n"
                    + "   WHEN con_considera_cant_alumno = 1\n"
                    + "    THEN (SELECT SUM(pagcon_monto * pagcon_numcuotas * pagcon_cantalumno) AS sumamonto FROM pago_concepto WHERE pagcon_pago=pag_codigo)\n"
                    + "   ELSE  (SELECT SUM(pagcon_monto * pagcon_numcuotas) AS sumamonto FROM pago_concepto WHERE pagcon_pago=pag_codigo)\n"
                    + " END AS totalpago, pag_periodo\n"
                    + " FROM pago, apoderado, pago_concepto, concepto\n"
                    + " WHERE pag_apoderado = apo_codigo\n"
                    + "   AND pag_codigo = pagcon_pago\n"
                    + "   AND pagcon_concepto = con_codigo\n"
                    + " ORDER BY pag_numpago DESC\n";
    }
    
    public String sqlPagoConceptos(int idPago){                        
        return  " SELECT con_descripcion, pagcon_numcuotas, pagcon_meses, pagcon_monto,\n"
                + " CASE\n"
                + "  WHEN con_considera_cant_alumno = 1\n"
                + "   THEN pagcon_numcuotas * pagcon_monto * pagcon_cantalumno\n"
                + "  ELSE pagcon_numcuotas * pagcon_monto\n"
                + "  END AS subtotal, pagcon_cantalumno\n"
                + " FROM pago, pago_concepto, concepto\n"
                + " WHERE pagcon_pago = " + idPago + " AND pag_codigo = pagcon_pago AND pagcon_concepto = con_codigo\n";
    }
    
}
