CREATE PROCEDURE testschema.Testprocedure1
	@param1 int = 0,
	@param2 int,
	@nrRecords int OUTPUT
AS
	SELECT @NrRecords = COUNT(*) FROM testschema.testTable
RETURN
