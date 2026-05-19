SELECT a.iid_1, a.iid_2, a.iid_3, b.iid_3 AS iid_4
FROM &LRelation& a, &LRelation& b
WHERE a.iid_1 = b.iid_1
AND a.iid_2 = b.iid_2
AND a.iid_3 < b.iid_3
ORDER BY a.iid_1, a.iid_2, a.iid_3, iid_4