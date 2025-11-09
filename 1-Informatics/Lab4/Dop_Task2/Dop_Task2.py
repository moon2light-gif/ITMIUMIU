#часть 1
import yaml
import pickle

#выполним сериализацию из yaml в python структуру, а потом сериализуем в бинарный файл
with open('../SourceFile.yaml') as file:
    data = yaml.load(file, Loader = yaml.SafeLoader)

with open('Dop_Task2_BinaryFile.bin', 'wb') as file_b:
    pickle.dump(data, file_b)
    #записываем в файл нашу data в байтовом виде

#часть 2
import json

with open('Dop_Task2_BinaryFile.bin', 'rb') as file_b:
    binary_data = file_b.read()
    #читаем байты, записанные в data

new_data = pickle.loads(binary_data)
#с помощью библиотеки десериализуем байты в структу питончика
with open('Dop_Task2_JSON.json', 'w') as file_js:
    json.dump(new_data, file_js, ensure_ascii=False, indent=4)
    #записываем python объект в файл в формате json
    #ensure_ascii=False - если ensure_ascii = True, все не-ASCII символы в выводе будут \uXXXX, и результатом будет строка, содержащая только ASCII символы. Если ensure_ascii = False, строки запишутся как есть.
    #indent=4 - просто уровень отступа

