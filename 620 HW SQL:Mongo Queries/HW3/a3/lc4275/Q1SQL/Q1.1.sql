SELECT COUNT(*) AS count, c.name AS category
FROM review r
JOIN businesscategory bc ON r.bid = bc.bid
JOIN category c ON bc.cid = c.id
WHERE r.review_date BETWEEN '&init_date&' AND '&end_date&'
GROUP BY c.name
ORDER BY count DESC
