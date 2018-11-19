package github.cweijan.ultimate.exception;

/**
 当获取CoponentInfo里面不存在的ColumnInfo时抛出此异常
 */
public class ColumnNotExistsException extends RuntimeException{

    public ColumnNotExistsException(String message){
        super(message);
    }

}