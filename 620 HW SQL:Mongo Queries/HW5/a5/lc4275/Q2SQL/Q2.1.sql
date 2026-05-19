SELECT &AttributeReturn& AS attribute, label, COUNT(*) AS count
FROM &Relation&
WHERE &AttributeName1& = &AttributeValue1&
GROUP BY &AttributeReturn&, label
ORDER BY &AttributeReturn&, label
