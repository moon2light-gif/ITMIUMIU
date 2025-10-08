#include <iostream>
#include <string>
#include <vector>
#include <cmath>

//0110100
//   ^
//'1' = 110001
//'0' = 110000
//'0' ^ '0' or '1' ^ '1' = 0
//'0' ^ '1' = 1, потому что отличаются на 1 единицу

int main() {
    std::string a;
    std::cin >> a;
    short int s1 = a[0] ^ a[2] ^ a[4] ^ a[6];
    short int s2 = a[1] ^ a[2] ^ a[5] ^ a[6];
    short int s3 = a[3] ^ a[4] ^ a[5] ^ a[6];
    short int position = s3 * 4 + s2 * 2 + s1;
    if (position != 0) {
        a[position - 1] = a[position - 1] == '0' ? '1' : '0';
        std::cout << a[2] << a[4] << a[5] << a[6] << "\n" << "error position: " << position;
    }
    else {
        std::cout << a[2] << a[4] << a[5] << a[6] << "\n" << "No error";
    }
    return 0;
}