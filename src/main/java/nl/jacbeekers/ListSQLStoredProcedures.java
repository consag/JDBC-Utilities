package nl.jacbeekers;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ListSQLStoredProcedures {

    static Logger logger = Logger.getLogger(getClassname());

    public static void main(String[] args) {
        initLogger();

        if(args.length < 2) {
            outError("Usage: " + getClassname() + " databaseURL databasePropertiesFile");
        } else {
            String databaseUrl = args[0];
            String databasePropertiesFile = args[1];
            Properties databaseProperties = new Properties();
            try {
                databaseProperties.load(new FileInputStream(databasePropertiesFile));
                listProcedures(databaseUrl, databaseProperties);
            } catch (FileNotFoundException e) {
                outConfig(e.toString());
                listProcedures(databaseUrl);
            } catch (IOException e) {
                outError(e.toString());
                listProcedures(databaseUrl);
            }
        }
    }

    private static void initLogger() {
        String procName ="initLogger";
        String logpropertiesFile = getClassname() + ".properties";
        logger.setLevel(Level.ALL);
        outConfig("Trying to load logger properties file >" + logpropertiesFile +"<...");
        try {
            InputStream inputStream = ListSQLStoredProcedures.class.getResourceAsStream(logpropertiesFile);
            if(inputStream == null) {
                outInfo("That did not work.");
            } else {
                LogManager.getLogManager().readConfiguration(inputStream);
            }
        } catch(IOException e) {
            outError(procName +": " + e.toString());
        }

    }
    private static List<String> getStoredProcedures(Connection connection) {
        String procName="getStoredProcedures";
        List<String> storedProceduresList = new ArrayList<String>();
        String catalog="";
        String schemaPattern="%";
        String procedureNamePattern="%";

        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            outConfig("Getting procedure metadata for catalog >" + catalog + "<.");
            outConfig("schemaPattern is >" + schemaPattern +"<.");
            outConfig("procedurePattern is >" + procedureNamePattern +"<.");
            ResultSet resultSet = databaseMetaData.getProcedures(catalog, schemaPattern, procedureNamePattern);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            outInfo("Catalog column 0: " + resultSetMetaData.getCatalogName(0));
            outInfo("Schema column 0: " + resultSetMetaData.getSchemaName(0));
            outInfo("Table name column 0: " + resultSetMetaData.getTableName(0));
            while(resultSet.next()) {
                if(resultSet.isFirst()) {
                    for(int i=0; i < resultSetMetaData.getColumnCount(); i++) {
                        outInfo(">" + i + "< Column name >" + resultSetMetaData.getColumnName(i)
                            +"< is of type >" + resultSetMetaData.getColumnType(i) +"<, which is >"
                            + JDBCType.valueOf(resultSetMetaData.getColumnType(i)).getName()+"<.");
                    }
                }

            }


        } catch (SQLException e) {
            outError( procName +": " + e.toString());
        }


        return storedProceduresList;
    }

    public static void listProcedures(String databaseUrl, Properties databaseProperties) {
        String procName = "listProcedures databaseUrl databaseProperties";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(databaseUrl, databaseProperties);
            List<String> storedProceduresList = getStoredProcedures(connection);
            connection.close();
        } catch (SQLException e) {
            outError(procName +": " + e.toString());
        }

    }

    public static void listProcedures(String databaseUrl) {
        String procName = "listProcedures databaseUrl";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(databaseUrl);
            List<String> storedProceduresList = getStoredProcedures(connection);
            connection.close();
        } catch (SQLException e) {
            outError(procName +": " + e.toString());
        }

    }


    private static void outConfig(String config) { logger.info(getSetLogLine(config)); }
    private static void outInfo(String info) { logger.info(getSetLogLine(info)); }
    private static void outError(String error) { logger.severe(getSetLogLine(error)); }

    private static String getClassname() { return "ListSQLStoredProcedures"; }
    private static String getSetLogLine(String message) {
        return message;
    }

}
