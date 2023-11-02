/* built in functions:
Q left(Ref r) – Returns the left field of the object referenced by r
Q right(Ref r) – Returns the right field of the object referenced by r
int isAtom(Q x) – Returns 1 if x’s value is nil or an int; returns 0 otherwise (if x’s value is a non-nil Ref)
int isNil(Q x) – Returns 1 if x’s value is nil; returns 0 otherwise (if x’s value is an int or a nonnil Ref)
      Q
int         ref
*/

Q main (int x) {
    return insertSort((3 . (2 . (1 . nil))));
}

/* 0 if no, non zero if yes */
int sameLength(Q l1, Q l2) {
    /* if both are nil, return 0 */
    if (isNil(l1) == 1 && isNil(l2) == 1) {
        return 999;
    }
    /* sameLenght of the right's */
    else if (isNil(l1) != 1 && isNil(l2) != 1) {
        return sameLength(right((Ref) l1), right((Ref) l2));
    }
    return 0;
}

Q secAndThird(Q l1) {
    Q right = right((Ref) l1);
    Q secondRight = right((Ref) right);
    Q second = left((Ref) right);
    Q third = left((Ref) secondRight);
    return (second . third);
}

Q insertSort(Q list) {
    /* if nil, do nothing */
    /* if left < right, recursive call on right */
    if (isNil(list) == 1 || (isNil(left((Ref) list)) == 1 && isNil((Ref) right((Ref) list)) == 1)) {
        return list;
    }
    else if ((int)left((Ref) list) <= (int) left((Ref) right((Ref) list))){
        return insertSort(right((Ref) list));
    /* if left > right, recursive call on right . left */
    } else if ((int)left((Ref) list) > (int) left((Ref) right((Ref) list))) {
        return insertSort(right((Ref) list)) . left((Ref) list);
    }
    return list;
}

/* 0 if no, non zero if yes */
int equiv(Q l1, Q l2) {
    /* if both are nil, return 0 */
    if (isNil(l1) == 1 && isNil(l2) == 1) {
        return 999;
    }
    /* sameLenght of the right's */
    else if (isNil(l1) != 1 && isNil(l2) != 1) {
        return sameLength(right((Ref) l1), right((Ref) l2));
    }
    return 0;
}