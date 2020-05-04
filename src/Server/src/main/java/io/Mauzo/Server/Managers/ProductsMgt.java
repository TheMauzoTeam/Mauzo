package io.Mauzo.Server.Managers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import io.Mauzo.Server.ServerUtils;
import io.Mauzo.Server.Templates.Product;

public class ProductsMgt implements ManagersIntf<Product>{
    private final PreparedStatement addQuery;
    private final PreparedStatement getIdQuery;
    private final PreparedStatement getNameQuery;
    private final PreparedStatement getListQuery;
    private final PreparedStatement modifyQuery;
    private final PreparedStatement removeQuery;

    public ProductsMgt(Connection connection) throws SQLException {
        addQuery = connection.prepareStatement("INSERT INTO Products (id, code, ProdName, ProdPrice, ProdDesc, ProdPic) VALUES (?, ?, ?, ?, ?, ?);");
        getIdQuery = connection.prepareStatement("SELECT * FROM Products WHERE id = ?;");
        getNameQuery = connection.prepareStatement("SELECT * FROM Products WHERE prodName = ?;");
        getListQuery = connection.prepareStatement("SELECT * FROM Products");
        modifyQuery = connection.prepareStatement("UPDATE Users SET code = ?, prodName = ?, prodPrice = ?, prodDesc = ? WHILE id = ?");
        removeQuery = connection.prepareStatement("DELETE FROM Products WHERE id = ?;");
    }
    //remove, modify, get, add, getProductsList

    /**
     * Método que añade productos a la base de datos.
     *
     * @param product El producto a añadir.
     * @Exception SQLException Excepcion de la consulta SQL
     */
    @Override
    public void add(Product product) throws SQLException{
        synchronized (addQuery) {
            //Asociamos los valores
            addQuery.setInt(1, product.getId());
            addQuery.setString(2, product.getCode());
            addQuery.setString(3, product.getName());
            addQuery.setDouble(4,product.getPrice());
            addQuery.setString(5, product.getDescription());
            addQuery.setBytes(6, ServerUtils.imageToByteArray(product.getPicture(),"png"));
            //Ejecutamos la sentencia SQl
            addQuery.execute();
        }
    }

    /**
     *Método para obtener el producto a partir de un id
     *
     * @param id    ID del objeto en la base de datos.
     * @return el producto en forma de objeto
     * @throws SQLException Excepción de la consuulta SQL
     * @throws ManagerErrorException Excepción dado al no poder encontrar el producto
     */
    @Override
    public Product get(int id) throws SQLException, ManagerErrorException {

        synchronized (getIdQuery) {
            Product product = null;

            //Asociamos los valores
             getIdQuery.setInt(1, id);

             //Ejecutamos la sentencia y conseguimos el resto de datos relacionados
             try (ResultSet resultSet = getIdQuery.executeQuery()){
                 if (!(resultSet.isLast())) {
                     while (resultSet.next()) {
                         product = new Product();

                         product.setId(resultSet.getInt("id"));
                         product.setCode(resultSet.getString("code"));
                         product.setName(resultSet.getString("prodName"));
                         product.setPrice(resultSet.getFloat("prodPrice"));
                         product.setDescription(resultSet.getString("prodDesc"));
                         product.setPicture(ServerUtils.imageFromByteArray(resultSet.getBytes("prodPic")));

                     }
                 }
                 else
                     throw new ManagerErrorException("No se ha encontrado el producto");
             }

             return product;
        }
    }

    /**
     * Método para obtener el producto a partir de su nombre
     *
     * @param name  El nombre del objeto en la base de datos.
     * @return El producto en forma de objeto
     * @throws SQLException Excepción en la consulta SQL
     * @throws ManagerErrorException Excepción dada en caso de no encontrar el producto
     */
    public Product get(String name) throws SQLException, ManagerErrorException {
       synchronized (getNameQuery) {
           Product product = null;

           getNameQuery.setString(1, name);

            try (ResultSet resultSet = getNameQuery.executeQuery()){
                if (!resultSet.isLast()){
                    while (resultSet.next()){
                        product = new Product();

                        product.setId(resultSet.getInt("id"));
                        product.setCode(resultSet.getString("code"));
                        product.setDescription(resultSet.getString("prodDesc"));
                        product.setPrice(resultSet.getFloat("prodPrice"));
                        product.setName(resultSet.getString("prodName"));
                        product.setPicture(ServerUtils.imageFromByteArray(resultSet.getBytes("prodPic")));

                    }
                }else {
                    throw new ManagerErrorException("No se ha encontrado el producto");
                }
            }
           return product;

       }
    }

    /**
     * Método para obtener la lista de productos de la base de datos
     *
     * @return La lista de productos
     * @throws SQLException Excepción en la consulta SQL
     */
    @Override
    public List<Product> getList() throws SQLException {
        synchronized (getListQuery) {
            List<Product> products = null;

            try(ResultSet resultSet = getListQuery.executeQuery()) {
                products = new ArrayList<>();

                while (resultSet.next()) {
                    Product product = new Product();

                    product.setId(resultSet.getInt("id"));
                    product.setName(resultSet.getString("prodName"));
                    product.setPrice(resultSet.getFloat("prodPrice"));
                    product.setCode(resultSet.getString("code"));
                    product.setDescription(resultSet.getString("prodDesc"));
                    product.setPicture(ServerUtils.imageFromByteArray(resultSet.getBytes("prodPic")));

                    products.add(product);
                }
            }

            return products;
        }
    }

    /**
     * Método para poder modificar el producto en la base de datos
     *
     * @param obj El producto en la base de datos
     * @throws SQLException Excepción en la consulta SQL
     * @throws ManagerErrorException Excepción que se da al no encontrar el producto
     */
    @Override
    public void modify(Product obj) throws SQLException, ManagerErrorException {
        synchronized (modifyQuery) {
            modifyQuery.setString(1, obj.getCode());
            modifyQuery.setString(2, obj.getName());
            modifyQuery.setDouble(3, obj.getPrice());
            modifyQuery.setString(4,obj.getDescription());
            modifyQuery.setBytes(7,ServerUtils.imageToByteArray(obj.getPicture(),"png"));
        }
    }

    /**
     * Método para eliminar el producto
     * @param obj producto de la base de datos
     * @throws SQLException Excepción de la consulta SQL
     * @throws ManagerErrorException Excepción dada al no encontrar el producto
     */
    @Override
    public void remove(Product obj) throws SQLException, ManagerErrorException {
        synchronized (removeQuery) {
            removeQuery.setInt(1, obj.getId());

            // Ejecutamos la sentencia sql.
            if(removeQuery.execute() == false)
                throw new ManagerErrorException("No se ha encontrado el producto");
        }
    }
}
