#десериализация бинарного объекта из обязательного задания в структуру python, а потом в xml-формат
with open('../Task1/Task1_Binary.bin', 'rb') as bin_file:
    data_in_file = bin_file.read()
    #прочитали содержимое файла, превратив его в строку

#превращаем в понятную питону структуру
def DeserializeFromBin(binary_data):
    def deserialize(data, offset):
        if offset >= len(data):
            return None, offset

        type_byte = data[offset:offset + 1]
        offset += 1

        if type_byte == b'D':  # Словарь
            result = {}
            while offset < len(data) and data[offset:offset + 1] not in (b'D', b'L', b'S'):
                key_length = int.from_bytes(data[offset:offset + 2], 'big')
                offset += 2
                key = data[offset:offset + key_length].decode('utf-8')
                offset += key_length
                value, offset = deserialize(data, offset)
                result[key] = value
            return result, offset

        elif type_byte == b'L':  # Список
            length = int.from_bytes(data[offset:offset + 2], 'big')
            offset += 2
            result = []
            for _ in range(length):
                item, offset = deserialize(data, offset)
                result.append(item)
            return result, offset

        elif type_byte == b'S':  # Строка
            length = int.from_bytes(data[offset:offset + 2], 'big')
            offset += 2
            value = data[offset:offset + length].decode('utf-8')
            offset += length
            return value, offset

    result, none = deserialize(binary_data, 0)

    return result

def ToXML(data, indent):
    result = ""
    space = "  " * indent
    #тут за отступ берём 2 пробела

    #если словарь
    if isinstance(data, dict):
        for key, value in data.items():
            if isinstance(value, (dict, list)):
                result += f"{space}<{key}>\n{ToXML(value, indent + 1)}{space}</{key}>\n"
            else:
                result += f"{space}<{key}>{value}</{key}>\n"
            #тут происходит форматирование в формат, как в xml:
            #<schedule>
            #  <selected_days>
            #    <item>
            #      <day>Среда</day>
            #       .....

    #если список
    elif isinstance(data, list):
        for item in data:
            if isinstance(item, (dict, list)):
                result += f"{space}<item>\n{ToXML(item, indent + 1)}{space}</item>\n"
                #тут по аналогии со случаем, когда у нас словарь
            else:
                result += f"{space}<item>{item}</item>\n"

    else:
        result += f"{space}{data}\n"

    return result

if __name__ == '__main__':
    data = DeserializeFromBin(data_in_file)

    xml_text = "<?xml version='1.0' encoding='UTF-8'?>\n<data>\n"
    xml_text += ToXML(data, indent=1)
    xml_text += "</data>"

    with open("Dop_Task3.xml", "w", encoding="utf-8") as f:
        f.write(xml_text)