SELECT b.id,
  CAST(CEIL(RANK() OVER (ORDER BY rc.review_count, b.id) * &k& / COUNT(*) OVER ()) AS SIGNED) AS att_1,
  COALESCE(b.is_open, 2) AS att_2,
  COALESCE(b.restaurants_take_out, 2) AS att_3,
  COALESCE(b.restaurants_delivery, 2) AS att_4,
  COALESCE(b.drive_thru, 2) AS att_5,
  COALESCE(b.wheelchair_accessible, 2) AS att_6,
  COALESCE(b.happy_hour, 2) AS att_7,
  COALESCE(b.outdoor_seating, 2) AS att_8,
  COALESCE(b.restaurants_price_range, 0) AS label
FROM business b
JOIN businesscategory bc ON b.id = bc.bid
JOIN category c ON bc.cid = c.id
JOIN (SELECT bid, COUNT(*) AS review_count FROM review GROUP BY bid) rc ON b.id = rc.bid
WHERE c.name = '&Category&'
ORDER BY b.id
