USE carbontrack;

START TRANSACTION;

-- 1. Households
-- Match by name so rerunning this script does not add another copy of these households.
INSERT INTO households (name, members_count, created_at)
SELECT demo.name, demo.members_count, CURRENT_TIMESTAMP
FROM (
    SELECT 'Pabi Household' AS name, 4 AS members_count
    UNION ALL SELECT 'Nisha Home', 3
    UNION ALL SELECT 'Green Family', 5
    UNION ALL SELECT 'Eco Living', 2
    UNION ALL SELECT 'Nature House', 4
) AS demo
WHERE NOT EXISTS (
    SELECT 1 FROM households existing WHERE existing.name = demo.name
);

-- 2. Challenges
-- These date ranges include September 28, 2026, so households can join them during the demonstration.
INSERT INTO challenges (
    name,
    description,
    target_reduction_percentage,
    start_date,
    end_date
)
SELECT demo.name, demo.description, demo.target_reduction_percentage, demo.start_date, demo.end_date
FROM (
    SELECT
        'August Commute Reset' AS name,
        'Reduce household travel emissions by combining trips, walking, cycling, or choosing public transport.' AS description,
        12.0 AS target_reduction_percentage,
        DATE('2026-08-01') AS start_date,
        DATE('2026-09-30') AS end_date
    UNION ALL SELECT
        'September Energy-Smart Homes',
        'Lower electricity use through mindful lighting, efficient appliances, and switching off unused devices.',
        10.0,
        DATE('2026-09-01'),
        DATE('2026-09-30')
    UNION ALL SELECT
        'Autumn Waste-Less Challenge',
        'Prevent avoidable household waste with reusable items, meal planning, and careful sorting.',
        8.0,
        DATE('2026-09-15'),
        DATE('2026-10-15')
) AS demo
WHERE NOT EXISTS (
    SELECT 1 FROM challenges existing WHERE existing.name = demo.name
);

-- 3. Activity logs
-- Each household has records in both months. Emissions use the backend factors:
-- CAR_COMMUTE 0.21, BUS_COMMUTE 0.08, ELECTRICITY 0.82, WASTE 0.50 kg CO2e per unit.
-- Carbon emissions are rounded to two decimal places, matching ActivityService.
INSERT INTO activity_logs (
    household_id,
    activity_date,
    activity_type,
    quantity,
    carbon_emission
)
SELECT
    household.id,
    demo.activity_date,
    demo.activity_type,
    demo.quantity,
    ROUND(demo.quantity * CASE demo.activity_type
        WHEN 'CAR_COMMUTE' THEN 0.21
        WHEN 'BUS_COMMUTE' THEN 0.08
        WHEN 'ELECTRICITY' THEN 0.82
        WHEN 'WASTE' THEN 0.50
    END, 2)
FROM (
    SELECT 'Pabi Household' AS household_name, DATE('2026-08-05') AS activity_date, 'CAR_COMMUTE' AS activity_type, 65.0 AS quantity
    UNION ALL SELECT 'Pabi Household', DATE('2026-08-18'), 'CAR_COMMUTE', 55.0
    UNION ALL SELECT 'Pabi Household', DATE('2026-08-05'), 'BUS_COMMUTE', 40.0
    UNION ALL SELECT 'Pabi Household', DATE('2026-08-18'), 'BUS_COMMUTE', 40.0
    UNION ALL SELECT 'Pabi Household', DATE('2026-08-05'), 'ELECTRICITY', 130.0
    UNION ALL SELECT 'Pabi Household', DATE('2026-08-18'), 'ELECTRICITY', 120.0
    UNION ALL SELECT 'Pabi Household', DATE('2026-08-05'), 'WASTE', 4.0
    UNION ALL SELECT 'Pabi Household', DATE('2026-08-18'), 'WASTE', 4.0
    UNION ALL SELECT 'Pabi Household', DATE('2026-09-05'), 'CAR_COMMUTE', 40.0
    UNION ALL SELECT 'Pabi Household', DATE('2026-09-18'), 'CAR_COMMUTE', 40.0
    UNION ALL SELECT 'Pabi Household', DATE('2026-09-05'), 'BUS_COMMUTE', 45.0
    UNION ALL SELECT 'Pabi Household', DATE('2026-09-18'), 'BUS_COMMUTE', 45.0
    UNION ALL SELECT 'Pabi Household', DATE('2026-09-05'), 'ELECTRICITY', 110.0
    UNION ALL SELECT 'Pabi Household', DATE('2026-09-18'), 'ELECTRICITY', 110.0
    UNION ALL SELECT 'Pabi Household', DATE('2026-09-05'), 'WASTE', 3.0
    UNION ALL SELECT 'Pabi Household', DATE('2026-09-18'), 'WASTE', 3.0

    UNION ALL SELECT 'Nisha Home', DATE('2026-08-05'), 'CAR_COMMUTE', 45.0
    UNION ALL SELECT 'Nisha Home', DATE('2026-08-18'), 'CAR_COMMUTE', 45.0
    UNION ALL SELECT 'Nisha Home', DATE('2026-08-05'), 'BUS_COMMUTE', 60.0
    UNION ALL SELECT 'Nisha Home', DATE('2026-08-18'), 'BUS_COMMUTE', 60.0
    UNION ALL SELECT 'Nisha Home', DATE('2026-08-05'), 'ELECTRICITY', 95.0
    UNION ALL SELECT 'Nisha Home', DATE('2026-08-18'), 'ELECTRICITY', 95.0
    UNION ALL SELECT 'Nisha Home', DATE('2026-08-05'), 'WASTE', 3.0
    UNION ALL SELECT 'Nisha Home', DATE('2026-08-18'), 'WASTE', 3.0
    UNION ALL SELECT 'Nisha Home', DATE('2026-09-05'), 'CAR_COMMUTE', 50.0
    UNION ALL SELECT 'Nisha Home', DATE('2026-09-18'), 'CAR_COMMUTE', 50.0
    UNION ALL SELECT 'Nisha Home', DATE('2026-09-05'), 'BUS_COMMUTE', 50.0
    UNION ALL SELECT 'Nisha Home', DATE('2026-09-18'), 'BUS_COMMUTE', 50.0
    UNION ALL SELECT 'Nisha Home', DATE('2026-09-05'), 'ELECTRICITY', 75.0
    UNION ALL SELECT 'Nisha Home', DATE('2026-09-18'), 'ELECTRICITY', 75.0
    UNION ALL SELECT 'Nisha Home', DATE('2026-09-05'), 'WASTE', 2.0
    UNION ALL SELECT 'Nisha Home', DATE('2026-09-18'), 'WASTE', 2.0

    UNION ALL SELECT 'Green Family', DATE('2026-08-05'), 'CAR_COMMUTE', 75.0
    UNION ALL SELECT 'Green Family', DATE('2026-08-18'), 'CAR_COMMUTE', 75.0
    UNION ALL SELECT 'Green Family', DATE('2026-08-05'), 'BUS_COMMUTE', 30.0
    UNION ALL SELECT 'Green Family', DATE('2026-08-18'), 'BUS_COMMUTE', 30.0
    UNION ALL SELECT 'Green Family', DATE('2026-08-05'), 'ELECTRICITY', 155.0
    UNION ALL SELECT 'Green Family', DATE('2026-08-18'), 'ELECTRICITY', 155.0
    UNION ALL SELECT 'Green Family', DATE('2026-08-05'), 'WASTE', 5.0
    UNION ALL SELECT 'Green Family', DATE('2026-08-18'), 'WASTE', 5.0
    UNION ALL SELECT 'Green Family', DATE('2026-09-05'), 'CAR_COMMUTE', 50.0
    UNION ALL SELECT 'Green Family', DATE('2026-09-18'), 'CAR_COMMUTE', 50.0
    UNION ALL SELECT 'Green Family', DATE('2026-09-05'), 'BUS_COMMUTE', 40.0
    UNION ALL SELECT 'Green Family', DATE('2026-09-18'), 'BUS_COMMUTE', 40.0
    UNION ALL SELECT 'Green Family', DATE('2026-09-05'), 'ELECTRICITY', 135.0
    UNION ALL SELECT 'Green Family', DATE('2026-09-18'), 'ELECTRICITY', 135.0
    UNION ALL SELECT 'Green Family', DATE('2026-09-05'), 'WASTE', 4.0
    UNION ALL SELECT 'Green Family', DATE('2026-09-18'), 'WASTE', 4.0

    UNION ALL SELECT 'Eco Living', DATE('2026-08-05'), 'CAR_COMMUTE', 35.0
    UNION ALL SELECT 'Eco Living', DATE('2026-08-18'), 'CAR_COMMUTE', 35.0
    UNION ALL SELECT 'Eco Living', DATE('2026-08-05'), 'BUS_COMMUTE', 45.0
    UNION ALL SELECT 'Eco Living', DATE('2026-08-18'), 'BUS_COMMUTE', 45.0
    UNION ALL SELECT 'Eco Living', DATE('2026-08-05'), 'ELECTRICITY', 70.0
    UNION ALL SELECT 'Eco Living', DATE('2026-08-18'), 'ELECTRICITY', 70.0
    UNION ALL SELECT 'Eco Living', DATE('2026-08-05'), 'WASTE', 2.0
    UNION ALL SELECT 'Eco Living', DATE('2026-08-18'), 'WASTE', 2.0
    UNION ALL SELECT 'Eco Living', DATE('2026-09-05'), 'CAR_COMMUTE', 22.5
    UNION ALL SELECT 'Eco Living', DATE('2026-09-18'), 'CAR_COMMUTE', 22.5
    UNION ALL SELECT 'Eco Living', DATE('2026-09-05'), 'BUS_COMMUTE', 50.0
    UNION ALL SELECT 'Eco Living', DATE('2026-09-18'), 'BUS_COMMUTE', 50.0
    UNION ALL SELECT 'Eco Living', DATE('2026-09-05'), 'ELECTRICITY', 60.0
    UNION ALL SELECT 'Eco Living', DATE('2026-09-18'), 'ELECTRICITY', 60.0
    UNION ALL SELECT 'Eco Living', DATE('2026-09-05'), 'WASTE', 1.5
    UNION ALL SELECT 'Eco Living', DATE('2026-09-18'), 'WASTE', 1.5

    UNION ALL SELECT 'Nature House', DATE('2026-08-05'), 'CAR_COMMUTE', 55.0
    UNION ALL SELECT 'Nature House', DATE('2026-08-18'), 'CAR_COMMUTE', 55.0
    UNION ALL SELECT 'Nature House', DATE('2026-08-05'), 'BUS_COMMUTE', 42.5
    UNION ALL SELECT 'Nature House', DATE('2026-08-18'), 'BUS_COMMUTE', 42.5
    UNION ALL SELECT 'Nature House', DATE('2026-08-05'), 'ELECTRICITY', 120.0
    UNION ALL SELECT 'Nature House', DATE('2026-08-18'), 'ELECTRICITY', 120.0
    UNION ALL SELECT 'Nature House', DATE('2026-08-05'), 'WASTE', 4.0
    UNION ALL SELECT 'Nature House', DATE('2026-08-18'), 'WASTE', 4.0
    UNION ALL SELECT 'Nature House', DATE('2026-09-05'), 'CAR_COMMUTE', 37.5
    UNION ALL SELECT 'Nature House', DATE('2026-09-18'), 'CAR_COMMUTE', 37.5
    UNION ALL SELECT 'Nature House', DATE('2026-09-05'), 'BUS_COMMUTE', 45.0
    UNION ALL SELECT 'Nature House', DATE('2026-09-18'), 'BUS_COMMUTE', 45.0
    UNION ALL SELECT 'Nature House', DATE('2026-09-05'), 'ELECTRICITY', 105.0
    UNION ALL SELECT 'Nature House', DATE('2026-09-18'), 'ELECTRICITY', 105.0
    UNION ALL SELECT 'Nature House', DATE('2026-09-05'), 'WASTE', 3.0
    UNION ALL SELECT 'Nature House', DATE('2026-09-18'), 'WASTE', 3.0
) AS demo
JOIN (
    SELECT MIN(id) AS id, name
    FROM households
    GROUP BY name
) AS household ON household.name = demo.household_name
WHERE NOT EXISTS (
    SELECT 1
    FROM activity_logs existing
    WHERE existing.household_id = household.id
      AND existing.activity_date = demo.activity_date
      AND existing.activity_type = demo.activity_type
      AND existing.quantity = demo.quantity
);

-- 4. Challenge participation
-- Resolve IDs from household/challenge names and honor the unique household/challenge constraint.
INSERT INTO participations (household_id, challenge_id, joined_at)
SELECT household.id, challenge.id, CURRENT_TIMESTAMP
FROM (
    SELECT 'Pabi Household' AS household_name, 'August Commute Reset' AS challenge_name
    UNION ALL SELECT 'Pabi Household', 'September Energy-Smart Homes'
    UNION ALL SELECT 'Pabi Household', 'Autumn Waste-Less Challenge'
    UNION ALL SELECT 'Nisha Home', 'August Commute Reset'
    UNION ALL SELECT 'Nisha Home', 'September Energy-Smart Homes'
    UNION ALL SELECT 'Nisha Home', 'Autumn Waste-Less Challenge'
    UNION ALL SELECT 'Green Family', 'August Commute Reset'
    UNION ALL SELECT 'Green Family', 'September Energy-Smart Homes'
    UNION ALL SELECT 'Green Family', 'Autumn Waste-Less Challenge'
    UNION ALL SELECT 'Eco Living', 'August Commute Reset'
    UNION ALL SELECT 'Eco Living', 'September Energy-Smart Homes'
    UNION ALL SELECT 'Eco Living', 'Autumn Waste-Less Challenge'
    UNION ALL SELECT 'Nature House', 'August Commute Reset'
    UNION ALL SELECT 'Nature House', 'September Energy-Smart Homes'
    UNION ALL SELECT 'Nature House', 'Autumn Waste-Less Challenge'
) AS demo
JOIN (
    SELECT MIN(id) AS id, name
    FROM households
    GROUP BY name
) AS household ON household.name = demo.household_name
JOIN (
    SELECT MIN(id) AS id, name
    FROM challenges
    GROUP BY name
) AS challenge ON challenge.name = demo.challenge_name
WHERE NOT EXISTS (
    SELECT 1
    FROM participations existing
    WHERE existing.household_id = household.id
      AND existing.challenge_id = challenge.id
);

COMMIT;
