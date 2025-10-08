#include <iostream>
#include <string>
#include <cmath>

// a = b * q + r
//если a > 0 и b > 0 или a > 0 и b < 0 формула работает корректно
//в случае a < 0 и b < 0 или a < 0 и b > 0 необходимо записать так: a = ((a/b) + 1) * b + r

void Func(int num, int ngsys) {
    while (num != 0) {
        if (num > 0 || num % ngsys == 0) {
            std::cout << num - (num / ngsys) * ngsys;
            num /= ngsys;
        }
        else {
            std::cout << num - ((num / ngsys) + 1) * ngsys;
            num = num/ngsys + 1;
        }
    }
}

int main() {
    std::ios_base::sync_with_stdio(false);
    std::cin.tie(nullptr);
    int num, ngsys;
    std::cin >> num >> ngsys;
    Func(num, ngsys);
    return 0;
}