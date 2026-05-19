SELECT a.iid_1, a.iid_2, b.iid_2 AS iid_3
FROM &LRelation& a, &LRelation& b
WHERE a.iid_1 = b.iid_1
AND a.iid_2 < b.iid_2
ORDER BY a.iid_1, a.iid_2, iid_3