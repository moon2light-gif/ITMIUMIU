lines = open('../SourceFile.yaml').read().splitlines()
lines = [line for line in lines if line.strip() != ""]
#подсчёт количества пробелов(отступов) в начале строки для определения вложенности
#отступом в yaml считается 2 пробела
def IndentLevel(line):
    counter = 0
    while line[counter] == ' ':
        counter += 1
    return counter

#stack = [ ({}, -1) ]
#           ^
#current_container
def ParseYaml(lines):
    data = {}
    stack = [(data, -1)]
    #стэк пар вида контейнер и уровень отступа

    for line in lines:
        indent = IndentLevel(line)
        line = line.strip()
        #если строка начинается с -, то элемент некого списка
        if line.startswith('-'):
            elem = line[1:].strip()
            #избавляемся от -
            key, value = elem.split(':', 1)
            key, value = key.strip(), value.strip().strip('"')
            item = {key : value} if value else {key: {}}
            # достаём текущий контейнер
            while stack and indent <= stack[-1][1]:
                stack.pop()

            current_container = stack[-1][0]

            #если текущий контейнер не список — превращаем в список
            if not isinstance(current_container, list):
                #узнаём последний ключ прошлого контейнера
                old_container = stack[-2][0]
                last_key = list(old_container.keys())[-1]

                if not isinstance(old_container[last_key], list):
                    old_container[last_key] = []
                old_container[last_key].append(item)
                stack.append((item, indent))

            else:
                current_container.append(item)
                stack.append((item, indent))

        #иначе это ключ, у которого значение - словарь, или продолжение элемента списка
        elif ':' in line:
            key, value = line.split(':', 1)
            key, value = key.strip(), value.strip().strip('"')

            # выходим на нужный уровень по отступу
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

    #если словарь
    if isinstance(item_to_serialize, dict):
        result += b'D' #своя метка dict
        for key, value in item_to_serialize.items():
            key_b = str(key).encode('utf-8') #кодирует строку в байты, но так как это человекочитаемая кодировка, мы все равно увидим буквы в выводе
            result += len(key_b).to_bytes(2, 'big') + key_b
            #рекурсивно делаем то же самое со значением
            result += SerializeToBin(value)

    #если список
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

if __name__ == '__main__':
    data = ParseYaml(lines)
    print(data)
    print(SerializeToBin(data))
    with open('Task1_Binary.bin', 'wb') as f:
        f.write(SerializeToBin(data))


