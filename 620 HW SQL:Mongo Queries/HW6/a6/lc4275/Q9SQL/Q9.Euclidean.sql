WITH ci AS (
  SELECT p.* FROM &PointRelation& p JOIN &AssignmentRelation& a ON a.pid = p.id WHERE a.cid = '&ClusterI&'
),
cj AS (
  SELECT p.* FROM &PointRelation& p JOIN &AssignmentRelation& a ON a.pid = p.id WHERE a.cid = '&ClusterJ&'
),
pairs AS (
  SELECT
    (ci.scaled_dim_1 - cj.scaled_dim_1) * (ci.scaled_dim_1 - cj.scaled_dim_1) +
    (ci.scaled_dim_2 - cj.scaled_dim_2) * (ci.scaled_dim_2 - cj.scaled_dim_2) +
    (ci.scaled_dim_3 - cj.scaled_dim_3) * (ci.scaled_dim_3 - cj.scaled_dim_3) +
    (ci.scaled_dim_4 - cj.scaled_dim_4) * (ci.scaled_dim_4 - cj.scaled_dim_4) +
    (ci.scaled_dim_5 - cj.scaled_dim_5) * (ci.scaled_dim_5 - cj.scaled_dim_5) +
    (ci.scaled_dim_6 - cj.scaled_dim_6) * (ci.scaled_dim_6 - cj.scaled_dim_6) +
    (ci.scaled_dim_7 - cj.scaled_dim_7) * (ci.scaled_dim_7 - cj.scaled_dim_7) AS d
  FROM ci CROSS JOIN cj
)
SELECT
  CAST(MIN(d) AS DECIMAL(32,24)) AS min_distance,
  CAST(MAX(d) AS DECIMAL(32,24)) AS max_distance,
  CAST(AVG(d) AS DECIMAL(32,24)) AS mean_distance
FROM pairs
