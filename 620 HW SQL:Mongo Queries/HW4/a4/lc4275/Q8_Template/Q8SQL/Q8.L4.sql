SELECT
    a1.id AS eclass,
    a1.iid_1,
    a1.iid_2,
    a1.iid_3,
    a2.iid_3 AS iid_4,
    COUNT(*) AS count,
    CONCAT('[', GROUP_CONCAT(t.tid ORDER BY t.tid SEPARATOR ', '), ']') AS transactions
FROM &LRelation& a1
INNER JOIN &LRelation& a2 ON a1.iid_1 = a2.iid_1 AND a1.iid_2 = a2.iid_2 AND a1.iid_3 < a2.iid_3,
JSON_TABLE(a1.transactions, '$[*]' COLUMNS (tid INT PATH '$')) t
WHERE JSON_CONTAINS(a2.transactions, CAST(t.tid AS JSON))
GROUP BY a1.id, a1.iid_1, a1.iid_2, a1.iid_3, a2.iid_3
HAVING COUNT(*) >= &MinSup&
ORDER BY a1.iid_1, a1.iid_2, a1.iid_3, a2.iid_3