WITH ranked AS (
    SELECT
        id,
        ROW_NUMBER() OVER (ORDER BY funny ASC, id ASC) AS rn,
        COUNT(*) OVER ()                               AS total
    FROM user
    WHERE yelping_since BETWEEN '&init_date&' AND '&end_date&'
)
SELECT
    id,
    CAST(CEIL(
        CAST(&k& AS DECIMAL(32, 24))
        *
        CAST(rn AS DECIMAL(32, 24))
        /
        CAST(total AS DECIMAL(32, 24))
    ) AS UNSIGNED) AS bin_index
FROM ranked
ORDER BY bin_index DESC, id ASC
