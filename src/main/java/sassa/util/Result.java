package sassa.util;

import sassa.enums.PassType;

public class Result<R, T> {

    private PassType result;
    private T data;

    public Result() {
    }

    public Result(PassType result, T data) {
        this.result = result;
        this.data = data;
    }

    public void set(PassType result, T data) {
        this.result = result;
        this.data = data;
    }

    public boolean isSuccessful() {
        return this.result == PassType.SUCCESS;
    }

    public boolean isFailure() {
        return this.result == PassType.FAIL;
    }


    public PassType getResult() {
        return this.result;
    }

    public T getData() {
        return this.data;
    }

    public PassType setResult(PassType result) {
        return this.result = result;
    }

    public T setData(T data) {
        return this.data = data;
    }
}
