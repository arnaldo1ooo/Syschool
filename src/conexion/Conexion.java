package conexion;

import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import utilidades.Metodos;

public class Conexion {

    public Connection connection;
    public Statement st;
    public ResultSet rs;
    private static String controlador;
    private static String usuarioDB;
    private static String passDB; //Contrasena de la BD
    private static String nombreBD;
    private static String host;
    private static String puerto;
    private static String servidor;
    private Conexion con;

    public static Connection ConectarBasedeDatos() {
        String tipoHost = "local";
        switch (tipoHost) {
            case "local":
                //Modo host local
                controlador = "com.mysql.cj.jdbc.Driver";
                usuarioDB = "root";
                passDB = "toor5127"; //Contrasena de la BD
                nombreBD = "syschool";
                host = "localhost";
                puerto = "3306";
                servidor = "jdbc:mysql://" + host + ":" + puerto + "/" + nombreBD
                        + "?useUnicode=true"
                        + "&useJDBCCompliantTimezoneShift=true"
                        + "&useLegacyDatetimeCode=false"
                        + "&serverTimezone=America/Mexico_City"
                        //+ "&serverTimezone=UTC"
                        + "&useSSL=false"
                        + "&allowPublicKeyRetrieval=true";
                break;
            case "remoto":
                //Modo host remoto
                controlador = "com.mysql.cj.jdbc.Driver";
                usuarioDB = "supervisor";
                passDB = "toor5127"; //Contrasena de la BD
                nombreBD = "syschool";
                host = "192.168.88.240"; //San roque 192.168.1.240
                puerto = "3306";
                servidor = "jdbc:mysql://" + host + ":" + puerto + "/" + nombreBD
                        + "?useUnicode=true"
                        + "&useJDBCCompliantTimezoneShift=true"
                        + "&useLegacyDatetimeCode=false"
                        + "&serverTimezone=UTC"
                        + "&useSSL=false";
                break;
            case "online":
                //Modo host online
                controlador = "com.mysql.cj.jdbc.Driver";
                usuarioDB = "root";
                passDB = "toor5127"; //Contrasena de la BD
                nombreBD = "escuela";
                host = "181.123.175.39";
                puerto = "3306";
                servidor = "jdbc:mysql://" + host + ":" + puerto + "/" + nombreBD
                        + "?useUnicode=true"
                        + "&useJDBCCompliantTimezoneShift=true"
                        + "&useLegacyDatetimeCode=false"
                        + "&serverTimezone=UTC"
                        + "&useSSL=false";
                break;

            default:
                JOptionPane.showMessageDialog(null, "No se encontró la moneda seleccionada", "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }

        Connection conexion;
        try {
            Class.forName(controlador);
            conexion = DriverManager.getConnection(servidor, usuarioDB, passDB);
            if (conexion != null) {
                System.out.println("\nCONEXIÓN A " + nombreBD + ", EXITOSA..");

            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error1 al conecta al BD, Verifique los datos de la conexion a la BD");
            System.out.println("Error1 al conecta al BD, Verifique los datos de la conexion a la BD  " + ex.getMessage());
            conexion = null;
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error2 al conecta al BD, Verifique los datos de la conexion a la BD");
            System.out.println("Error2 al conecta al BD, Verifique los datos de la conexion a la BD  " + ex.getMessage());
            conexion = null;
        } catch (Exception ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error3 al conecta al BD, Verifique los datos de la conexion a la BD");
            System.out.println("Error3 al conecta al BD, Verifique los datos de la conexion a la BD  " + ex.getMessage());
            conexion = null;
        }
        return conexion;
    }

    public void DesconectarBasedeDatos() {
        try {
            if (connection != null) {
                connection.close();
                if (st != null) {
                    st.close();
                    if (rs != null) {
                        rs.close();
                    }
                }
                System.out.println("DESCONEXIÓN DEL CONNECTION(" + nombreBD + "), RESULTSET y del STATEMENT, EXITOSA..\n");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR AL INTENTAR DESCONECTAR "
                    + "CONNECTION(" + nombreBD + "), RESULTSET y del STATEMENT", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public int NumColumnsRS() {
        int NumColumnsRS = -1;
        try {
            NumColumnsRS = rs.getMetaData().getColumnCount();

        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return NumColumnsRS;
    }

    DefaultTableModel modelotabla;
    ResultSetMetaData mdrs;
    int numColumns;
    Object[] registro;

    public DefaultTableModel ConsultaTableBD(String sentencia, String titlesJtabla[], JComboBox ElComboCampos) {
        modelotabla = new DefaultTableModel(null, titlesJtabla);
        con = ObtenerRSSentencia(sentencia);
        try {
            mdrs = con.rs.getMetaData();
            numColumns = mdrs.getColumnCount();
            registro = new Object[numColumns]; //el numero es la cantidad de columnas del rs
            while (con.rs.next()) {
                for (int c = 0; c < numColumns; c++) {
                    registro[c] = (con.rs.getString(c + 1));
                }
                modelotabla.addRow(registro);//agrega el registro a la tabla
            }
            //Carga el combobox con los titulos de la tabla, solo si esta vacio
            if (ElComboCampos != null && ElComboCampos.getItemCount() == 0) {
                javax.swing.DefaultComboBoxModel modelCombo = new javax.swing.DefaultComboBoxModel(titlesJtabla);
                ElComboCampos.setModel(modelCombo);
            }
        } catch (SQLException ex) {
            System.out.println("Error en ConsultaTableBD " + ex);
        }
        con.DesconectarBasedeDatos();
        return modelotabla;
    }

    public Conexion ObtenerRSSentencia(String sentencia) { //con.Desconectar luego de usar el metodo
        con = new Conexion();
        try {
            System.out.println("Ejecutar sentencia ObtenerRSSentencia " + sentencia);
            con.connection = (Connection) Conexion.ConectarBasedeDatos();
            con.st = con.connection.createStatement();
            con.rs = con.st.executeQuery(sentencia);
            int cantreg = 0;
            while (con.rs.next() && cantreg < 2) { //Revisamos cuantos registro trajo la consulta
                cantreg = cantreg + 1;
            }

            switch (cantreg) {
                case 0:
                    System.out.println("ObtenerRSSentencia no trajo ningun resultado");
                    //con.rs.beforeFirst(); //Ponemos antes del primer registro en el puntero
                    break;
                case 1:
                    System.out.println("ObtenerRSSentencia trajo un resultado");
                    con.rs.beforeFirst(); //Ponemos antes del primer registro en el puntero
                    break;
                case 2:
                    System.out.println("ObtenerRSSentencia trajo mas de un resultado");
                    con.rs.beforeFirst(); //Ponemos antes del primer registro en el puntero
                    break;
                default:
                //aca se escribe lo que si o si se ejecuta
            }
        } catch (SQLException e) {
            System.out.println("Error al EjecutarSentencia ObtenerRSSentencia,  sentencia: " + sentencia + ",  ERROR " + e);
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, e);
        } catch (NullPointerException e) {
            System.out.println("ObtenerRSSentencia no trajo ningun resultado (null),  sentencia: " + sentencia + ",  ERROR " + e);
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, e);
        }
        return con;
    }

    public void EjecutarABM(String sentencia, boolean conAviso) {
        //Ejecuta consultas de Altas, Bajas y Modificaciones
        try {
            System.out.println("EjecutarABM: " + sentencia);
            connection = Conexion.ConectarBasedeDatos();
            st = connection.createStatement();
            st.executeUpdate(sentencia);
            connection.close();
            st.close();

            if (conAviso == true) {
                Toolkit.getDefaultToolkit().beep(); //BEEP
                JOptionPane.showMessageDialog(null, "La operación se realizó correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Metodos.class.getName() + " Sentencia: " + sentencia).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error al intentar crear o modificar registro" + ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Boolean SiYaExisteEnLaBD(String sentencia) { //con.Desconectar luego de usar el metodo
        con = new Conexion();
        int cantreg = 0;
        try {
            System.out.println("Comprobar si ya existe en la BD: " + sentencia);
            con.connection = (Connection) Conexion.ConectarBasedeDatos();
            con.st = con.connection.createStatement();
            con.rs = con.st.executeQuery(sentencia);
            while (con.rs.next() && cantreg < 2) { //Revisamos cuantos registro trajo la consulta
                cantreg = cantreg + 1;
            }

        } catch (SQLException e) {
            System.out.println("Error al EjecutarSentencia SiYaExisteEnLaBD,  sentencia: " + sentencia + ",  ERROR " + e);
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, e);
        } catch (NullPointerException e) {
            System.out.println("SiYaExisteEnLaBD no trajo ningun resultado (null),  sentencia: " + sentencia + ",  ERROR " + e);
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, e);
        }
        con.DesconectarBasedeDatos();

        if (cantreg > 0) {
            return true;
        } else {
            return false;
        }
    }
}
