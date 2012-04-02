--This file is licensed under the University of Illinois/NCSA Open Source License. See LICENSE.TXT for details.

\p Importing detailed-transactions.csv

DROP TABLE "PUBLIC"."DETAILED_TRANSACTIONS" IF EXISTS;

CREATE TABLE "PUBLIC"."DETAILED_TRANSACTIONS" (

  "TRANSACTION_IDENTIFIER" INT,

  "TRANSACTION_PATTERN_IDENTIFIER" INT,

  "USERNAME" VARCHAR(*{USERNAME_MAX_LENGTH}),

  "WORKSPACE_ID" VARCHAR(*{WORKSPACE_ID_MAX_LENGTH}),

  "CODINGSPECTATOR_VERSION" VARCHAR(*{CODINGSPECTATOR_VERSION_MAX_LENGTH}),

  "CODINGTRACKER_TIMESTAMP" BIGINT,

  "CODINGSPECTATOR_TIMESTAMP" BIGINT,

  "REFACTORING_ID" VARCHAR(*{REFACTORING_ID_MAX_LENGTH})

);

* *DSV_COL_SPLITTER =,

* *DSV_ROW_SPLITTER =\n

* *DSV_TARGET_TABLE ="PUBLIC"."DETAILED_TRANSACTIONS"

\m detailed-transactions.csv

\p Importing transaction-patterns.csv

DROP TABLE "PUBLIC"."TRANSACTION_PATTERNS" IF EXISTS;

CREATE TABLE "PUBLIC"."TRANSACTION_PATTERNS" (

  "TRANSACTION_PATTERN_IDENTIFIER" INT,

  "REFACTORING_ID" VARCHAR(*{REFACTORING_ID_MAX_LENGTH})

);

* *DSV_COL_SPLITTER =,

* *DSV_ROW_SPLITTER =\n

* *DSV_TARGET_TABLE ="PUBLIC"."TRANSACTION_PATTERNS"

\m transaction-patterns.csv

