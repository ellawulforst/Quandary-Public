/* built in functions:
Q left(Ref r) – Returns the left field of the object referenced by r
Q right(Ref r) – Returns the right field of the object referenced by r
int isAtom(Q x) – Returns 1 if x’s value is nil or an int; returns 0 otherwise (if x’s value is a non-nil Ref)
int isNil(Q x) – Returns 1 if x’s value is nil; returns 0 otherwise (if x’s value is an int or a nonnil Ref)
      Q
int         ref
*/
/*
Ref main(int x) {
    return reverse((((3 . nil) . nil) . (nil . (45 . (56 . ((777 . nil) . nil))))));
}
*/


int isList(Q list) {
    /* if its an int, its not a list */
    if (isAtom(list) == 1 && isNil(list) != 1) {
        return 0;
    }
    /* if its a ref and is nil, its a list */
    else if (isAtom(list) == 1 && isNil(list) == 1) {
        return 1;
    }
    /* if its a ref and not nil, recursive call on right side */
    else {
        return isList(right((Ref)list));
    }    
    return 9;
}

Ref append(Ref l1, Ref l2) {
    /* if left is nil, return  l1 . l2 */
    /* if right is nil, return 
    /* if right side of l1 is empty, tack on l2 to that spot */
    if (isNil(l1) == 1 && isNil(l2) == 1) {
        return nil;
    } else if (isNil(l1) == 1 && isNil(l2) != 1) {
        return l2;
    } else if (isNil(l1)!= 1 && isNil(l2) == 1) {
        return l1;
    } else if (isNil(right(l1)) == 1) {
        return left(l1) . l2;
    /* if right side of l1 is not empty, call append again */
    } else {
        return left(l1) . append((Ref) right(l1), l2);
    }
    return nil;
}

Ref reverse (Ref list) {
    /* if list is nil or an int, return normal */

    /* if right is nil, reverse the right and append on left */
    if (isNil(list) == 0 && isNil(right((Ref) list)) == 0) {
        return append(reverse((Ref) right(list)), (left(list).nil));
    }
    return list;
}
/* nonzero = is sorted, zero = is not sorted */
int isSorted(Q list) {
    /* if list is nil or an int, return 1 */
    if (isNil((list)) == 1 || (isAtom(list) == 1 && isNil(list) != 1)) {
        return 1;
    /* if length of left is < length of right, keep checking */
    } else if (length(left((Ref) list)) <= length(right((Ref) list))) {
        return isSorted(left((Ref) list)) + isSorted(right((Ref) list));
    /* else return 1 */
    }
    return 0;
}

int length(Q list) {
    /* if the list is nil or just an int, return 1 */
    if ((isNil(list) == 1) || (isAtom(list) == 1 && isNil(list) != 1)) {
        return 1;
    }
    /* if the right is nil */
    else if (isNil(right((Ref) list)) == 1) {
        /* if the left not a list, return 1 */
        if ((isAtom(left((Ref) list)) != 1 || isNil(left((Ref)list)) != 1)) {
            return 2;
        /* else return length of left + 1 */
        } else {
            return length(left((Ref) list)) + 1;
        }
    /* if the right is not nil */
    } else {
        /* if the left is not a list, return length of right + 1 */
        if ((isAtom(left((Ref) list)) != 1 || isNil(left((Ref) list)) != 1)) {
            return 1 + length(right((Ref) list));
        /* else return length of left + length of right */
        } else {
           return length(left((Ref) list)) + length(right((Ref) list));
        }
    }
    return 999;
}

/* Question 4
Since quandary does not have any for/while loops, if statments are essential for
evaluating the stopping point of a calculation. Recursion is also necessary
for making immutable quandary turing complete. In order to get around the immutable
property of quandary and the lack of loops, a recursive call is essential to
repeating the same set of steps multiple times. If you knew how many times a
set of steps had to be executed and it was a small enough number, you in theory
COULD use nested if statements to simulate a loop and make a copy of the
incrementation variable to determine how many times to 'loop'. This gets unrealistic
when the amount of iterations needed increases and is not a feasible implementation */
