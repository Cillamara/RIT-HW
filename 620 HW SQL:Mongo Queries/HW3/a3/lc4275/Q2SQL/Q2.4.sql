WITH filtered AS (
    SELECT b.id, CAST(b.latitude AS DECIMAL(32, 24)) AS lat
    FROM business b
    JOIN businesscategory bc ON b.id = bc.bid
    JOIN category c ON bc.cid = c.id
    WHERE c.name = '&category&'
),
ranked AS (
    SELECT
        id,
        lat,
        ROW_NUMBER() OVER (ORDER BY lat) AS rn,
        COUNT(*) OVER ()                 AS total
    FROM filtered
),
quantiles AS (
    SELECT
        CAST(AVG(CASE WHEN rn = FLOOR(CAST(0.25 AS DECIMAL(32, 24)) * total) OR rn = FLOOR(CAST(0.25 AS DECIMAL(32, 24)) * total) + 1 THEN lat END) AS DECIMAL(32, 24)) AS q1,
        CAST(AVG(CASE WHEN rn = FLOOR(CAST(0.5  AS DECIMAL(32, 24)) * total) OR rn = FLOOR(CAST(0.5  AS DECIMAL(32, 24)) * total) + 1 THEN lat END) AS DECIMAL(32, 24)) AS median_lat,
        CAST(AVG(CASE WHEN rn = FLOOR(CAST(0.75 AS DECIMAL(32, 24)) * total) OR rn = FLOOR(CAST(0.75 AS DECIMAL(32, 24)) * total) + 1 THEN lat END) AS DECIMAL(32, 24)) AS q3
    FROM ranked
)
SELECT
    f.id,
    CAST(
        (f.lat - q.median_lat)
        /
        (q.q3 - q.q1)
    AS DECIMAL(32, 24)) AS scaled_latitude
FROM filtered f, quantiles q
ORDER BY scaled_latitude ASC, id ASC
