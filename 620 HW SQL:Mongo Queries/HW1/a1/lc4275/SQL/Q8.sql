SELECT u.id, (SELECT COUNT(*) FROM friend f2 WHERE f2.uid_one = u.id) AS friend_count
FROM user u
WHERE u.fans > 50
  AND (SELECT COUNT(*) FROM friend f WHERE f.uid_one = u.id) > 5
  AND NOT EXISTS (
      SELECT 1
      FROM friend f
      WHERE f.uid_one = u.id
        AND (SELECT COUNT(*) FROM review r WHERE r.uid = f.uid_two AND r.stars = 5) > 3
  )
  AND NOT EXISTS (
      SELECT 1
      FROM review r
      JOIN business b ON r.bid = b.id
      JOIN zipcode z ON b.zc_id = z.id
      JOIN city c ON z.city_id = c.id
      WHERE r.uid = u.id AND c.name = 'Largo'
  )
ORDER BY friend_count DESC, u.id ASC;
