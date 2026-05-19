WITH att_totals AS (
  SELECT attribute, CAST(SUM(count) AS DECIMAL(38,10)) AS at_total FROM &AVCSet& GROUP BY attribute
),
grand AS (
  SELECT SUM(at_total) AS gt FROM att_totals
),
gini_per_att AS (
  SELECT a.attribute, a.at_total,
    CAST(1 AS DECIMAL(38,10)) - SUM(CAST(av.count AS DECIMAL(38,10)) * CAST(av.count AS DECIMAL(38,10))) / (a.at_total * a.at_total) AS gini_v
  FROM att_totals a
  JOIN &AVCSet& av ON a.attribute = av.attribute
  GROUP BY a.attribute, a.at_total
)
SELECT CAST(SUM(gpa.at_total / g.gt * gpa.gini_v) AS DECIMAL(30,24)) AS gini_a
FROM gini_per_att gpa, grand g
