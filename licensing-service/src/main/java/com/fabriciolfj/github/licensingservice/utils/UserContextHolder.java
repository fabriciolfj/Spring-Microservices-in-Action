package com.fabriciolfj.github.licensingservice.utils;

import org.springframework.util.Assert;

public class UserContextHolder {
    /*
    * A construção TheadLocal nos permite armazenar dados que serão acessíveis apenas por um segmento específico.
    * */
    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<UserContext>();

    public static final UserContext getContext(){
        UserContext context = userContext.get();

        if (context == null) {
            context = createEmptyContext();
            userContext.set(context);

        }
        return userContext.get();
    }

    public static final void setContext(UserContext context) {
        Assert.notNull(context, "Only non-null UserContext instances are permitted");
        userContext.set(context);
    }

    public static final UserContext createEmptyContext(){
        return new UserContext();
    }
}
