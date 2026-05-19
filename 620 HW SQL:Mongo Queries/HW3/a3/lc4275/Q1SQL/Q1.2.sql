SELECT COUNT(*) AS count, c.name AS category, YEAR(r.review_date) AS year
FROM review r
JOIN businesscategory bc ON r.bid = bc.bid
JOIN category c ON bc.cid = c.id
WHERE r.review_date BETWEEN '&init_date&' AND '&end_date&'
GROUP BY c.name, YEAR(r.review_date)
ORDER BY year ASC, count DESC
