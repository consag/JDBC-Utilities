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
    static String currentProcName ="";

    public static void main(String[] args) {
        setProcName("main");
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
        setProcName("initLogger");
        String logpropertiesFile = getClassname() + ".properties";
        logger.setLevel(Level.FINEST);
        outConfig("Trying to load logger properties file >" + logpropertiesFile +"<...");
        try {
            InputStream inputStream = ListSQLStoredProcedures.class.getResourceAsStream(logpropertiesFile);
            if(inputStream == null) {
                outInfo("That did not work.");
            } else {
                LogManager.getLogManager().readConfiguration(inputStream);
            }
        } catch(IOException e) {
            outError(e.toString());
        }

    }
    private static List<String> getStoredProcedures(Connection connection) {
        setProcName("getStoredProcedures");
        List<String> storedProceduresList = new ArrayList<String>();
        String catalog="testdb";
        String schemaPattern="testschema";
        String procedureNamePattern="%";

        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            outConfig("Getting procedure metadata for catalog >" + catalog + "<.");
            outConfig("schemaPattern is >" + schemaPattern +"<.");
            outConfig("procedurePattern is >" + procedureNamePattern +"<.");
            outDebug("Getting procedures...");
            ResultSet resultSet = null;
            resultSet = databaseMetaData.getProcedures(catalog, schemaPattern, procedureNamePattern);
            outDebug("getting metadata...");
            final ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            StringBuilder columnNames = new StringBuilder();
            for (int column = 0; column < columnCount; column++ ) {
                columnNames.append(metaData.getColumnName(column + 1).toUpperCase()).append(";");

            }
            outInfo(columnNames.toString());

            outDebug("Processing result set...");
            int nrRecs =0;
            while(resultSet.next()) {
                nrRecs++;
                outDebug("Record number is >" + nrRecs +"<.");
//                final Object[] row = new Object[columnCount];
                for(int column = 0; column < columnCount; column++) {
                    int colType = resultSet.getType();
                    //String colTypeName = JDBCType.valueOf(resultSet.getType()).getName();
                    Object colValue = resultSet.getObject(column +1);
                    if(colValue == null) {
                        outInfo("Column data type is " + colType + "< and its value is >null<.");
                    } else {
                        outInfo("Column data type is >" + colType + "< and its value is >" + colValue.toString() +"<.");
                    }
                }
                }
            outDebug("Processing result set completed.");
            resultSet.close();

        } catch (SQLException e) {
            outError(e.toString());
        }


        return storedProceduresList;
    }

    public static void listProcedures(String databaseUrl, Properties databaseProperties) {
        setProcName("listProcedures databaseUrl databaseProperties");
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(databaseUrl, databaseProperties);
            List<String> storedProceduresList = getStoredProcedures(connection);
            connection.close();
        } catch (SQLException e) {
            outError(e.toString());
        }

    }

    public static void listProcedures(String databaseUrl) {
        setProcName("listProcedures databaseUrl");
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(databaseUrl);
            List<String> storedProceduresList = getStoredProcedures(connection);
            connection.close();
        } catch (SQLException e) {
            outError(e.toString());
        }

    }


    private static void outConfig(String config) { logger.info(getSetLogLine(config)); }
    private static void outInfo(String info) { logger.info(getSetLogLine(info)); }
    private static void outError(String error) { logger.severe(getSetLogLine(error)); }
    private static void outDebug(String debuginfo) { logger.info(debuginfo); }

    private static String getClassname() { return "ListSQLStoredProcedures"; }
    private static String getSetLogLine(String message) {
        return getProcName() + ": " + message;
    }
    private static void setProcName(String procName) { currentProcName=procName; }
    private static String getProcName() { return currentProcName; }

}
