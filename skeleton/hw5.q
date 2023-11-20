/*
1. Make your program so that, for an argument of 1 and using mark-sweep, it runs out of memory
when using a heap size of 384 but not a heap size of 408.

More precisely, your program should give process return code 5 for
ref/quandary -gc MarkSweep -heapsize 384 myprog.q 1

but give process return code 0 for
ref/quandary -gc MarkSweep -heapsize 408 myprog.q 1
*/

mutable int main(int x) {
    if (x == 1) {
        return one(0);
    } else if (x == 2){ 
        return two(0);
    }else if (x == 3){
        return three(0);
    } else if (x == 4) {
        return fourOne(0);
    }
    return 999;
}

int one(int x) {
    if (x < 17) {
        Ref y = 3 . nil;
        return one(x + 1);
    }
    return 999;
}

/*
2. Make your program so that, for an argument of 2 and a fixed heap size (384 bytes), it runs out
of memory using reference counting but not using mark-sweep.

More precisely, your program should give process return code 5 for
ref/quandary -gc RefCount -heapsize 384 myprog.q 2

but give process return code 0 for
ref/quandary -gc MarkSweep -heapsize 384 myprog.q 2
*/

mutable int two(int z) {
    if (z <17) {
        mutable Ref y = 3 . nil;
        mutable Ref x = 3 . nil;
        setRight(y, x);
        setRight(x, y);
        x = nil;
        y = nil;
        two(z + 1);
    }
    return 0;
}

/*
3. Make your program so that, for an argument of 3 and a fixed heap size (384 bytes), it runs out
of memory using mark-sweep but not explicit memory management.

More precisely, your program should give process return code 5 for
ref/quandary -gc MarkSweep -heapsize 384 myprog.q 3

but give process return code 0 for
ref/quandary -gc Explicit -heapsize 384 myprog.q 3
*/

int three(int x) {
    if (x < 17) {
        Ref y =  nil. 3;
        free(y);
        return one(x + 1);
    }
    return 999;
}

/*
4. Make your program so that, for an argument of 4 and a fixed heap size (384 bytes), it runs out
of memory using explicit memory management but not mark-sweep GC.

More precisely, your program should give process return code 5 for
ref/quandary -gc Explicit -heapsize 384 myprog.q 4

but give process return code 0 for
ref/quandary -gc MarkSweep -heapsize 384 myprog.q 4
*/

int fourOne(int i) {
    if (i < 17) {
        int a = fourTwo(1);
        return fourOne(i + 1);
    }
    return 0;
}
int fourTwo(int i) {
    Ref x = (1 . 1);
    return 0;
}