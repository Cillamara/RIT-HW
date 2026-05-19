SELECT u.id, c.name AS category, COUNT(*) AS number_of_reviews
FROM user u
JOIN review r ON u.id = r.uid
JOIN businesscategory bc ON r.bid = bc.bid
JOIN category c ON bc.cid = c.id
WHERE u.id IN (
    SELECT f.uid_one 
    FROM friend f 
    GROUP BY f.uid_one 
    HAVING COUNT(*) > 250
)
AND u.id NOT IN (
    SELECT r2.uid
    FROM review r2
    JOIN businesscategory bc2 ON r2.bid = bc2.bid
    JOIN category c2 ON bc2.cid = c2.id
    WHERE c2.name = 'Mexican'
)
GROUP BY u.id, c.name
ORDER BY number_of_reviews DESC, c.name ASC, u.id ASC
LIMIT 15;