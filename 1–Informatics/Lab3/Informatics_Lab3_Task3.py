# Author = Kramar Mikhail Romanovich
# Group = P3113
# Date = 13.10.2025

# номер в ИСУ - 502511 % 3 = 2
# Вариант 2
'''Написать регулярное выражение, которое проверяет, что пароль соответствует этим
шести требованиям и после ввода пароля сообщать о том, какое из требований не выполнено.'''

import re

tests = ['KL4D0VK4_P4VLU*xx!july23>13377331WxR',
         'lJanuarY05fkpk5555',
         '5670012310JEkaPONCHIK_SePTEMber',
         'MarCh56783OctoBer!000_love',
         ' ',
         'itmo']

for password in tests:
    print(password)

    patt_nums = r'\d'
    nums = re.findall(patt_nums, password)
    sum_nums = sum([int(elem) for elem in nums])
    if (sum_nums != 25):
        print('Rule 1: The digits in your password must add up to 25.')

    patt_month = (r'[jJ][aA][nN][uU][aA][rR][yY]|[fF][eE][bB][rR][uU][aA][rR][yY]|'
              r'[mM][aA][rR][cC][hH]|[aA][pP][rR][iI][lL]|'
              r'[mM][aA][yY]|[jJ][uU][nN][eE]|[jJ][uU][lL][yY]|'
              r'[aA][uU][gG][uU][sS][tT]|[sS][eE][pP][tT][eE][mM][bB][eE][rR]|'
              r'[oO][cC][tT][oO][bB][eE][rR]|'
              r'[nN][oO][vV][eE][mM][bB][eE][rR]|[dD][eE][cC][eE][mM][bB][eE][rR]')
    if (re.search(patt_month, password) == None):
        print('Rule 2: Your password must include a month of the year.')

    patt_spec = r'[^A-Za-z0-9\s]'
    if (re.search(patt_spec, password) == None):
        print('Rule 3: Your password must include a special character.')

    patt_upcase = r'[A-Z]'
    if (re.search(patt_upcase, password) == None):
        print('Rule 4: Your password must include a uppercase letter.')

    patt_number = r'[0-9]'
    if (re.search(patt_number, password) == None):
        print('Rule 5: Your password must include a number')

    patt_len = r'.{5,}'
    if (re.search(patt_len, password) == None):
        print('Rule 6: Your password must be at least 5 characters.')

    print()