WITH stats AS (
    SELECT
        CAST(AVG(CAST(funny AS DECIMAL(32, 24))) AS DECIMAL(32, 24)) AS mean_funny,
        CAST(MIN(funny) AS DECIMAL(32, 24)) AS min_funny,
        CAST(MAX(funny) AS DECIMAL(32, 24)) AS max_funny
    FROM review
    WHERE review_date BETWEEN '&init_date&' AND '&end_date&'
)
SELECT
    r.id,
    CAST(
        (CAST(r.funny AS DECIMAL(32, 24)) - s.mean_funny)
        /
        (s.max_funny - s.min_funny)
    AS DECIMAL(32, 24)) AS scaled_funny
FROM review r, stats s
WHERE r.review_date BETWEEN '&init_date&' AND '&end_date&'
ORDER BY scaled_funny DESC
