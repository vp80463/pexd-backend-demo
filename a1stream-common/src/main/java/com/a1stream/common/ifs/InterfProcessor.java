package com.a1stream.common.ifs;

import org.springframework.stereotype.Component;

@Component
public interface InterfProcessor<T, R> {

    /**
     * @param request
     * @return
     */
     InterfProcessResponse execute(T request);
}

