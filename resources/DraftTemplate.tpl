package com.wiley.project.tests.drafts;

import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Title;

import static com.wiley.wat.Group.toAutomate;

/**
 * User: {{USER_NAME}}
 * Date: {{TEST_DATE}}
 * <p>
 * Preconditions:
 * {{PRECONDITIONS}}
 * <p>
 * Description:
 * {{TEST_DESCRIPTION}}
 * <p>
 */
public class {{CLASS_NAME}} {

    @Stories(value = {{{STORY}}})
    @Test(groups = {toAutomate})
    public void test_{{CLASS_NAME}}() {
        // specially do nothing
    }
}