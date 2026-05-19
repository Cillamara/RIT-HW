WITH ant AS (
  SELECT CAST(count AS DECIMAL(30,6)) AS cnt FROM &RuleCoverage& WHERE antecedent = 1
),
totals AS (
  SELECT SUM(cnt) AS total, SUM(cnt * cnt) AS sum_sq FROM ant
)
SELECT CAST(1 - sum_sq / (total * total) AS DECIMAL(30, 24)) AS gini
FROM totals
WHERE total > 0
