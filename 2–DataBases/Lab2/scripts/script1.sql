SELECT t2."ИД", t1."ЧЛВК_ИД" FROM "Н_ТИПЫ_ВЕДОМОСТЕЙ" AS t2
RIGHT OUTER JOIN "Н_ВЕДОМОСТИ" t1 
    ON t1."ТВ_ИД" = t2."ИД"
WHERE t2."ИД" = 2 AND t1."ЧЛВК_ИД" > 105590;
