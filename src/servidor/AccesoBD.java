package servidor;

import java.io.*;
import java.sql.*;
import java.util.*;

public class AccesoBD {
    private Connection con;

    public AccesoBD() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.con =
                    DriverManager.getConnection(
                            "jdbc:postgresql://localhost:5432/project", "postgres", "password");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean conexionAbiertaCorrectamente() {
        return (this.con != null);
    }

    public boolean existeUsuarioContrasena(String username, String password) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con.setAutoCommit(true);
            ps = con.prepareStatement("SELECT * FROM USUARIOS WHERE NOMBRE = ? AND CONTRASENA = ? ;");
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                System.out.println("No se pudo cerrar cursores");
            }
        }
        return false;
    }

    public boolean existeSolicitud(String solicitante, String solicitado) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con.setAutoCommit(true);
            ps = con.prepareStatement("SELECT * FROM SOLICITUDES WHERE solicitante = ? AND solicitado = ? ;");
            ps.setString(1, solicitante);
            ps.setString(2, solicitado);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                System.out.println("No se pudo cerrar cursores");
            }
        }
        return false;
    }


    public boolean almacenarUsuario(String nombre, String contrasena) {

        PreparedStatement ps = null;
        try {
            con.setAutoCommit(true);
            ps =
                    con.prepareStatement(
                            "INSERT INTO USUARIOS(nombre, contrasena) VALUES(?, ?);");

            ps.setString(1, nombre);
            ps.setString(2, contrasena);

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                System.out.println("No se pudo cerrar cursores");
            }
        }
        return false;
    }

    public ArrayList<String> obtenerAmigos(String nombre) {
        ArrayList<String> amigos = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con.setAutoCommit(true);
            ps = con.prepareStatement("SELECT AMIGO FROM AMIGOS WHERE USUARIO = ?;");
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            while (rs.next()) {
                amigos.add(rs.getString("AMIGO"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                System.out.println("No se pudo cerrar cursores");
            }
        }
        return amigos;
    }

    public ArrayList<String> obtenerSolicitudes(String nombre) {
        ArrayList<String> solicitudes = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con.setAutoCommit(true);
            ps = con.prepareStatement("SELECT solicitante FROM solicitudes WHERE solicitado = ?;");
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            while (rs.next()) {
                solicitudes.add(rs.getString("solicitante"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                System.out.println("No se pudo cerrar cursores");
            }
        }
        return solicitudes;
    }

    public boolean solicitar(String solicitante, String solicitado) {
        PreparedStatement ps = null;
        try {
            con.setAutoCommit(true);
            ps =
                    con.prepareStatement(
                            "INSERT INTO SOLICITUDES(solicitante, solicitado) VALUES(?, ?);");

            ps.setString(1, solicitante);
            ps.setString(2, solicitado);

            return (ps.executeUpdate() > 0);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                System.out.println("No se pudo cerrar cursores");
            }
        }
        return false;
    }


    public boolean amigar(String usuario1, String usuario2) {
        boolean exito = false;
        PreparedStatement ps = null;
        try {
            con.setAutoCommit(false);
            ps = con.prepareStatement("DELETE FROM SOLICITUDES WHERE ( solicitante = ? AND  solicitado = ? ) OR ( solicitante = ? AND  solicitado = ? );");
            ps.setString(1, usuario1);
            ps.setString(2, usuario2);
            ps.setString(3, usuario2);
            ps.setString(4, usuario1);
            ps.executeUpdate();

            ps = con.prepareStatement(
                    "INSERT INTO AMIGOS(usuario, amigo) VALUES( ?, ? ) ;");
            ps.setString(1, usuario1);
            ps.setString(2, usuario2);
            ps.executeUpdate();

            ps = con.prepareStatement(
                    "INSERT INTO AMIGOS(usuario, amigo) VALUES( ?, ? ) ;");
            ps.setString(1, usuario2);
            ps.setString(2, usuario1);
            ps.executeUpdate();

            con.commit();
            exito = true;
            con.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                System.out.println("No se pudo cerrar cursores");
            }
        }
        return exito;
    }

    public boolean borrarSolicitud(String solicitante, String solicitado) {
        PreparedStatement ps = null;
        try {
            con.setAutoCommit(true);
            ps = con.prepareStatement("DELETE FROM SOLICITUDES WHERE  solicitante = ? AND  solicitado = ? ;");
            ps.setString(1, solicitante);
            ps.setString(2, solicitado);
            ps.executeUpdate();
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                System.out.println("No se pudo cerrar cursores");
            }
        }
        return false;
    }

    public boolean cambiarContra(String usuario, String newContra) {
        PreparedStatement ps = null;
        try {
            con.setAutoCommit(true);
            ps = con.prepareStatement("UPDATE USUARIOS SET CONTRASENA = ? WHERE  NOMBRE = ?;");
            ps.setString(1, newContra);
            ps.setString(2, usuario);
            ps.executeUpdate();

            return (ps.executeUpdate() > 0);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                System.out.println("No se pudo cerrar cursores");
            }
        }
        return false;
    }
}
