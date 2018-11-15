package com.ultimate.exception;

/**
 当获取TableInfo里面不存在的Component时抛出此异常
 */
public class ComponentNotExistsException extends RuntimeException{

    public ComponentNotExistsException(String message) {
        super(message);
    }

}
