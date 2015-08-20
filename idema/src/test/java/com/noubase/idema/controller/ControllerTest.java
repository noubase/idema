package com.noubase.idema.controller;

import com.noubase.core.crud.test.AbstractControllerTest;
import com.noubase.idema.Application;
import com.noubase.idema.domain.User;
import org.springframework.boot.test.SpringApplicationConfiguration;

/**
 * Created by rshuper on 17.08.15.
 */
@SpringApplicationConfiguration(classes = {Application.class})
abstract class ControllerTest extends AbstractControllerTest<User> {

    protected ControllerTest() {
        super(User.class);
    }
}
