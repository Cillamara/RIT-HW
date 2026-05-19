WITH filtered_users AS (
  SELECT u.id,
    COUNT(DISTINCT r.id) AS review_count,
    COUNT(DISTINCT f.uid_two) AS friend_count,
    u.useful, u.funny, u.cool, u.fans,
    COUNT(DISTINCT ue.year) AS elite_count
  FROM user u
  LEFT JOIN review r ON u.id = r.uid
  LEFT JOIN friend f ON u.id = f.uid_one
  LEFT JOIN userelite ue ON u.id = ue.uid
  WHERE u.yelping_since BETWEEN '&StartDate&' AND '&EndDate&'
  GROUP BY u.id, u.useful, u.funny, u.cool, u.fans
),
maxvals AS (
  SELECT MAX(useful) AS max_useful, MAX(funny) AS max_funny,
         MAX(cool) AS max_cool, MAX(fans) AS max_fans
  FROM filtered_users
)
SELECT fu.id,
  CAST(CEIL(RANK() OVER (ORDER BY fu.review_count, fu.id) * &k& / COUNT(*) OVER ()) AS SIGNED) AS att_1,
  CAST(CEIL(RANK() OVER (ORDER BY fu.friend_count, fu.id) * &k& / COUNT(*) OVER ()) AS SIGNED) AS att_2,
  CAST(CASE WHEN mv.max_useful = 0 THEN 0 ELSE FLOOR(fu.useful * &k& / mv.max_useful) END AS SIGNED) AS att_3,
  CAST(CASE WHEN mv.max_funny = 0 THEN 0 ELSE FLOOR(fu.funny * &k& / mv.max_funny) END AS SIGNED) AS att_4,
  CAST(CASE WHEN mv.max_cool = 0 THEN 0 ELSE FLOOR(fu.cool * &k& / mv.max_cool) END AS SIGNED) AS att_5,
  CAST(CASE WHEN mv.max_fans = 0 THEN 0 ELSE FLOOR(fu.fans * &k& / mv.max_fans) END AS SIGNED) AS att_6,
  CAST(CEIL(RANK() OVER (ORDER BY fu.elite_count, fu.id) * &k& / COUNT(*) OVER ()) AS SIGNED) AS label
FROM filtered_users fu
CROSS JOIN maxvals mv
ORDER BY fu.id
