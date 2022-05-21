package com.matchi.bargain.finder.transform;

public interface Transformer <T, R> {
    R transform(T t);
}
