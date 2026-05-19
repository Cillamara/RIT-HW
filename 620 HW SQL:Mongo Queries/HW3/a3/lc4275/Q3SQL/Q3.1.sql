WITH stats AS (
    SELECT
        CAST(MIN(funny) AS DECIMAL(32, 24)) AS min_funny,
        CAST(MAX(funny) AS DECIMAL(32, 24)) AS max_funny
    FROM user
    WHERE yelping_since BETWEEN '&init_date&' AND '&end_date&'
)
SELECT
    u.id,
    CAST(FLOOR(
        CAST(&k& AS DECIMAL(32, 24))
        *
        (CAST(u.funny AS DECIMAL(32, 24)) - s.min_funny)
        /
        (s.max_funny - s.min_funny)
    ) AS UNSIGNED) AS bin_index
FROM user u, stats s
WHERE u.yelping_since BETWEEN '&init_date&' AND '&end_date&'
ORDER BY bin_index DESC, id ASC
