SELECT b.id, CAST(AVG(r.stars) AS CHAR) AS rating
FROM business b
JOIN review r ON b.id = r.bid
WHERE b.name <> 'Garage Burger Bar & Grill'
  AND r.uid IN (
      SELECT r2.uid 
      FROM review r2 
      JOIN business b2 ON r2.bid = b2.id 
      WHERE b2.name = 'Garage Burger Bar & Grill'
  )
GROUP BY b.id
HAVING COUNT(DISTINCT r.uid) = (
    SELECT COUNT(DISTINCT r3.uid) 
    FROM review r3 
    JOIN business b3 ON r3.bid = b3.id 
    WHERE b3.name = 'Garage Burger Bar & Grill'
)
ORDER BY b.id ASC;
