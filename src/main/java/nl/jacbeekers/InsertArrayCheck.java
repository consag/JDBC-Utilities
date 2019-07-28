package nl.jacbeekers;

/*
create table ARRAY_INSERT_TEST
(recid number
,created timestamp(6)
,created_by varchar2(100 char)
,nr number
);
 */
import java.sql.*;
public class InsertArrayCheck {

    public static void main(String[] args) throws SQLException{
	// write your code here
        String url = args[0];
        String user = args[1];
        String passwd = args[2];

        arrayInsert(url, user, passwd);
    }

static void arrayInsert(String url, String username, String password) throws SQLException {
    String methodName = "arrayInsert";
    int recNr = 1;
    int totalRec = 0;
    int nrInserted;
    int totalInserted = 0;
    int commitSize = 100;
    int maxRows = 1000;
    int batchSize = 20;

    Connection connection = DriverManager.getConnection(url
            , username
            , password);

    connection.setAutoCommit(false); //commit transaction manually*
    String insertTableSQL =
            "INSERT INTO ARRAY_INSERT_TEST" + " (RECID, CREATED, CREATED_BY, NR)" + "  VALUES (?,?,?,?)";

    PreparedStatement preparedStatement = connection.prepareStatement(insertTableSQL);

    preparedStatement.clearBatch();

    for (int i = 1; i <= maxRows; i++) {

        java.sql.Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());
        preparedStatement.setDouble(1, recNr);
        preparedStatement.setTimestamp(2, timestamp);
        preparedStatement.setString(3, methodName);
        preparedStatement.setDouble(4, recNr);
        preparedStatement.addBatch();
        System.out.println("Rec# >" + recNr + "< added to batch.");

        recNr++;
        totalRec++;

        if (recNr > batchSize) {
            recNr = 1;
            nrInserted=0;
            int rc[] = preparedStatement.executeBatch();
            nrInserted = rc.length;
//            for ( int r=0 ; r < rc.length ; r++) {
//                nrInserted += r;
//            }
            System.out.println("Array inserted >" + nrInserted + "< records.");
            preparedStatement.clearBatch();
            totalInserted = totalInserted + nrInserted;

            if (totalInserted > commitSize) {
                totalInserted = 0;
                System.out.println("Reached commit size of >" + commitSize + "<. Commiting...");
                connection.commit();
                System.out.println("Commit succeeded.");
            }
        }

    }

    int rc[] = preparedStatement.executeBatch();
    nrInserted = rc.length;
    System.out.println("Last array inserted >" + nrInserted + "< records.");
    preparedStatement.clearBatch();
    connection.commit();
    System.out.println("Last commit succeeded.");
    connection.close();
    System.out.println("Connection closed.");
    System.out.println("Done.");

}
}
