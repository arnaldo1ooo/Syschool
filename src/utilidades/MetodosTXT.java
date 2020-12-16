/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilidades;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.apache.log4j.Logger;

public class MetodosTXT {

    static Logger log_historial = Logger.getLogger(MetodosTXT.class.getName());

    public void FiltroCaracteresProhibidos(java.awt.event.KeyEvent evt) {
        // Verificar si la tecla pulsada no es '
        char caracter = evt.getKeyChar();

        if (caracter == "'".charAt(0) || caracter == "\\".charAt(0)) {
            evt.consume(); // ignorar el evento de teclado
        }
    }

    public void SoloTextoKeyTyped(KeyEvent evt) {
        //Declaramos una variable y le asignamos un evento
        char car = evt.getKeyChar();
        //Condicion que nos permite ingresar datos de tipo texto
        if (((car < 'a' || car > 'z') && (car < 'A' || car > 'z')) && (car != '.') && (car != 'ñ') && (car != 'Ñ') && (car != (char) KeyEvent.VK_BACK_SPACE) && (car != (char) KeyEvent.VK_SPACE)) {
            evt.consume();
        }
    }

    public String QuitaEspaciosString(String texto) {
        StringTokenizer tokens = new StringTokenizer(texto);
        StringBuilder buff = new StringBuilder();
        while (tokens.hasMoreTokens()) {
            buff.append(" ").append(tokens.nextToken());
        }
        return buff.toString().trim();
    }

    public String MayusSoloPrimeraLetra(String laCadena) {
        if (laCadena == null || laCadena.isEmpty()) {
            return "";
        } else {
            return Character.toUpperCase(laCadena.charAt(0)) + laCadena.substring(1, laCadena.length()).toLowerCase();
        }
    }

    public String MayusCadaPrimeraLetra(String laCadena) {
        if (laCadena == null || laCadena.isEmpty()) {
            return "";
        } else {
            laCadena = laCadena.toLowerCase();
            char[] caracteres = laCadena.toCharArray();
            caracteres[0] = Character.toUpperCase(caracteres[0]); //Hace mayuscula la primera letra de la primera palabra
            // el -2 es para evitar una excepción al caernos del arreglo
            for (int i = 0; i < laCadena.length() - 2; i++) { //Recorre cada letra
                // Es 'palabra'
                if (caracteres[i] == ' ' || caracteres[i] == '.' || caracteres[i] == ',') { //Separadores puede ser espacio . o ,

                    caracteres[i + 1] = Character.toUpperCase(caracteres[i + 1]); // Reemplazamos
                }
            }
            return new String(caracteres);
        }
    }

    public void SoloNumeroEnteroKeyTyped(KeyEvent evt) {
        //Declaramos una variable y le asignamos un evento
        char car = evt.getKeyChar();
        //Condicion que nos permite ingresar datos de tipo numerico
        if (((car < '0' || car > '9') && (car != (char) KeyEvent.VK_BACK_SPACE) && car != (char) KeyEvent.VK_LEFT && car != (char) KeyEvent.VK_RIGHT)) {
            evt.consume();
        }
    }

    public void SoloNumeroDecimalKeyTyped(KeyEvent evt, JTextField ElTXT) {
        // Que solo entre numeros y .
        char teclaoprimida = evt.getKeyChar();
        if ((teclaoprimida < '0' || teclaoprimida > '9')
                && teclaoprimida != KeyEvent.VK_BACK_SPACE //Para que no entre espacio
                && teclaoprimida != KeyEvent.VK_LEFT
                && (teclaoprimida != ',' && teclaoprimida != '.' || ElTXT.getText().contains(",") == true)) { //Para que solo tenga una coma
            evt.consume(); // ignorar el evento de teclado
        } else {
            if (teclaoprimida == '.') { //Si se oprime . lo reemplaza con ,
                evt.setKeyChar(',');
            }
        }
    }

    //Metodo en cual si el texto esta vacio, su label queda en gris, si esta cargado queda en verde
    public void TxtColorLabelKeyReleased(JTextField ElTXT, JLabel ElLabel) {
        if (ElTXT.getText().equals("")) {
            if (ElLabel.getForeground() == Color.RED) { //Si esta en rojo, y es vacio entonces no hace nada

            }
            //ElLabel.setForeground(new Color(0, 0, 0)); //Negro
        } else { //Si es distinto a vacio
            ElLabel.setForeground(new Color(0, 153, 51)); //Verde
        }
    }

    //Convierte a mayusculas
    public void TxtMayusKeyReleased(JTextField ElTXT, KeyEvent evt) {
        Character s = evt.getKeyChar();
        if (Character.isLetter(s)) {
            ElTXT.setText(ElTXT.getText().toUpperCase());
        }
    }

    //Limitar cantidad caracteres
    public void TxtCantidadCaracteresKeyTyped(JTextField ElTXT, int Cantidad) {
        int longitud = ElTXT.getText().length();
        Cantidad = Cantidad - 1;
        if (longitud > Cantidad) {
            ElTXT.setText(ElTXT.getText().substring(0, Cantidad));
        }
    }

    public String StringAFormatSudamericaKeyRelease(String elNumString) {
        try {
            //Si es vacio devuelve lo mismo
            if (elNumString.equals("") || elNumString.contains(",") || elNumString.contains("-")) {
                return elNumString;
            }

            elNumString = elNumString.replace(".", "");
            DecimalFormat formatSudamerica = new DecimalFormat("#,###.###");
            double elNumeroDouble = Double.parseDouble(elNumString);
            elNumString = formatSudamerica.format(elNumeroDouble);
            return elNumString;
        } catch (NullPointerException e) {
            return elNumString;
        }
    }

    //Convertir de Double americano a Double Sudamericano
    public String DoubleAFormatSudamerica(double elDoubleAmericano) {
        try {
            DecimalFormat formatSudamerica = new DecimalFormat("#,###.###");
            String elNumeroString = formatSudamerica.format(elDoubleAmericano);

            return elNumeroString;
        } catch (NullPointerException e) {
            return "0";
        }

    }

    public Double StringAFormatoAmericano(String ElNumString) {
        double ElNumDouble = 0.0;

        if (ElNumString.equals("")) {
            return ElNumDouble;
        }

        try {
            if (ElNumString.endsWith(".0")) { //Si termina en .0 se borra esa parte
                ElNumString = ElNumString.substring(0, ElNumString.length() - 2); //Borra los dos ultimos caracteres de la cadena
            }

            ElNumString = ElNumString.replace(".", ""); //Saca los puntos de miles
            ElNumString = ElNumString.replace(",", "."); //Cambia la coma en punto
            ElNumDouble = Double.parseDouble(ElNumString);
        } catch (NumberFormatException e) {
            System.out.println("Error DoubleAFormatoAmericano valor: " + ElNumString + ", Error:" + e);
            /*log_historial.error("Error 1019 valor: " + ElNumero + ", Error:" + e);
            e.printStackTrace();*/
        } catch (NullPointerException e) {
            return 0.0;
        }

        return ElNumDouble;
    }

    public int StringSinPuntosMiles(String elNumString) {
        try {
            if (elNumString.equals("")) {
                return 0;
            }
            elNumString = elNumString.replace(".", "");
            int elNumInt = Integer.parseInt(elNumString);
            return elNumInt;
        } catch (NullPointerException e) {
            return 0;
        }
    }

    //Poner los puntos de miles
    public String StringPuntosMiles(String elNumString) {
        try {
            if (elNumString.equals("") || elNumString.contains("-")) {
                return elNumString;
            }

            elNumString = elNumString.replace(".", "");
            DecimalFormat formatSudamerica = new DecimalFormat("#,###");
            double elNumeroDouble = Double.parseDouble(elNumString);
            elNumString = formatSudamerica.format(elNumeroDouble);
        } catch (NullPointerException e) {
            return "0";
        }
        return elNumString;
    }

    //Formatear double para que tenga solo dos numeros despues de la coma, y la coma es punto
    public double DoubleATresDecimales(double ElDouble) {
        String elDoubleString = "";
        Double elNumeroDouble = 0.000;
        try {
            elDoubleString = String.format("%.3f", ElDouble);
            elDoubleString = elDoubleString.replace(",", ".");
            elNumeroDouble = Double.parseDouble(elDoubleString);
        } catch (NumberFormatException e) {
            System.out.println("Error al poner DosDecimales al numero " + ElDouble + "   " + e);
            log_historial.error("Error 1020: " + e);
            e.printStackTrace();
        }
        return elNumeroDouble;
    }

    public void BloquearTeclaKeyTyped(KeyEvent evt, int tecla) {
        //Declaramos una variable y le asignamos un evento
        char car = evt.getKeyChar();
        //Que no entre espacio
        if ((car == (char) tecla)) { //Ejemplo de tecla: KeyEvent.VK_SPACE
            evt.consume();
        }
    }

    public boolean ValidarDouble(String elDouble) {
        elDouble = elDouble.replace(".", "");
        elDouble = elDouble.replace(",", ".");
        try {
            double a = Double.parseDouble(elDouble);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Error al validar double, no valido: " + elDouble);
            log_historial.error("Error 1021: " + e);
            e.printStackTrace();
            return false;
        }
    }

    public boolean ValidarDoubleTXT(JTextField ElTXT, JLabel ElTitulo) {
        if (ElTXT.getText().equals("")) {
            ElTitulo.setForeground(Color.RED);
            JOptionPane.showMessageDialog(null, "Complete el campo con titulo en rojo", "Error", JOptionPane.ERROR_MESSAGE);
            ElTXT.requestFocus();
            return false;
        } else {
            String ElTXTString = ElTXT.getText().replace(".", "");
            ElTXTString = ElTXT.getText().replace(",", ".");
            try {
                double ElTXTDouble = Double.parseDouble(ElTXTString);
                return true;
            } catch (NumberFormatException e) {
                System.out.println("Error al validar double, no valido: " + ElTXTString);
                log_historial.error("Error 1022: " + e);
                e.printStackTrace();

                ElTitulo.setForeground(Color.RED);
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "Valor no válido, complete el campo con titulo en rojo", "Error", JOptionPane.ERROR_MESSAGE);
                ElTXT.requestFocus();
                return false;
            }
        }
    }

    //Comprueba si el campo está vacio, pone el titulo en rojo si es vacio
    public boolean ValidarCampoVacioTXT(JTextField ElTXT, JLabel ElTitulo) {
        if (ElTXT.getText().equals("")) { //Si es vacio pone el titulo en rojo
            ElTitulo.setForeground(Color.RED);
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Complete el campo con titulo en rojo", "Advertencia", JOptionPane.WARNING_MESSAGE);
            ElTXT.requestFocus();
            return false;
        }
        return true;
    }
};
