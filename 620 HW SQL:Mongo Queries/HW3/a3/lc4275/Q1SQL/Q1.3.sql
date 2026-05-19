WITH ranked AS (
    SELECT r.stars,
           ROW_NUMBER() OVER (ORDER BY r.stars) AS rn,
           COUNT(*) OVER ()                      AS total
    FROM review r
    JOIN businesscategory bc ON r.bid = bc.bid
    JOIN category c ON bc.cid = c.id
    WHERE c.name = '&category&'
),
pos AS (
    SELECT FLOOR(&quantile& * MAX(total)) AS k
    FROM ranked
)
SELECT stars
FROM ranked
WHERE rn = (SELECT k FROM pos)
   OR rn = (SELECT k FROM pos) + 1
ORDER BY rn
