package utilidades;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObtenerCotizacion {

    public static void main(String[] args) {
        try {
            System.out.println("Ejecuntando ObtenerCotizacion");
            //se abre la conexiòn
            URL url = new URL("http://www.cambioschaco.com.py/");
            //URL url = new URL("http://www.cambioschaco.com.py/php/chaco_cotizaciones_nuevo.php");
            URLConnection conexion = url.openConnection();
            conexion.connect();

            //Lectura
            InputStream is = conexion.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            char[] buffer = new char[1000];
            //ACA EMPIEZA MI CONDICIÓN DESDE ACÁ PODES CAMBIAR VOS
            int leido;
            String texto = "";
            while ((leido = br.read(buffer)) > 0) {
                String datos = new String(buffer, 0, leido);
                texto += datos;
            }
            String[] tr = texto.split("<tr");
            String fila = "";
            for (String linea : tr) {
                if (linea.contains("Dólar Americano")) {
                    fila = linea;
                }
            }
            String[] td = fila.split("<td");
            String columna = "";
            for (String linea : td) {
                if (linea.contains(",00")) {
                    System.out.println("---> " + linea);
                    int pos1 = 0;
                    int pos2 = 0;
                    String cotizacion = "";
                    for (int i = 0; i < linea.length(); i++) {
                        if (linea.substring(i, i + 1).equals(">")) {
                            pos1 = i;
                        } else if (linea.substring(i, i + 1).equals("<")) {
                            pos2 = i;
                            break;
                        }
                    }
                    System.out.println("---> " + linea.substring(pos1 + 1, pos2));
                }
            }
            //FIN DE LA CONDICION

        } catch (MalformedURLException ex) {
            Logger.getLogger(ObtenerCotizacion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ObtenerCotizacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
