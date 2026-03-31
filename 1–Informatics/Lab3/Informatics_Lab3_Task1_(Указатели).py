# Author = Kramar Mikhail Romanovich
# Group = P3113
# Date = 13.10.2025

# номер в ИСУ - 502511 % 7 = 2
# Вариант 2

''' Дан текст. Необходимо найти в нём каждый фрагмент, где сначала идёт слово «ВТ»,
затем не более 4 слов, и после этого идёт слово «ИТМО».
Для простоты будем считать словом любую последовательность букв, цифр и знаков
«_» (то есть символов \w).'''

import re

arr = ['А ты знал, что ВТ – лучшая кафедра в ИТМО?',
       'Зачем нужна личная жизнь, когда есть ВТ так называемое ИТМО ВТ которое ВТ ИТМО в ИТМО',
       'Я так люблю ВТ ИТМО просто любимая кафедра ВТ - равно ИТМО',
       'Вчера шел по Кремниевой долине ВТ и увидел ИТМО ВТ ИТМО ИТМО ВТ',
       'Лабораторная ВТ ИТМО В ИТМО ПРОХОДИТ ИТМО',
       'Очень равноценные задания для каждого варианта',
       'ВТ слово - ВТ слово ИТМО слово ИТМО']

for elem in arr:
    if ' - ' in elem or ' – ' in elem:
        elem = elem.replace(' - ', ' ').replace(' – ', ' ')
    patt = r'ВТ(?:\s+\w+){0,4}\s+\bИТМО\b'
    sup_patt = rf'(?=({patt}))'
    matches = list(re.finditer(sup_patt, elem))
    found_matches = []
    for match in matches:
        found_matches.append(match.group(1))

    for elem in found_matches:
        l = 1
        r = 1
        for r in range(l, len(elem) - 4):
            if elem[r:r+4] == 'ИТМО':
                found_matches.append(elem[l-1:r+4])
                if r + 4 == len(elem):
                    while elem[l-1] + elem[l] != 'ВТ':
                        l += 1
                    r = l
    found_matches = list(set(found_matches))
    if found_matches:
        print(found_matches)


