WITH label_totals AS (
  SELECT label, CAST(SUM(count) AS DECIMAL(30,6)) AS lt FROM &AVCSet& GROUP BY label
),
grand AS (
  SELECT SUM(lt) AS gt FROM label_totals
)
SELECT CAST(1 - SUM(lt * lt) / (MAX(g.gt) * MAX(g.gt)) AS DECIMAL(30, 24)) AS gini
FROM label_totals, grand g
