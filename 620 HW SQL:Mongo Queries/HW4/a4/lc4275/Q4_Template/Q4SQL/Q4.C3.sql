SELECT c.iid_1, c.iid_2, c.iid_3
FROM &CRelation& c
WHERE EXISTS (
    SELECT 1 FROM &LRelation& l
    WHERE l.iid_1 = c.iid_2 AND l.iid_2 = c.iid_3
)
ORDER BY c.iid_1, c.iid_2, c.iid_3