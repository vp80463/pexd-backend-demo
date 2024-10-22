package com.a1stream.common.exception;

import com.ymsl.solid.base.exception.BusinessCodedException;

/**
 * An exception that reports a business processing exception that can be resolved by the application consumer.
 */
public class PJException extends BusinessCodedException {

    private static final long serialVersionUID = 2876236753633028037L;

    public PJException(String messageCode, String[] codeArguments) {
        super(messageCode, getCodeArgumentsValues(codeArguments), null, null);
    }

    private static String[] getCodeArgumentsValues(String[] codeArguments) {

        if (codeArguments == null) return null;
        String[] result = new String[codeArguments.length];

        for (int i = 0; i < codeArguments.length; i++) {
            //result[i] = MessageUtils.getMessage(codeArguments[i]);
        }

        return result;
    }
}
