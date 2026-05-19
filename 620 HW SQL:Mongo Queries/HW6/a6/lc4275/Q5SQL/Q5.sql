SELECT
  cid,
  CAST(SUM(distance * distance) AS DECIMAL(32,24)) AS sse,
  COUNT(*) AS size
FROM &AssignmentRelation&
GROUP BY cid
ORDER BY cid
