# CSCI 620 - Assignment 5 - Rule Mining

## Project Structure
- `a5/lc4275/` — All solution folders (Q1-Q6 for both Mongo and SQL)
- `a5 Grading software/` — Grading JSON configs
- `Yelp_RM_Data/` — Pre-computed reference data (already loaded into both DBs)
- `CSCI 620 - Assignment 5 - Rule mining.pdf` — Assignment spec

## Databases
- **MongoDB**: `yelp_A3Q3` (local, no auth) — has Businesses, Users, Reviews + all Yelp_RM_Data collections
- **MySQL**: `yelp_A3Q3` (user: root, pwd: chenzhe1, path: /usr/local/mysql/bin/mysql) — has business, user, review, friend, userelite, category, businesscategory + all Yelp_RM_Data tables

## Solution Status (All 12 questions complete & verified)

### Q1 - Tabular Data Preparation
- **Binning**: att_1 (review count) uses NTILE via `CEIL(rank * k / total)`, NOT equal-width
- **D1 (Businesses)**: att_2=is_open, att_3-8=boolean attrs (null→2), label=restaurants_price_range (null→0)
- **D2 (Users)**: att_1=NTILE(review_count), att_2=NTILE(friend_count), att_3-6=equal-width `FLOOR(val*k/max)` for useful/funny/cool/fans, label=NTILE(elite_count)
- **Date filtering (Mongo D2)**: Uses `$dateToString` on both sides to handle timezone offsets in yelping_since

### Q2 - AVC-Sets
- Group by (attribute, label) with COUNT
- Q2.0=no filter, Q2.1=1 filter, Q2.2=2 filters
- **Mongo**: Uses `$expr` with `$eq` for matching; `&AttributeValue&` must be wrapped in `{"$toInt": "&AttributeValue&"}` (bare `&` chars break JSON parser)

### Q3 - Gini Index
- **Gini(D)**: `1 - SUM(label_count²) / total²`
- **GiniA(D)**: Weighted sum of per-attribute-value Gini
- Mongo uses `$toDecimal` + `$round` to 30 places; SQL uses `DECIMAL(30,24)` / `DECIMAL(38,10)`

### Q4 - Rule Coverage (Contingency Table)
- 2×2 table: antecedent(1/0) × consequent(1/0) with counts
- Must inject zero-count rows: Mongo uses `$unionWith`, SQL uses `LEFT JOIN` on literal combos
- Q4.0=1 condition, Q4.1=2 conditions, Q4.2=3 conditions

### Q5 - Gini of Rule's Antecedent
- Gini of antecedent=1 subset only: `1 - SUM(count²) / total²`
- Returns empty when antecedent has no support (TP+FP=0)

### Q6 - Classification Metrics
- prec=TP/(TP+FP), recall=TP/(TP+FN), specificity=TN/(TN+FP), accuracy=(TP+TN)/total, error_rate=(FP+FN)/total
- Edge cases: TP+FP=0→prec=0, TP+FN=0→recall=0, TN+FP=0→specificity=1, total=0→empty

## Grading Software Usage
```bash
cd GradingSoftwareJava-2
./app/build/install/app/bin/app "<a5/lc4275 path>" "<grading json path>" false
```

## Key Gotchas
- MongoDB `$documentNumber`/`$rank` require exactly ONE sortBy field — use group/unwind approach instead
- MongoDB param substitution: bare `&param&` outside quotes breaks JSON parsing — always keep in string positions
- MySQL: cast to DECIMAL early to avoid integer overflow in precision calculations
- Yelp_RM_Data files use NTILE binning (equal-count), not equal-width, despite assignment wording
