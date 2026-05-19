WITH self AS (
  SELECT p.id, p.scaled_dim_1, p.scaled_dim_2, p.scaled_dim_3, p.scaled_dim_4, p.scaled_dim_5, p.scaled_dim_6, p.scaled_dim_7, a.cid AS my_cid
  FROM &PointRelation& p JOIN &AssignmentRelation& a ON a.pid = p.id
  WHERE p.id = &PointId&
)
SELECT
  CAST(AVG(
    ABS(s.scaled_dim_1 - p.scaled_dim_1) +
    ABS(s.scaled_dim_2 - p.scaled_dim_2) +
    ABS(s.scaled_dim_3 - p.scaled_dim_3) +
    ABS(s.scaled_dim_4 - p.scaled_dim_4) +
    ABS(s.scaled_dim_5 - p.scaled_dim_5) +
    ABS(s.scaled_dim_6 - p.scaled_dim_6) +
    ABS(s.scaled_dim_7 - p.scaled_dim_7)
  ) AS DECIMAL(32,24)) AS distance
FROM self s
JOIN &AssignmentRelation& a2 ON a2.cid = s.my_cid
JOIN &PointRelation& p ON p.id = a2.pid
