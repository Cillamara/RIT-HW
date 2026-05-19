SELECT u.id, COUNT(r.id) AS number_of_reviews
FROM user u
JOIN review r ON u.id = r.uid
WHERE u.id IN (
    SELECT u2.id
    FROM user u2
    JOIN review r2 ON u2.id = r2.uid
    JOIN businesscategory bc ON r2.bid = bc.bid
    JOIN category c ON bc.cid = c.id
    WHERE c.name = 'Bookstores' AND r2.stars >= 4
    GROUP BY u2.id
    HAVING COUNT(DISTINCT r2.bid) >= 5
)
GROUP BY u.id
ORDER BY number_of_reviews DESC, u.id ASC
LIMIT 5;
