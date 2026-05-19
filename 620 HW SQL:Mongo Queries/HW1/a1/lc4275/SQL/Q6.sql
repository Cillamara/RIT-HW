SELECT u.id
FROM user u
JOIN review r ON u.id = r.uid
JOIN business b ON r.bid = b.id
JOIN zipcode z ON b.zc_id = z.id
JOIN city ci ON z.city_id = ci.id
JOIN state s ON ci.state_id = s.id
WHERE u.yelping_since >= '2021-01-01' 
  AND u.yelping_since <= '2021-05-31'
  AND s.name = 'AZ'
  AND u.id NOT IN (SELECT uid FROM userelite)
  AND u.id IN (
      SELECT uid_one 
      FROM friend 
      GROUP BY uid_one 
      HAVING COUNT(DISTINCT uid_two) >= 2
  )
GROUP BY u.id
HAVING COUNT(DISTINCT b.id) > 3
ORDER BY u.id ASC
LIMIT 5;
