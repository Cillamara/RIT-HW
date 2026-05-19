SELECT
  b.id AS id,
  CAST(b.latitude AS DECIMAL(32,24)) AS dim_1,
  CAST(b.longitude AS DECIMAL(32,24)) AS dim_2,
  CAST(COALESCE(b.restaurants_price_range, 0) AS DECIMAL(32,24)) AS dim_3,
  CAST(COALESCE(wd.h, 0) AS DECIMAL(32,24)) AS dim_4,
  CAST(COALESCE(we.h, 0) AS DECIMAL(32,24)) AS dim_5,
  CAST(cc.cnt AS DECIMAL(32,24)) AS dim_6,
  CAST(COALESCE(rv.cnt, 0) AS DECIMAL(32,24)) AS dim_7
FROM business b
JOIN businesscategory bc ON bc.bid = b.id
JOIN category c ON c.id = bc.cid AND c.name = '&Category&'
LEFT JOIN (
  SELECT bid, SUM(ABS(TIME_TO_SEC(TIMEDIFF(closing_time, open_time)))/3600) AS h
  FROM businesshour
  WHERE day IN (1,2,3,4,5)
  GROUP BY bid
) wd ON wd.bid = b.id
LEFT JOIN (
  SELECT bid, SUM(ABS(TIME_TO_SEC(TIMEDIFF(closing_time, open_time)))/3600) AS h
  FROM businesshour
  WHERE day IN (0,6)
  GROUP BY bid
) we ON we.bid = b.id
JOIN (
  SELECT bid, COUNT(*) AS cnt FROM businesscategory GROUP BY bid
) cc ON cc.bid = b.id
LEFT JOIN (
  SELECT bid, COUNT(*) AS cnt FROM review GROUP BY bid
) rv ON rv.bid = b.id
ORDER BY b.id
