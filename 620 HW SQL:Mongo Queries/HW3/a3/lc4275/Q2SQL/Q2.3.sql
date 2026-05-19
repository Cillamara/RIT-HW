WITH stats AS (
    SELECT
        CAST(AVG(CAST(compliment_cool AS DECIMAL(65, 30))) AS DECIMAL(65, 30)) AS mean_cool,
        CAST(AVG(CAST(compliment_cool AS DECIMAL(65, 30)) * CAST(compliment_cool AS DECIMAL(65, 30))) AS DECIMAL(65, 30)) AS mean_sq_cool
    FROM user
    WHERE yelping_since BETWEEN '&init_date&' AND '&end_date&'
)
SELECT
    u.id,
    CAST(
        (
            CAST(u.compliment_cool AS DECIMAL(65, 30)) * CAST(u.compliment_cool AS DECIMAL(65, 30))
            - CAST(2 AS DECIMAL(65, 30)) * CAST(u.compliment_cool AS DECIMAL(65, 30)) * s.mean_cool
            + s.mean_cool * s.mean_cool
        )
        /
        (
            s.mean_sq_cool - s.mean_cool * s.mean_cool
        )
    AS DECIMAL(32, 24)) AS scaled_cool
FROM user u, stats s
WHERE u.yelping_since BETWEEN '&init_date&' AND '&end_date&'
ORDER BY scaled_cool DESC, id ASC
