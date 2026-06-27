-- наша функция 
CREATE OR REPLACE FUNCTION s502511.checkDinoAction() 
    RETURNS TRIGGER AS $$
    BEGIN 
        -- NEW - это запись строки, которая наша база "держит" перед тем, как вставить в таблицу
        IF NEW.distance_in_meters < 2.00 THEN
            UPDATE s502511.dino_state SET action = 'ATTACKING'
            WHERE dino_id = NEW.dino_id;
            -- выводим сообщение в консоль (Messages)
            RAISE NOTICE 'Опасность! Динозавр % слишком близко к ящикам!', NEW.dino_id;
        END IF;

        RETURN NEW; -- формальность, так как требуется RETURN
    END;
    $$ LANGUAGE plpgsql; 

-- тут создаём сам триггер, который сработает уже после вставки или обновления таблицы Dino_position
CREATE OR REPLACE TRIGGER dinoStatusAlert
    AFTER INSERT OR UPDATE ON s502511.dino_position
    -- теперь для каждой строки выполняем функцию
    FOR EACH ROW
    EXECUTE FUNCTION s502511.checkDinoAction();

-- пример выполнения
UPDATE s502511.dino_position SET distance_in_meters = 1.34
WHERE dino_id = 1 OR dino_id = 5;

SELECT * FROM s502511.dino_state;
