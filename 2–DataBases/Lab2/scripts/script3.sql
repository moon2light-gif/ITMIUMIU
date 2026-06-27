SELECT COUNT(DISTINCT studs."ЧЛВК_ИД") FROM "Н_УЧЕНИКИ" studs
-- могут находиться одни и те же люди в группе(типа отчислился, потом появился, результат уменьшается на 4 человека)
INNER JOIN "Н_ЛЮДИ" people 
    ON people."ИД" = studs."ЧЛВК_ИД"
WHERE studs."ГРУППА" = '3102' 
    AND EXTRACT(YEAR FROM AGE(NOW(), people."ДАТА_РОЖДЕНИЯ")) < 20



