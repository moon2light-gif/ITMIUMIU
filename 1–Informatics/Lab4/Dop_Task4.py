import time

#скопируем сюда все нужные нам функции из наших заданий
def IndentLevel(line):
    counter = 0
    while line[counter] == ' ':
        counter += 1
    return counter

def ParseYaml(lines):
    data = {}
    stack = [(data, -1)]
    for line in lines:
        indent = IndentLevel(line)
        line = line.strip()
        if line.startswith('-'):
            elem = line[1:].strip()
            key, value = elem.split(':', 1)
            key, value = key.strip(), value.strip().strip('"')
            item = {key : value} if value else {key: {}}
            while stack and indent <= stack[-1][1]:
                stack.pop()
            current_container = stack[-1][0]
            if not isinstance(current_container, list):
                old_container = stack[-2][0]
                last_key = list(old_container.keys())[-1]
                if not isinstance(old_container[last_key], list):
                    old_container[last_key] = []
                old_container[last_key].append(item)
                stack.append((item, indent))
            else:
                current_container.append(item)
                stack.append((item, indent))
        elif ':' in line:
            key, value = line.split(':', 1)
            key, value = key.strip(), value.strip().strip('"')
            while stack and indent <= stack[-1][1]:
                stack.pop()
            current_container = stack[-1][0]
            if value == "":
                current_container[key] = {}
                stack.append((current_container[key], indent))
            else:
                current_container[key] = value
    return data

def SerializeToBin(item_to_serialize):
    result = b''
    if isinstance(item_to_serialize, dict):
        result += b'D'
        for key, value in item_to_serialize.items():
            key_b = str(key).encode('utf-8')
            result += len(key_b).to_bytes(2, 'big') + key_b
            result += SerializeToBin(value)
    elif isinstance(item_to_serialize, list):
        result += b'L'
        result += len(item_to_serialize).to_bytes(2, 'big')
        for item in item_to_serialize:
            result += SerializeToBin(item)
    else:
        result += b'S'
        value = str(item_to_serialize).encode('utf-8')
        result += len(value).to_bytes(2, 'big') + value
    return result

# Из Dop_Task1.py
def DeserializeFromBin(binary_data):
    def deserialize(data, offset):
        if offset >= len(data):
            return None, offset
        type_byte = data[offset:offset + 1]
        offset += 1
        if type_byte == b'D':
            result = {}
            while offset < len(data) and data[offset:offset + 1] not in (b'D', b'L', b'S'):
                key_length = int.from_bytes(data[offset:offset + 2], 'big')
                offset += 2
                key = data[offset:offset + key_length].decode('utf-8')
                offset += key_length
                value, offset = deserialize(data, offset)
                result[key] = value
            return result, offset
        elif type_byte == b'L':
            length = int.from_bytes(data[offset:offset + 2], 'big')
            offset += 2
            result = []
            for _ in range(length):
                item, offset = deserialize(data, offset)
                result.append(item)
            return result, offset
        elif type_byte == b'S':
            length = int.from_bytes(data[offset:offset + 2], 'big')
            offset += 2
            value = data[offset:offset + length].decode('utf-8')
            offset += length
            return value, offset
    result, _ = deserialize(binary_data, 0)
    return result

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

# Из Dop_Task3.py
def ToXML(data, indent):
    result = ""
    space = "  " * indent
    if isinstance(data, dict):
        for key, value in data.items():
            if isinstance(value, (dict, list)):
                result += f"{space}<{key}>\n{ToXML(value, indent + 1)}{space}</{key}>\n"
            else:
                result += f"{space}<{key}>{value}</{key}>\n"
    elif isinstance(data, list):
        for item in data:
            if isinstance(item, (dict, list)):
                result += f"{space}<item>\n{ToXML(item, indent + 1)}{space}</item>\n"
            else:
                result += f"{space}<item>{item}</item>\n"
    else:
        result += f"{space}{data}\n"
    return result


#считываем содержимое бинарного файла
with open('Task1/Task1_Binary.bin') as bin_file:
    binary_data = bin_file.read()

#тест обязательного задания Task1
lines = open('SourceFile.yaml').read().splitlines()
lines = [line for line in lines if line.strip() != ""]

start = time.time()
for i in range(100):
    data = ParseYaml(lines)
    bin_data = SerializeToBin(data)
time_task1 = time.time() - start
start = time.time()

#теперь протестируем задание Dop_Task1.py
start = time.time()
for i in range(100):
    data = DeserializeFromBin(bin_data)
    json_text = ToJson(data)
time_dop1 = time.time() - start

#теперь протестим задание Dop_Task2.py
import yaml
import pickle
import json
with open('SourceFile.yaml') as file:
    yaml_data = yaml.load(file, Loader = yaml.SafeLoader)

start = time.time()
for i in range(100):
    pickle_data = pickle.dumps(yaml_data)
    new_data = pickle.loads(pickle_data)
    json.dumps(new_data, ensure_ascii=False, indent=4)
time_dop2 = time.time() - start

#самое время протестировать задание Dop_Task3.py
start = time.time()
for i in range(100):
    data = DeserializeFromBin(bin_data)
    xml_text = "<?xml version='1.0' encoding='UTF-8'?>\n<data>\n"
    xml_text += ToXML(data, indent = 1)
    xml_text += "</data>"
time_dop3 = time.time() - start

#РЕЗУЛЬТАТЫ
print('РЕЗУЛЬТАТЫ 100 ЗАПУСКОВ:')
print(f'Task1(YAML_TO_BIN).py, результат: {time_task1:.3f} сек')
print(f'Dop_Task1.py, результат: {time_dop1:.3f} сек')
print(f'Dop_Task2.py, результат: {time_dop2:.3f} сек')
print(f'Dop_Task3.py, результат: {time_dop3:.3f} сек')



