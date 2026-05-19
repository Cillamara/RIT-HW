SELECT
  u.id AS id,
  CAST(u.useful AS DECIMAL(32,24)) AS dim_1,
  CAST(u.funny AS DECIMAL(32,24)) AS dim_2,
  CAST(u.cool AS DECIMAL(32,24)) AS dim_3,
  CAST(COALESCE(e.cnt, 0) AS DECIMAL(32,24)) AS dim_4,
  CAST(COALESCE(f.cnt, 0) AS DECIMAL(32,24)) AS dim_5,
  CAST(COALESCE(r.cnt, 0) AS DECIMAL(32,24)) AS dim_6,
  CAST(COALESCE(r.avg_stars, 0) AS DECIMAL(32,24)) AS dim_7
FROM user u
LEFT JOIN (
  SELECT uid, COUNT(*) AS cnt FROM userelite GROUP BY uid
) e ON e.uid = u.id
LEFT JOIN (
  SELECT uid_one AS uid, COUNT(*) AS cnt FROM friend GROUP BY uid_one
) f ON f.uid = u.id
LEFT JOIN (
  SELECT uid, COUNT(*) AS cnt, CAST(SUM(stars) AS DECIMAL(32,24)) / COUNT(*) AS avg_stars FROM review GROUP BY uid
) r ON r.uid = u.id
WHERE u.yelping_since BETWEEN '&StartDate&' AND '&EndDate&'
ORDER BY u.id
