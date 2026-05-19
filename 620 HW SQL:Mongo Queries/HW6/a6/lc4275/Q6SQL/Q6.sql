SELECT
  a.cid AS cid,
  CAST(AVG(p.scaled_dim_1) AS DECIMAL(32,24)) AS dim_1,
  CAST(AVG(p.scaled_dim_2) AS DECIMAL(32,24)) AS dim_2,
  CAST(AVG(p.scaled_dim_3) AS DECIMAL(32,24)) AS dim_3,
  CAST(AVG(p.scaled_dim_4) AS DECIMAL(32,24)) AS dim_4,
  CAST(AVG(p.scaled_dim_5) AS DECIMAL(32,24)) AS dim_5,
  CAST(AVG(p.scaled_dim_6) AS DECIMAL(32,24)) AS dim_6,
  CAST(AVG(p.scaled_dim_7) AS DECIMAL(32,24)) AS dim_7
FROM &AssignmentRelation& a
JOIN &PointRelation& p ON p.id = a.pid
GROUP BY a.cid
ORDER BY a.cid
