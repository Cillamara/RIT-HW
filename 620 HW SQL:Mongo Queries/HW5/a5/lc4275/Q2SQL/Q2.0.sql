SELECT &AttributeReturn& AS attribute, label, COUNT(*) AS count
FROM &Relation&
GROUP BY &AttributeReturn&, label
ORDER BY &AttributeReturn&, label
