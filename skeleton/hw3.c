int main() {
    int result;
    fib(3, &result);
    printf("Result = %d\n", result);
}
void fib(int n, int* result) {
    if (n < 2) {
        *result = n;
    }
    int first, second;
    fib(n - 1, &first);
    fib(n - 2, &second);
    *result =  first + second;
}