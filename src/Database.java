import java.sql.*;

public class Database {
    // Getters and setters weren't added because they don't need to access the database (security reasons) and
    // there is no need to change any of the database variables
    private final String dbUrl, username, password;
    private Connection connection;

    Database(String dbUrl, String username, String password) {
        this.dbUrl = dbUrl;
        this.username = username;
        this.password = password;
    }

    public void setConnection() throws SQLException {
        this.connection = DriverManager.getConnection(dbUrl, username, password);
    }

    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }

        // prevents user from trying to close a connection that hasn't been set up yet
        else {
            System.out.println("You haven't set a Connection");
        }
    }

    public ResultSet select_statement(String query) throws SQLException {
        ResultSet resultSet = null;

        if (connection != null) {
            PreparedStatement myStatement = connection.prepareStatement(query);
            resultSet = myStatement.executeQuery();
        }

        // same as line 24
        else {
            System.out.println("You haven't set a Connection");
        }

        return resultSet;
    }

    public void modify_table(String query) throws SQLException {
        if (connection != null) {
            Statement myStatment = connection.createStatement();
            myStatment.executeUpdate(query);
        }

        // same as line 24
        else {
            System.out.println("You haven't set a Connection");
        }
    }

    // made it a generic method in case it is later decided to use more stored procedures
    public <T> void stored_procedure(String procedure_call, T[] array_of_values) throws SQLException {
        if (connection != null) {
            // notes said CallableStatement, but PreparedStatment made it work... (and makes more sense)
            PreparedStatement myStatement = connection.prepareStatement(procedure_call);

            for (int i = 0; i < array_of_values.length; i++) {
                // checks the wrapper type and calls the set method accordingly
                if (array_of_values[i].getClass().getSimpleName().equals("String")) {
                    myStatement.setString(i + 1, (String) array_of_values[i]);
                }
            }
            myStatement.execute();

        } else {
            System.out.println("You haven't set a Connection");
        }
    }
}
