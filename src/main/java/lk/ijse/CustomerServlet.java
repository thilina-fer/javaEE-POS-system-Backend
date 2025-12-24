package lk.ijse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/api/v1/customers")
public class CustomerServlet extends HttpServlet {
    BasicDataSource basicDataSource;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        basicDataSource = (BasicDataSource) servletContext.getAttribute("datasource");
    }

    //======================= Save Customers
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(req.getReader(), JsonObject.class);
        String name = jsonObject.get("name").getAsString();
        String address = jsonObject.get("address").getAsString();
        String contact = jsonObject.get("contact").getAsString();

        try {
            Connection connection = basicDataSource.getConnection();
            String query = "INSERT INTO customers (name, address, contact) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, contact);
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                resp.getWriter().println("Customer Saved Successfully");
            } else {
                resp.getWriter().println("Customer Save Failed");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //======================= Get All Customers

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Connection connection = basicDataSource.getConnection();
            String sql = "SELECT * FROM customers";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            JsonArray result = new JsonArray();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String contact = resultSet.getString("contact");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("name", name);
                jsonObject.addProperty("address", address);
                jsonObject.addProperty("contact", contact);
                result.add(jsonObject);
            }
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().println(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //======================= Update Customers
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    //======================= Delete Customers
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");

        // 1. මුලින්ම පරීක්ෂා කරන්න id එක null ද කියා
        if (id == null || id.isEmpty()) {
            System.out.println("Error: ID parameter is missing in the request!");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Customer ID is required");
            return;
        }

        try (Connection connection = basicDataSource.getConnection()) {
            String sql = "DELETE FROM customers WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // 2. දැන් parse කරන්න
            preparedStatement.setInt(1, Integer.parseInt(id));

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                resp.getWriter().write("Deleted Successfully");
            } else {
                resp.setStatus(404);
                resp.getWriter().write("Customer Not Found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
