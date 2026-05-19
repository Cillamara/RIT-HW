SELECT b.id, CAST(v.total_useful AS CHAR) AS total_useful_votes
FROM business b
JOIN businesscategory bc ON b.id = bc.bid
JOIN category c ON bc.cid = c.id
JOIN (
    SELECT bid, SUM(useful) as total_useful
    FROM review
    GROUP BY bid
) v ON b.id = v.bid
WHERE c.name = 'Korean'
  AND NOT EXISTS (
      SELECT 1
      FROM business target_b
      JOIN businesshour target_bh ON target_b.id = target_bh.bid
      WHERE target_b.name = 'Bayou Daiquiris'
        AND target_bh.day BETWEEN 1 AND 5
        AND NOT EXISTS (
            SELECT 1
            FROM businesshour bh
            WHERE bh.bid = b.id
              AND bh.day = target_bh.day
              AND bh.open_time >= target_bh.open_time
              AND bh.closing_time >= target_bh.closing_time
        )
  )
GROUP BY b.id, v.total_useful
ORDER BY v.total_useful DESC, b.id ASC;
