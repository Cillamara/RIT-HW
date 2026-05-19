SELECT DISTINCT r.bid AS tid, r.uid AS iid
FROM yelp_A3Q3.review r
JOIN yelp_A3Q3.businesscategory bc ON r.bid = bc.bid
JOIN yelp_A3Q3.category c ON bc.cid = c.id
WHERE c.name = '&Category&'
ORDER BY tid, iid
