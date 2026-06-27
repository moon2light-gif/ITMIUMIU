SELECT studs."ГРУППА" AS Группа, AVG(EXTRACT(YEAR FROM AGE(NOW(), people."ДАТА_РОЖДЕНИЯ"))) AS Средний_возраст 
FROM "Н_УЧЕНИКИ" AS studs
INNER JOIN "Н_ЛЮДИ" AS people
    ON people."ИД" = studs."ЧЛВК_ИД"
WHERE people."ДАТА_РОЖДЕНИЯ" < NOW()
GROUP BY studs."ГРУППА"
HAVING AVG(EXTRACT(YEAR FROM AGE(NOW(), people."ДАТА_РОЖДЕНИЯ"))) < (
    SELECT MIN(EXTRACT(YEAR FROM AGE(NOW(), "Н_ЛЮДИ"."ДАТА_РОЖДЕНИЯ"))) FROM "Н_ЛЮДИ"
    INNER JOIN "Н_УЧЕНИКИ" 
        ON "Н_УЧЕНИКИ"."ЧЛВК_ИД" = "Н_ЛЮДИ"."ИД"
    WHERE "Н_УЧЕНИКИ"."ГРУППА" = '1101' AND "Н_ЛЮДИ"."ДАТА_РОЖДЕНИЯ" < NOW()
);

