public class Main {
    public static boolean number_in(int[] arr, int num) {
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] == num) {
                return true;
            }
        }
        return false;
    }

    public static double func1(double elem) {
        return Math.cbrt(Math.pow(((1.0 / 3.0) / 3.0) * elem, Math.tan(elem)));
    }
    public static double func2(double elem)  {
        return ((3.0 / 4.0) / 0.25) * (Math.pow((2 * elem), 3) + 2.0 / 3.0);
    }
    public static double func3(double elem) {
        return Math.sin(Math.sin(Math.asin(Math.sin(elem))));
    }
    public static void output(double[][] arr) {
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 15; ++j) {
                System.out.printf("%.3f", arr[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }
    //7 9 11 13 15 17 19 21 23

    public static void main(String[] args) {
        int[] s = new int[9];
        int el = 23;
        for (int i = 0; i < 9; ++i) {
            s[i] = el;
            el -= 2;
        }

        double[] x = new double[15];
        for (int i = 0; i < x.length; ++i) {
            x[i] = (Math.random() * 13.0) - 5.0;
        }

        double[][] w = new double[9][15];
        int[] arr = {7, 11, 15, 19};
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 15; ++j) {
                if (s[i] == 17) {
                    w[i][j] = func1(x[j]);
                } else if (number_in(arr, s[i])) {
                    w[i][j] = func2(x[j]);
                } else {
                    w[i][j] = func3(x[j]);
                }
            }
        }
        output(w);
    }
}

