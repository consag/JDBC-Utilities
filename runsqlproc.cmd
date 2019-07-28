REM In D:\GitRepos\arrayinsertJava\target\classes>
REM Copy sqljdbc42.jar to target\classes
REM Then
set PATH=D:\Java\ZuluJDK1.8\bin;%PATH%
cd target/classes
java -cp .;sqljdbc42.jar nl.jacbeekers.ListSQLStoredProcedures jdbc:sqlserver://localhost:1433 ../../sqlserver.properties
cd ../..
