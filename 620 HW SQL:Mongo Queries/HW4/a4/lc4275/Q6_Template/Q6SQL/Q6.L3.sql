SELECT
    rules.a_iid_1,
    rules.a_iid_2,
    rules.c_iid,
    CAST(rules.lk_count / total.n AS DECIMAL(38,24)) AS support,
    CAST(rules.lk_count / lk2.count AS DECIMAL(38,24)) AS confidence,
    CAST((CAST(rules.lk_count AS DECIMAL(38,24)) * total.n) / (lk2.count * l1.count) AS DECIMAL(38,24)) AS lift
FROM (
    SELECT iid_2 AS a_iid_1, iid_3 AS a_iid_2, iid_1 AS c_iid, count AS lk_count
    FROM &LKRelation& WHERE iid_1 = &Iid_1Filter&
    UNION ALL
    SELECT iid_1 AS a_iid_1, iid_3 AS a_iid_2, iid_2 AS c_iid, count AS lk_count
    FROM &LKRelation& WHERE iid_1 = &Iid_1Filter&
    UNION ALL
    SELECT iid_1 AS a_iid_1, iid_2 AS a_iid_2, iid_3 AS c_iid, count AS lk_count
    FROM &LKRelation& WHERE iid_1 = &Iid_1Filter&
) rules
JOIN &LKMinusOneRelation& lk2 ON lk2.iid_1 = rules.a_iid_1 AND lk2.iid_2 = rules.a_iid_2
JOIN &LOneRelation& l1 ON l1.iid_1 = rules.c_iid
CROSS JOIN (SELECT COUNT(DISTINCT tid) AS n FROM &TRelation&) total
ORDER BY rules.a_iid_1, rules.a_iid_2, rules.c_iid