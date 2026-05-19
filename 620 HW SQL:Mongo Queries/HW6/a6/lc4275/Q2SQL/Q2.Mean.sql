WITH s AS (
  SELECT
    CAST(AVG(dim_1) AS DECIMAL(32,24)) AS m1, MIN(dim_1) AS mn1, MAX(dim_1) AS mx1,
    CAST(AVG(dim_2) AS DECIMAL(32,24)) AS m2, MIN(dim_2) AS mn2, MAX(dim_2) AS mx2,
    CAST(AVG(dim_3) AS DECIMAL(32,24)) AS m3, MIN(dim_3) AS mn3, MAX(dim_3) AS mx3,
    CAST(AVG(dim_4) AS DECIMAL(32,24)) AS m4, MIN(dim_4) AS mn4, MAX(dim_4) AS mx4,
    CAST(AVG(dim_5) AS DECIMAL(32,24)) AS m5, MIN(dim_5) AS mn5, MAX(dim_5) AS mx5,
    CAST(AVG(dim_6) AS DECIMAL(32,24)) AS m6, MIN(dim_6) AS mn6, MAX(dim_6) AS mx6,
    CAST(AVG(dim_7) AS DECIMAL(32,24)) AS m7, MIN(dim_7) AS mn7, MAX(dim_7) AS mx7
  FROM &Relation&
)
SELECT
  p.id AS id,
  CAST(CASE WHEN s.mx1 = s.mn1 THEN 0 ELSE (p.dim_1 - s.m1) / (s.mx1 - s.mn1) END AS DECIMAL(32,24)) AS scaled_dim_1,
  CAST(CASE WHEN s.mx2 = s.mn2 THEN 0 ELSE (p.dim_2 - s.m2) / (s.mx2 - s.mn2) END AS DECIMAL(32,24)) AS scaled_dim_2,
  CAST(CASE WHEN s.mx3 = s.mn3 THEN 0 ELSE (p.dim_3 - s.m3) / (s.mx3 - s.mn3) END AS DECIMAL(32,24)) AS scaled_dim_3,
  CAST(CASE WHEN s.mx4 = s.mn4 THEN 0 ELSE (p.dim_4 - s.m4) / (s.mx4 - s.mn4) END AS DECIMAL(32,24)) AS scaled_dim_4,
  CAST(CASE WHEN s.mx5 = s.mn5 THEN 0 ELSE (p.dim_5 - s.m5) / (s.mx5 - s.mn5) END AS DECIMAL(32,24)) AS scaled_dim_5,
  CAST(CASE WHEN s.mx6 = s.mn6 THEN 0 ELSE (p.dim_6 - s.m6) / (s.mx6 - s.mn6) END AS DECIMAL(32,24)) AS scaled_dim_6,
  CAST(CASE WHEN s.mx7 = s.mn7 THEN 0 ELSE (p.dim_7 - s.m7) / (s.mx7 - s.mn7) END AS DECIMAL(32,24)) AS scaled_dim_7
FROM &Relation& p
CROSS JOIN s
ORDER BY p.id
