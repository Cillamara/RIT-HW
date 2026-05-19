SELECT b.restaurants_price_range AS mode
FROM business b
JOIN businesscategory bc ON b.id = bc.bid
JOIN category c ON bc.cid = c.id
WHERE c.name = '&category&'
  AND b.restaurants_price_range IS NOT NULL
GROUP BY b.restaurants_price_range
ORDER BY COUNT(*) DESC
LIMIT 1
