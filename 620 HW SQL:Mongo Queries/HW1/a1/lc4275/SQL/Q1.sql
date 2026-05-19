SELECT b.id, COUNT(DISTINCT bc.cid) AS number_of_categories
FROM business b
JOIN businesscategory bc ON b.id = bc.bid
WHERE b.is_open = 1
GROUP BY b.id
HAVING COUNT(DISTINCT bc.cid) > 5
   AND (SELECT COUNT(*) FROM review r WHERE r.bid = b.id) > 250
   AND b.id NOT IN (
       SELECT bh.bid 
       FROM businesshour bh 
       WHERE bh.day IN (0, 6)
   )
ORDER BY number_of_categories DESC, b.id ASC;