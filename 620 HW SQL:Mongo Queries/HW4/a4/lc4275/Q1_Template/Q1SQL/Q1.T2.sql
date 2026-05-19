SELECT DISTINCT r.uid AS tid, r.bid AS iid
FROM yelp_A3Q3.review r
JOIN yelp_A3Q3.user u ON r.uid = u.id
WHERE u.yelping_since BETWEEN '&YelpingStart&' AND '&YelpingEnd&'
ORDER BY tid, iid
