package example.utils;

import lombok.Data;

@Data
public class Result<T> {
    private int code;

    private T data;

    private String msg;

    private static final class CODE {
        public static final int SUCCESS = 200;
        public static final int SERVER_ERROR = 500;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(CODE.SUCCESS);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(CODE.SUCCESS);
        return result;
    }

    public static <T> Result<T> failed(String msg) {
        Result<T> result = new Result<>();
        result.setCode(CODE.SERVER_ERROR);
        result.setMsg(msg);
        return result;
    }

    public boolean isSuccess() {
        return CODE.SUCCESS == code;
    }
}
