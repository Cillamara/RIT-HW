SELECT c.iid_1, c.iid_2, c.iid_3, COUNT(*) AS count
FROM &CRelation& c
JOIN &TRelation& t1 ON t1.iid = c.iid_1
JOIN &TRelation& t2 ON t2.tid = t1.tid AND t2.iid = c.iid_2
JOIN &TRelation& t3 ON t3.tid = t1.tid AND t3.iid = c.iid_3
GROUP BY c.iid_1, c.iid_2, c.iid_3
HAVING COUNT(*) >= &MinSup&
ORDER BY c.iid_1, c.iid_2, c.iid_3