SELECT r1.id AS original, r2.id AS posterior
FROM review r1
JOIN user u ON r1.uid = u.id
JOIN review r2 ON r1.uid = r2.uid
WHERE u.yelping_since LIKE '2015-06%'
  AND r2.review_date = '2020-01-01'
  AND r1.review_date < r2.review_date
  AND r1.stars = r2.stars
  AND r1.funny = r2.funny
  AND r1.cool = r2.cool
  AND r1.useful = r2.useful
ORDER BY r1.id, r2.id;