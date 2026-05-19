SELECT u.id
FROM user u
WHERE (SELECT COUNT(*) FROM friend f WHERE f.uid_one = u.id) >= 75
  AND EXISTS (
      SELECT 1 FROM review r 
      WHERE r.uid = u.id AND r.funny > 3 AND r.cool > 3
  )
  AND u.yelping_since > ALL (
      SELECT u_friend.yelping_since
      FROM friend f
      JOIN user u_friend ON f.uid_two = u_friend.id
      WHERE f.uid_one = u.id
  )
ORDER BY u.id ASC;