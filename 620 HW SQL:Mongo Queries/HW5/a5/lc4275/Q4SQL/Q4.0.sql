SELECT c.antecedent, c.consequent, COALESCE(t.cnt, 0) AS count
FROM (SELECT 1 AS antecedent, 1 AS consequent UNION ALL SELECT 1,0 UNION ALL SELECT 0,1 UNION ALL SELECT 0,0) c
LEFT JOIN (
  SELECT
    CASE WHEN &AttributeName1& = &AttributeValue1& THEN 1 ELSE 0 END AS antecedent,
    CASE WHEN label = &LabelValue& THEN 1 ELSE 0 END AS consequent,
    COUNT(*) AS cnt
  FROM &Relation&
  GROUP BY antecedent, consequent
) t ON c.antecedent = t.antecedent AND c.consequent = t.consequent
ORDER BY c.antecedent DESC, c.consequent DESC
