WITH s AS (
  SELECT
    MIN(dim_1) AS min1, MAX(dim_1) AS max1,
    MIN(dim_2) AS min2, MAX(dim_2) AS max2,
    MIN(dim_3) AS min3, MAX(dim_3) AS max3,
    MIN(dim_4) AS min4, MAX(dim_4) AS max4,
    MIN(dim_5) AS min5, MAX(dim_5) AS max5,
    MIN(dim_6) AS min6, MAX(dim_6) AS max6,
    MIN(dim_7) AS min7, MAX(dim_7) AS max7
  FROM &Relation&
)
SELECT
  p.id AS id,
  CAST(CASE WHEN s.max1 = s.min1 THEN 0 ELSE (p.dim_1 - s.min1) / (s.max1 - s.min1) END AS DECIMAL(32,24)) AS scaled_dim_1,
  CAST(CASE WHEN s.max2 = s.min2 THEN 0 ELSE (p.dim_2 - s.min2) / (s.max2 - s.min2) END AS DECIMAL(32,24)) AS scaled_dim_2,
  CAST(CASE WHEN s.max3 = s.min3 THEN 0 ELSE (p.dim_3 - s.min3) / (s.max3 - s.min3) END AS DECIMAL(32,24)) AS scaled_dim_3,
  CAST(CASE WHEN s.max4 = s.min4 THEN 0 ELSE (p.dim_4 - s.min4) / (s.max4 - s.min4) END AS DECIMAL(32,24)) AS scaled_dim_4,
  CAST(CASE WHEN s.max5 = s.min5 THEN 0 ELSE (p.dim_5 - s.min5) / (s.max5 - s.min5) END AS DECIMAL(32,24)) AS scaled_dim_5,
  CAST(CASE WHEN s.max6 = s.min6 THEN 0 ELSE (p.dim_6 - s.min6) / (s.max6 - s.min6) END AS DECIMAL(32,24)) AS scaled_dim_6,
  CAST(CASE WHEN s.max7 = s.min7 THEN 0 ELSE (p.dim_7 - s.min7) / (s.max7 - s.min7) END AS DECIMAL(32,24)) AS scaled_dim_7
FROM &Relation& p
CROSS JOIN s
ORDER BY p.id
