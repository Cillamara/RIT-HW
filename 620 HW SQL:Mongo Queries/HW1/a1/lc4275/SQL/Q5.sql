SELECT u.id, COUNT(DISTINCT r.bid) AS number_of_reviewed_businesses
FROM user u
JOIN review r ON u.id = r.uid
JOIN businesscategory bc ON r.bid = bc.bid
JOIN category c ON bc.cid = c.id
WHERE (u.name LIKE '%ch%' OR u.name LIKE '%th%')
  AND c.name IN ('Korean', 'Seafood')
  AND u.id NOT IN (SELECT uid_one FROM friend)
  AND u.id NOT IN (
      SELECT r2.uid 
      FROM review r2 
      JOIN businesscategory bc2 ON r2.bid = bc2.bid 
      JOIN category c2 ON bc2.cid = c2.id 
      WHERE c2.name = 'Mexican' AND r2.review_date > '2020-12-31'
  )
GROUP BY u.id
HAVING number_of_reviewed_businesses > 2
ORDER BY number_of_reviewed_businesses DESC, u.id ASC
LIMIT 5;