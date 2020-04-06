package io.GestionTiendas.Server;

// Paquetes del framework estandar de java
import java.util.List;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.sql.SQLException;

// Paquetes del framework extendido de java
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.servlet.http.HttpServletRequest;

// Paquetes para la validación del token de inicio de sesión.
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;

import io.GestionTiendas.Server.ServerApp;

public class ServerUtils {
    public interface Content {
        /**
         * Funcion lambda para permitir la personalizacion de instrucciones que se van a
         * ejecutar en los metodos generic de este archivo de utilidades.
         * 
         * @return La respuesta del servidor preconstruida.
         * @throws Exception Puede devolver cualquier tipo de excepcion que será
         *                   capturado por los métodos generic.
         */
        ResponseBuilder executeContent() throws Exception;
    }

    private static String base64Key = System.getenv("LOGIN_KEY");

    /**
     * Método para procesar y responder a una petición generica con las
     * comprobaciones, requeridas por el servidor, y por ende por el proyecto.
     * 
     * Se checkeará si el usuario ha iniciado sesión validando el token que nos
     * habrá entregado en la cabecera AUTHENTICATOR de la petición HTTP.
     * 
     * Si la cabecera no estuviera presente, si hubiera algún inconveniente con la
     * base de datos o otro problema, el método devolverá al cliente un error 500.
     * 
     * Si no, devolverá un 200 indicando que ha sido procesado correctamente.
     * 
     * @param req      La petición HTTP.
     * @param paramId  A que Id se refiere, en caso de referirse a uno.
     * @param jsonData Los datos en Json que ha enviado el cliente
     * @param content  Funcion lambda con todos los pasos a seguir.
     * @return La respuesta con la cual responderemos al cliente.
     */
    public static Response genericMethod(HttpServletRequest req, String paramId, String jsonData, Content content) {
        // Convertimos la información JSON recibida en un objeto.
        ResponseBuilder response = null;

        try {
            // Validamos la conexión con la base de datos.
            if (ServerApp.getConnection() == null)
                ServerApp.setConnection();

            // Lanzamos el resto de la secuencia a ejecutar.
            response = content.executeContent();
        } catch (SQLException e) {
            // Detectamos errores en la SQL
            ServerApp.getLoggerSystem().warning("Error en procesar la consulta SQL.");
            response = Response.serverError();
        } catch (Exception e) {
            // En caso de existir otros errores, devolvemos un error 500 y listo.
            ServerApp.getLoggerSystem().warning("Error imprevisto, devolviendo error 500...");
            response = Response.serverError();
        }

        // Lanzamos la respuesta.
        return response.build();
    }

    /**
     * Método para procesar y responder a una petición generica con las
     * comprobaciones, requeridas por el servidor, y por ende por el proyecto.
     * 
     * Se checkeará si el usuario ha iniciado sesión validando el token que nos
     * habrá entregado en la cabecera AUTHENTICATOR de la petición HTTP.
     * 
     * Si la cabecera no estuviera presente, si hubiera algún inconveniente con la
     * base de datos o otro problema, el método devolverá al cliente un error 500.
     * 
     * Si no, devolverá un 200 indicando que ha sido procesado correctamente.
     * 
     * @param req      La petición HTTP.
     * @param paramId  A que Id se refiere, en caso de referirse a uno.
     * @param jsonData Los datos en Json que ha enviado el cliente
     * @param content  Funcion lambda con todos los pasos a seguir.
     * @return La respuesta con la cual responderemos al cliente.
     */
    public static Response genericUserMethod(HttpServletRequest req, String paramId, String jsonData, Content content) {
        // Obtenemos el token
        String token = getToken(req);

        // Convertimos la información JSON recibida en un objeto.
        ResponseBuilder response = null;

        if (isTokenLogged(token) != false) {
            try {
                // Validamos la conexión con la base de datos.
                if (ServerApp.getConnection() == null)
                    ServerApp.setConnection();

                // Lanzamos el resto de la secuencia a ejecutar.
                response = content.executeContent();
            } catch (SQLException e) {
                // Detectamos errores en la SQL
                ServerApp.getLoggerSystem().warning("Error en procesar la consulta SQL.");
                response = Response.serverError();
            } catch (Exception e) {
                // En caso de existir otros errores, devolvemos un error 500 y listo.
                ServerApp.getLoggerSystem().warning("Error imprevisto, devolviendo error 500...");
                response = Response.serverError();
            }
        } else {
            response = Response.status(Status.FORBIDDEN);
        }

        // Lanzamos la respuesta.
        return response.build();
    }

    /**
     * Método para procesar y responder a una petición generica con las
     * comprobaciones requeridas a un usuario administrador, requeridas por el
     * servidor, y por ende por el proyecto.
     * 
     * Se checkeará si el usuario ha iniciado sesión validando el token que nos
     * habrá entregado en la cabecera AUTHENTICATOR de la petición HTTP.
     * 
     * Si la cabecera no estuviera presente, si hubiera algún inconveniente con la
     * base de datos o otro problema, el método devolverá al cliente un error 500.
     * 
     * Si no, devolverá un 200 indicando que ha sido procesado correctamente.
     * 
     * @param req      La petición HTTP.
     * @param paramId  A que Id se refiere, en caso de referirse a uno.
     * @param jsonData Los datos en Json que ha enviado el cliente
     * @param content  Funcion lambda con todos los pasos a seguir.
     * @return La respuesta con la cual responderemos al cliente.
     */
    public static Response genericAdminMethod(HttpServletRequest req, String paramId, String jsonData,
            Content content) {
        // Obtenemos el token
        String token = getToken(req);

        // Convertimos la información JSON recibida en un objeto.
        ResponseBuilder response = null;

        if (isTokenLogged(token) != false && isTokenAdmin(token) == true) {
            try {
                // Validamos la conexión con la base de datos.
                if (ServerApp.getConnection() == null)
                    ServerApp.setConnection();

                // Lanzamos el resto de la secuencia a ejecutar.
                response = content.executeContent();
            } catch (SQLException e) {
                // Detectamos errores en la SQL
                ServerApp.getLoggerSystem().warning("Error en procesar la consulta SQL.");
                response = Response.serverError();
            } catch (Exception e) {
                // En caso de existir otros errores, devolvemos un error 500 y listo.
                ServerApp.getLoggerSystem().warning("Error imprevisto, devolviendo error 500...");
                response = Response.serverError();
            }
        } else {
            response = Response.status(Status.FORBIDDEN);
        }

        // Lanzamos la respuesta.
        return response.build();
    }

    /**
     * Método para recuperar el token recibido desde la cabecera AUTHENTICATOR el
     * token de seguridad.
     * 
     * @param req La cabecera de la petición.
     * @return El token recibido.
     */
    public static String getToken(final HttpServletRequest req) {
        // Declaramos las variables
        String token = null;
        List<String> authHeader = null;

        // Obtenemos el token
        authHeader = Collections.list(req.getHeaders(HttpHeaders.AUTHORIZATION));

        // Comprobamos que exista un token.
        if (authHeader.size() != 0)
            token = authHeader.get(0).substring(7).trim();

        // Devolvemos el token.
        return token;
    }

    /**
     * Método para validar si el token pasado por parametro es de un login valido o
     * no.
     * 
     * @param token El token JWT.
     * @return Verdadero si es valido o falso si no lo es.
     */
    public static boolean isTokenLogged(final String token) {
        // Iniciamos el parser con la clave.
        boolean returnVar = false;

        try {
            // Inicializamos el parser del token.
            final JwtParser jwtsParser = Jwts.parser();

            // Cargamos la llave y validamos el token
            jwtsParser.setSigningKey(base64Key);
            jwtsParser.parseClaimsJws(token);

            // Si no ha lanzado una excepción anda okey el token.
            returnVar = true;
        } catch (final SignatureException e) {
            // Si la firma es invalida, anulamos el token.
            returnVar = false;
        } catch (final ExpiredJwtException e) {
            // Si el token ha expirado, anulamos el token.
            returnVar = false;
        } catch (final Exception e) {
            // Si es cualquier otra excepción no prevista, anulamos el token.
            returnVar = false;
        }

        // Devolvemos el estado del token esté como este.
        return returnVar;
    }

    /**
     * Método para validar si el token pasado por paraametro es de un usuario
     * administrador.
     * 
     * @param token El token JWT.
     * @return Verdadero si es valido o falso si no lo es.
     */
    public static boolean isTokenAdmin(final String token) {
        // Iniciamos el parser con la clave.
        boolean returnVar = false;

        try {
            // Inicializamos el parser del token.
            final JwtParser jwtsParser = Jwts.parser();

            // Cargamos la llave y validamos el token.
            jwtsParser.setSigningKey(base64Key);
            jwtsParser.parseClaimsJws(token);

            // Obtenemos los datos del token.
            final Claims claims = jwtsParser.parseClaimsJws(token).getBody();

            // Validamos la conexión con la base de datos.
            if (ServerApp.getConnection() == null)
                ServerApp.setConnection();

            // Ejecutamos la consulta de verificación.
            Statement st = ServerApp.getConnection().createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Users WHERE id = " + claims.getId());

            // Obtenemos el resultado.
            rs.next();

            // Si no ha lanzado una excepción, comprobamos si es admin.
            returnVar = claims.get("adm").toString().equals("true") && rs.getBoolean("isAdmin");
        } catch (final SignatureException e) {
            // Si la firma es invalida, anulamos el token.
            returnVar = false;
        } catch (final ExpiredJwtException e) {
            // Si el token ha expirado, anulamos el token.
            returnVar = false;
        } catch (final Exception e) {
            // Si es cualquier otra excepción no prevista, anulamos el token.
            returnVar = false;
        }

        // Devolvemos el estado del token esté como este.
        return returnVar;
    }

    /**
     * Método para recuperar la llave que firma el Json Web Token que recibe el
     * cliente, tambien sirve esta llave para validar si el token de seguridad no ha
     * sido manipulado.
     * 
     * @return Llave en base64 del Json Web Token
     */

    public static String getBase64Key() {
        return ServerUtils.base64Key;
    }
}