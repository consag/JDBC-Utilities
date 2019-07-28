USE [testdb]
GO

/****** Object: SqlProcedure [testschema].[Testprocedure1] Script Date: 29-7-2019 00:06:51 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE testschema.Testprocedure3
	
AS
	SELECT COUNT(*) FROM testschema.testTable
RETURN
