WITH ranked AS (
  SELECT pid, cid, distance,
    ROW_NUMBER() OVER (PARTITION BY pid ORDER BY distance, cid) AS rn
  FROM &DistanceRelation&
)
SELECT pid, cid, distance
FROM ranked
WHERE rn = 1
ORDER BY pid
