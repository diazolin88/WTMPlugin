package com.wiley.project.tests.drafts;

import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Title;

import static com.wiley.wat.Group.toAutomate;

/**
 * User: {{USER_NAME}}
 * Date: {{TEST_DATE}}
 * <p>
 * Summary:
 * {{SUMMARY}}
 * <p>
 * Preconditions:
 * {{PRECONDITIONS}}
 * <p>
 * Description:
 * {{TEST_DESCRIPTION}}
 * <p>
 */
public class {{CLASS_NAME}} {

    @Title("{{TEST_RAIL_TITLE}}")
    @Test(groups = {toAutomate})
    public void test_{{PROJECT_PREFIX}}_{{TEST_RAIL_ID}}_{{CLASS_NAME}}() {
        // specially do nothing
    }
}