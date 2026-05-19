SELECT
  i.cid AS cid_i,
  j.cid AS cid_j,
  COUNT(*) AS count
FROM &AssignmentRelationI& i
JOIN &AssignmentRelationJ& j ON i.pid = j.pid
GROUP BY i.cid, j.cid
ORDER BY i.cid, j.cid
