SELECT iid AS iid_1, COUNT(tid) AS count
FROM &TRelation&
GROUP BY iid
HAVING COUNT(tid) >= &MinSup&
ORDER BY iid_1
