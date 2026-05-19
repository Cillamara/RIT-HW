SELECT
  p.id AS pid,
  c.id AS cid,
  CAST(
    (p.scaled_dim_1 - c.scaled_dim_1) * (p.scaled_dim_1 - c.scaled_dim_1) +
    (p.scaled_dim_2 - c.scaled_dim_2) * (p.scaled_dim_2 - c.scaled_dim_2) +
    (p.scaled_dim_3 - c.scaled_dim_3) * (p.scaled_dim_3 - c.scaled_dim_3) +
    (p.scaled_dim_4 - c.scaled_dim_4) * (p.scaled_dim_4 - c.scaled_dim_4) +
    (p.scaled_dim_5 - c.scaled_dim_5) * (p.scaled_dim_5 - c.scaled_dim_5) +
    (p.scaled_dim_6 - c.scaled_dim_6) * (p.scaled_dim_6 - c.scaled_dim_6) +
    (p.scaled_dim_7 - c.scaled_dim_7) * (p.scaled_dim_7 - c.scaled_dim_7)
    AS DECIMAL(32,24)
  ) AS distance
FROM &PointRelation& p
CROSS JOIN &CentroidRelation& c
ORDER BY p.id, c.id
