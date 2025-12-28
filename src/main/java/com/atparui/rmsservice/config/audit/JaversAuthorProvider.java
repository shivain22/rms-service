package com.atparui.rmsservice.config.audit;

import com.atparui.rmsservice.config.Constants;
import com.atparui.rmsservice.security.SecurityUtils;
import org.javers.spring.auditable.AuthorProvider;
import org.springframework.stereotype.Component;

@Component
public class JaversAuthorProvider implements AuthorProvider {

    @Override
    public String provide() {
        return SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM);
    }
}
