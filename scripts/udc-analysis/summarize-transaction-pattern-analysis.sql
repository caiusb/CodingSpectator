--This file is licensed under the University of Illinois/NCSA Open Source License. See LICENSE.TXT for details.

\p Importing transaction-descriptions.csv

DROP TABLE "PUBLIC"."TRANSACTION_DESCRIPTIONS" IF EXISTS;

CREATE TABLE "PUBLIC"."TRANSACTION_DESCRIPTIONS" (

  "TRANSACTION_IDENTIFIER" INT,

  "EXAMINER" VARCHAR(*{EXAMINER_MAX_LENGTH}),

  "IS_COMPOSITE_REFACTORING" VARCHAR(*{IS_COMPOSITE_REFACTORING_MAX_LENGTH}),

  "TRANSACTION_DESCRIPTION" VARCHAR(*{TRANSACTION_DESCRIPTION_MAX_LENGTH})

);

* *DSV_COL_SPLITTER =,

* *DSV_ROW_SPLITTER =\n

* *DSV_TARGET_TABLE ="PUBLIC"."TRANSACTION_DESCRIPTIONS"

\mq transaction-descriptions.csv

\p Computing the number of analyzed transactions of each transaction pattern

DROP TABLE "PUBLIC"."TRANSACTION_PATTERN_ANALYSIS_SUMMARIES" IF EXISTS;

CREATE TABLE "PUBLIC"."TRANSACTION_PATTERN_ANALYSIS_SUMMARIES" (

  "TRANSACTION_PATTERN_IDENTIFIER" INT,

  "ANALYZED_TRANSACTION_COUNT" INT,

  "TOTAL_TRANSACTION_COUNT" INT

);

INSERT INTO "PUBLIC"."TRANSACTION_PATTERN_ANALYSIS_SUMMARIES" (

  "TRANSACTION_PATTERN_IDENTIFIER",

  "ANALYZED_TRANSACTION_COUNT",

  "TOTAL_TRANSACTION_COUNT"

)

SELECT

"TRANSACTION_PATTERN_IDENTIFIER",

"ANALYZED_TRANSACTION_COUNT",

"TOTAL_TRANSACTION_COUNT"

FROM

(SELECT

"DT"."TRANSACTION_PATTERN_IDENTIFIER" AS "TRANSACTION_PATTERN_IDENTIFIER",

(SELECT COUNT(DISTINCT "TD"."TRANSACTION_IDENTIFIER") FROM
"PUBLIC"."TRANSACTION_DESCRIPTIONS" "TD" WHERE EXISTS (SELECT
"TRANSACTION_IDENTIFIER" FROM "PUBLIC"."DETAILED_TRANSACTIONS" "DT2" WHERE
"DT2"."TRANSACTION_PATTERN_IDENTIFIER" = "DT"."TRANSACTION_PATTERN_IDENTIFIER"
AND "DT2"."TRANSACTION_IDENTIFIER" = "TD"."TRANSACTION_IDENTIFIER")) AS
"ANALYZED_TRANSACTION_COUNT",

COUNT(DISTINCT "DT"."TRANSACTION_IDENTIFIER") AS "TOTAL_TRANSACTION_COUNT"

FROM "PUBLIC"."DETAILED_TRANSACTIONS" "DT"

GROUP BY "TRANSACTION_PATTERN_IDENTIFIER")

ORDER BY "ANALYZED_TRANSACTION_COUNT" DESC, "TRANSACTION_PATTERN_IDENTIFIER"
ASC;

* *DSV_COL_DELIM =,

* *DSV_ROW_DELIM =\n

* *DSV_TARGET_FILE =transaction-pattern-analysis-summary.csv

\x SELECT * FROM "PUBLIC"."TRANSACTION_PATTERN_ANALYSIS_SUMMARIES"
