package lk.ijse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.apache.commons.dbcp2.BasicDataSource;

public class DBConnection implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        basicDataSource.setUrl("jdbc:mysql://localhost:3306/javaee_pos_system ");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("mysql");
        basicDataSource.setInitialSize(50);
        basicDataSource.setMaxTotal(100);
        servletContext.setAttribute("datasource", basicDataSource);
    }
}
