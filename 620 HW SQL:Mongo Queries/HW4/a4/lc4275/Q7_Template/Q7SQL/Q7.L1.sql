SELECT
    iid AS iid_1,
    COUNT(*) AS count,
    CONCAT('[', GROUP_CONCAT(tid ORDER BY tid SEPARATOR ', '), ']') AS transactions,
    0 AS eclass
FROM &TRelation&
GROUP BY iid
HAVING COUNT(*) >= &MinSup&
ORDER BY iid