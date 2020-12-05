/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilidades;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
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
        if (((car < 'a' || car > 'z') && (car < 'A' || car > 'z')) && (car != 'ñ') && (car != (char) KeyEvent.VK_BACK_SPACE) && (car != (char) KeyEvent.VK_SPACE)) {
            evt.consume();
        }
    }

    public void SoloNumeroEnteroKeyTyped(KeyEvent evt) {
        //Declaramos una variable y le asignamos un evento
        char car = evt.getKeyChar();
        //Condicion que nos permite ingresar datos de tipo numerico
        if (((car < '0' || car > '9') && (car != (char) KeyEvent.VK_BACK_SPACE))) {
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

    public String DoubleFormatoSudamericaKeyReleased(String elNumero) {
        //Si es vacio devuelve lo mismo
        if (elNumero.equals("")) {
            return elNumero;
        }
        String elNumeroModi = "";
        try {
            int siEsDecimal = elNumero.indexOf(",");
            String sub1;
            String sub2;
            String sub3;
            if (siEsDecimal != -1) { //Si es decimal, si tiene coma
                int parteEntera = (int) Double.parseDouble(elNumero.replace(",", "."));
                String parteDecimal = elNumero.substring(elNumero.indexOf(','));
                String parteEnteraString = parteEntera + "";
                int longitud = parteEnteraString.length();

                switch (longitud) {
                    case 1 -> {
                        return elNumero;
                    }
                    case 2 -> {
                        return elNumero;
                    }
                    case 3 -> {
                        return elNumero;
                    }
                    case 4 -> {
                        sub1 = parteEnteraString.substring(0, 1);
                        sub2 = parteEnteraString.substring(1, 4);
                        elNumeroModi = sub1 + "." + sub2 + parteDecimal;
                        return elNumeroModi;
                    }
                    case 5 -> {
                        sub1 = parteEnteraString.substring(0, 2);
                        sub2 = parteEnteraString.substring(2, 5);
                        elNumeroModi = sub1 + "." + sub2 + parteDecimal;
                        return elNumeroModi;
                    }
                    case 6 -> {
                        sub1 = parteEnteraString.substring(0, 3);
                        sub2 = parteEnteraString.substring(3, 6);
                        elNumeroModi = sub1 + "." + sub2 + parteDecimal;
                        return elNumeroModi;
                    }
                    case 7 -> {
                        sub1 = parteEnteraString.substring(0, 1);
                        sub2 = parteEnteraString.substring(1, 4);
                        sub3 = parteEnteraString.substring(4, 7);
                        elNumeroModi = sub1 + "." + sub2 + "." + sub3 + parteDecimal;
                        return elNumeroModi;
                    }
                    case 8 -> {
                        sub1 = parteEnteraString.substring(0, 2);
                        sub2 = parteEnteraString.substring(2, 5);
                        sub3 = parteEnteraString.substring(5, 8);
                        elNumeroModi = sub1 + "." + sub2 + "." + sub3 + parteDecimal;
                        return elNumeroModi;
                    }
                    case 9 -> {
                        sub1 = parteEnteraString.substring(0, 3);
                        sub2 = parteEnteraString.substring(3, 6);
                        sub3 = parteEnteraString.substring(6, 9);
                        elNumeroModi = sub1 + "." + sub2 + "." + sub3 + parteDecimal;
                        return elNumeroModi;
                    }
                    default -> {
                        //JOptionPane.showMessageDialog(null, "Ninguno", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else { //Si no es decimal
                elNumero = elNumero.replace(".", "");
                elNumeroModi = elNumero;
                int longitud = elNumero.length();

                switch (longitud) {
                    case 4 -> {
                        sub1 = elNumero.substring(0, 1);
                        sub2 = elNumero.substring(1, 4);
                        elNumeroModi = sub1 + "." + sub2;
                        return elNumeroModi;
                    }
                    case 5 -> {
                        sub1 = elNumero.substring(0, 2);
                        sub2 = elNumero.substring(2, 5);
                        elNumeroModi = sub1 + "." + sub2;
                        return elNumeroModi;
                    }
                    case 6 -> {
                        sub1 = elNumero.substring(0, 3);
                        sub2 = elNumero.substring(3, 6);
                        elNumeroModi = sub1 + "." + sub2;
                        return elNumeroModi;
                    }
                    case 7 -> {
                        sub1 = elNumero.substring(0, 1);
                        sub2 = elNumero.substring(1, 4);
                        sub3 = elNumero.substring(4, 7);
                        elNumeroModi = sub1 + "." + sub2 + "." + sub3;
                        return elNumeroModi;
                    }
                    case 8 -> {
                        sub1 = elNumero.substring(0, 2);
                        sub2 = elNumero.substring(2, 5);
                        sub3 = elNumero.substring(5, 8);
                        elNumeroModi = sub1 + "." + sub2 + "." + sub3;
                        return elNumeroModi;
                    }
                    case 9 -> {
                        sub1 = elNumero.substring(0, 3);
                        sub2 = elNumero.substring(3, 6);
                        sub3 = elNumero.substring(6, 9);
                        elNumeroModi = sub1 + "." + sub2 + "." + sub3;
                        return elNumeroModi;
                    }
                    default -> {
                        //JOptionPane.showMessageDialog(null, "Ninguno", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (NumberFormatException e) {
            elNumeroModi = elNumero;
            System.out.println("El Numero (" + elNumero + ") no es valido, error en el metodo DoubleFormatoSudamericaKeyReleased  " + e);
            log_historial.warn("Error 1017: " + e);
            e.printStackTrace();
        }
        return elNumeroModi;
    }

    //Convertir de Double americano a Double Sudamericano
    public String DoubleAFormatoSudamerica(double elDoubleAmericano) {
        //System.out.println("Sudamerica: " + elDoubleAmericano);
        String elDoubleSudamerica = "";
        BigDecimal elDoubleBig = BigDecimal.valueOf(elDoubleAmericano);
        try {
            String[] parts = elDoubleBig.toString().split("\\.");
            String parteEntera = parts[0];
            String parteDecimal = "." + parts[1];

            parteDecimal = parteDecimal.replace(".", ",");
            if (parteDecimal.equals(",0")) {
                parteDecimal = "";
            }
            int longitud = parteEntera.length();
            if (longitud == 1 || longitud == 2 || longitud == 3) {
                elDoubleSudamerica = parteEntera + parteDecimal;
                return elDoubleSudamerica;
            }
            if (longitud == 4) {
                String sub1 = parteEntera.substring(0, 1);
                String sub2 = parteEntera.substring(1, 4);
                elDoubleSudamerica = sub1 + "." + sub2 + parteDecimal;
                return elDoubleSudamerica;
            }
            if (longitud == 5) {
                String sub1 = parteEntera.substring(0, 2);
                String sub2 = parteEntera.substring(2, 5);
                elDoubleSudamerica = sub1 + "." + sub2 + parteDecimal;
                return elDoubleSudamerica;
            }

            if (longitud == 6) {
                String sub1 = parteEntera.substring(0, 3);
                String sub2 = parteEntera.substring(3, 6);
                elDoubleSudamerica = sub1 + "." + sub2 + parteDecimal;
                return elDoubleSudamerica;
            }

            if (longitud == 7) {
                String sub1 = parteEntera.substring(0, 1);
                String sub2 = parteEntera.substring(1, 4);
                String sub3 = parteEntera.substring(4, 7);
                elDoubleSudamerica = sub1 + "." + sub2 + "." + sub3 + parteDecimal;
                return elDoubleSudamerica;
            }

            if (longitud == 8) {
                String sub1 = parteEntera.substring(0, 2);
                String sub2 = parteEntera.substring(2, 5);
                String sub3 = parteEntera.substring(5, 8);
                elDoubleSudamerica = sub1 + "." + sub2 + "." + sub3 + parteDecimal;
                return elDoubleSudamerica;
            }

            if (longitud == 9) {
                String sub1 = parteEntera.substring(0, 3);
                String sub2 = parteEntera.substring(3, 6);
                String sub3 = parteEntera.substring(6, 9);
                elDoubleSudamerica = sub1 + "." + sub2 + "." + sub3 + parteDecimal;
                return elDoubleSudamerica;
            }
        } catch (NumberFormatException e) {
            System.out.println("El Numero " + elDoubleAmericano + "  no es valido, error al convertir a formato sudamericano " + e);
            log_historial.error("Error 1018: " + e);
            e.printStackTrace();
        }
        return elDoubleSudamerica;
    }

    public Double DoubleAFormatoAmericano(String ElNumero) {
        double ElNumeroDouble = 0.0;
        try {
            if (ElNumero.endsWith(".0")) { //Si termina en .0 se borra esa parte
                ElNumero = ElNumero.substring(0, ElNumero.length() - 2); //Borra los dos ultimos caracteres de la cadena
            }

            ElNumero = ElNumero.replace(".", ""); //Saca los puntos de miles
            ElNumero = ElNumero.replace(",", "."); //Cambia la coma en punto
            ElNumeroDouble = Double.parseDouble(ElNumero);
        } catch (NumberFormatException e) {
            System.out.println("Error DoubleAFormatoAmericano " + e);
            log_historial.error("Error 1019: " + e);
            e.printStackTrace();
        }
        return ElNumeroDouble;
    }

    public int StringSinPuntosMiles(String elNumString) {
        elNumString = elNumString.replace(".", "");
        int elNumInt = Integer.parseInt(elNumString);

        return elNumInt;
    }

    //Poner los puntos de miles al Integer
    public String StringPuntosMiles(String elNumString) {
        //System.out.println("StringPuntosMiles: " + elNumString);
        elNumString = elNumString.replace(".", "");
        String ElNumConPuntos = "";
        String sub1;
        String sub2;
        String sub3;

        if (elNumString.equals("")) { //Si es vacio
            return elNumString;
        }

        String longitud = elNumString.length() + "";
        switch (longitud) {
            case "1" -> {
                ElNumConPuntos = elNumString;
                return ElNumConPuntos;
            }
            case "2" -> {
                ElNumConPuntos = elNumString;
                return ElNumConPuntos;
            }
            case "3" -> {
                ElNumConPuntos = elNumString;
                return ElNumConPuntos;
            }
            case "4" -> {
                sub1 = elNumString.substring(0, 1);
                sub2 = elNumString.substring(1, 4);
                ElNumConPuntos = sub1 + "." + sub2;
                return ElNumConPuntos;
            }
            case "5" -> {
                sub1 = elNumString.substring(0, 2);
                sub2 = elNumString.substring(2, 5);
                ElNumConPuntos = sub1 + "." + sub2;
                return ElNumConPuntos;
            }
            case "6" -> {
                sub1 = elNumString.substring(0, 3);
                sub2 = elNumString.substring(3, 6);
                ElNumConPuntos = sub1 + "." + sub2;
                return ElNumConPuntos;
            }
            case "7" -> {
                sub1 = elNumString.substring(0, 1);
                sub2 = elNumString.substring(1, 4);
                sub3 = elNumString.substring(4, 7);
                ElNumConPuntos = sub1 + "." + sub2 + "." + sub3;
                return ElNumConPuntos;
            }
            case "8" -> {
                sub1 = elNumString.substring(0, 2);
                sub2 = elNumString.substring(2, 5);
                sub3 = elNumString.substring(5, 8);
                ElNumConPuntos = sub1 + "." + sub2 + "." + sub3;
                return ElNumConPuntos;
            }
            case "9" -> {
                sub1 = elNumString.substring(0, 3);
                sub2 = elNumString.substring(3, 6);
                sub3 = elNumString.substring(6, 9);
                ElNumConPuntos = sub1 + "." + sub2 + "." + sub3;
                return ElNumConPuntos;
                //break;
            }
            //break;

            default -> {
                System.out.println("longitud o algun caracter invalido: longitud: " + longitud + ", elNumString: " + elNumString);
                return ElNumConPuntos;
            }
        }
    }

    //Formatear double para que tenga solo dos numeros despues de la coma, y la coma es punto
    public double FormatearATresDecimales(double ElDouble) {
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
