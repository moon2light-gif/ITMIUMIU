#десериализация бинарного объекта из обязательного задания в структуру python
with open('../Task1/Task1_Binary.bin', 'rb') as bin_file:
    binary_data = bin_file.read()
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

#превращаем питоновскую структуру в json-файл
#должно получится что-то вроде такого:
#{
# "schedule": {
#   "sel_days": [
#     {
#       "day": "Среда",
#       "time": "12"
#     }
#   ]
# }
#}
def ToJson(data, indent = 0):
    space = "    " * indent

    if isinstance(data, dict):
        items = []
        for key, value in data.items():
            json_value = ToJson(value, indent + 1)
            items.append(f'{space}    "{key}": {json_value}')
        return '{\n' + ',\n'.join(items) + '\n' + space + '}'

    elif isinstance(data, list):
        items = []
        for item in data:
            items.append(f'{space}    {ToJson(item, indent + 1)}')
        return '[\n' + ',\n'.join(items) + '\n' + space + ']'

    elif isinstance(data, str):
        return f'"{data}"'

if __name__ == '__main__':
    data = DeserializeFromBin(binary_data)
    json_text = ToJson(data)
    with open("Dop_Task1.json", "w", encoding="utf-8") as f:
        f.write(json_text)