package lk.ijse;

import com.google.gson.Gson;
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(req.getReader(), JsonObject.class);
        String id = jsonObject.get("id").getAsString();
        String name = jsonObject.get("name").getAsString();
        String address = jsonObject.get("address").getAsString();
        String contact = jsonObject.get("contact").getAsString();

        try {
            Connection connection = basicDataSource.getConnection();
            String query = "INSERT INTO customers (id, name, address, contact) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, contact);
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
}
