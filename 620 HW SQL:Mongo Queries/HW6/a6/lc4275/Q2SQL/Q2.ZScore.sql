WITH a AS (
  SELECT
    AVG(dim_1) AS m1, AVG(dim_1*dim_1) - AVG(dim_1)*AVG(dim_1) AS v1,
    AVG(dim_2) AS m2, AVG(dim_2*dim_2) - AVG(dim_2)*AVG(dim_2) AS v2,
    AVG(dim_3) AS m3, AVG(dim_3*dim_3) - AVG(dim_3)*AVG(dim_3) AS v3,
    AVG(dim_4) AS m4, AVG(dim_4*dim_4) - AVG(dim_4)*AVG(dim_4) AS v4,
    AVG(dim_5) AS m5, AVG(dim_5*dim_5) - AVG(dim_5)*AVG(dim_5) AS v5,
    AVG(dim_6) AS m6, AVG(dim_6*dim_6) - AVG(dim_6)*AVG(dim_6) AS v6
  FROM &Relation&
)
SELECT
  p.id AS id,
  CAST(CASE WHEN a.v1 = 0 OR a.v1 IS NULL THEN 0 ELSE (p.dim_1 - a.m1) * (p.dim_1 - a.m1) / a.v1 END AS DECIMAL(32,24)) AS scaled_dim_1,
  CAST(CASE WHEN a.v2 = 0 OR a.v2 IS NULL THEN 0 ELSE (p.dim_2 - a.m2) * (p.dim_2 - a.m2) / a.v2 END AS DECIMAL(32,24)) AS scaled_dim_2,
  CAST(CASE WHEN a.v3 = 0 OR a.v3 IS NULL THEN 0 ELSE (p.dim_3 - a.m3) * (p.dim_3 - a.m3) / a.v3 END AS DECIMAL(32,24)) AS scaled_dim_3,
  CAST(CASE WHEN a.v4 = 0 OR a.v4 IS NULL THEN 0 ELSE (p.dim_4 - a.m4) * (p.dim_4 - a.m4) / a.v4 END AS DECIMAL(32,24)) AS scaled_dim_4,
  CAST(CASE WHEN a.v5 = 0 OR a.v5 IS NULL THEN 0 ELSE (p.dim_5 - a.m5) * (p.dim_5 - a.m5) / a.v5 END AS DECIMAL(32,24)) AS scaled_dim_5,
  CAST(CASE WHEN a.v6 = 0 OR a.v6 IS NULL THEN 0 ELSE (p.dim_6 - a.m6) * (p.dim_6 - a.m6) / a.v6 END AS DECIMAL(32,24)) AS scaled_dim_6
FROM &Relation& p
CROSS JOIN a
ORDER BY p.id
