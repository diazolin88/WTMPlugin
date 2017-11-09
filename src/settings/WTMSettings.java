package settings;

import com.intellij.credentialStore.*;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Class needs for save user settings in file WTMSettings.xml such as:
 *
 * @username
 * @url
 * @password All settings saved under project, so for new project need setup
 * plugin again
 * <p>
 * All fields that should be saved should be public
 */
@State(
        name = "WTM.Settings",
        storages = {
                @Storage("WTMSettings.xml")
        }
)
public class WTMSettings implements PersistentStateComponent<WTMSettings>, User {

    // region Settings vars
    public static final String DEFAULT_VALUE = "";
    public boolean isLogged;
    private static final String RAIL_P_KEY = "wtm.settings.rail.password.key";
    public String railUserName = DEFAULT_VALUE;
    public String railUrl = DEFAULT_VALUE;
    public String template = "package com.wiley.project.tests.drafts;\n" +
            "\n" +
            "import org.testng.annotations.Test;\n" +
            "import ru.yandex.qatools.allure.annotations.Title;\n" +
            "\n" +
            "import static com.wiley.wat.Group.toAutomate;\n" +
            "import ru.yandex.qatools.allure.annotations.Stories;\n" +
            "\n" +
            "/**\n" +
            " * User: {{USER_NAME}}\n" +
            " * Date: {{TEST_DATE}}\n" +
            " * <p>\n" +
            " * Preconditions:\n" +
            " * {{PRECONDITIONS}}\n" +
            " * <p>\n" +
            " * Description:\n" +
            " * {{TEST_DESCRIPTION}}\n" +
            " * <p>\n" +
            " */\n" +
            "public class {{CLASS_NAME}} {\n" +
            "\n" +
            "    @Stories(value = {{{STORY}}})\n" +
            "    @Test(groups = {toAutomate})\n" +
            "    public void test_C{{TEST_RAIL_ID}}_{{AUTHOR_SHORT_NAME}}() {\n" +
            "        // specially do nothing\n" +
            "    }\n" +
            "}";
    //endregion

    //region default template
    public static final String DEFAULT_TEMPLATE = "package com.wiley.project.tests.drafts;\n" +
            "\n" +
            "import org.testng.annotations.Test;\n" +
            "import static com.wiley.wat.Group.toAutomate;\n" +
            "import ru.yandex.qatools.allure.annotations.Stories;\n" +
            "\n" +
            "/**\n" +
            " * User: {{USER_NAME}}\n" +
            " * Date: {{TEST_DATE}}\n" +
            " * <p>\n" +
            " * Preconditions:\n" +
            " * {{PRECONDITIONS}}\n" +
            " * <p>\n" +
            " * Description:\n" +
            " * {{TEST_DESCRIPTION}}\n" +
            " * <p>\n" +
            " */\n" +
            "public class {{CLASS_NAME}} {\n" +
            "\n" +
            "    @Stories(value = {{{STORY}}})\n" +
            "    @Test(groups = {toAutomate})\n" +
            "    public void test_C{{TEST_RAIL_ID}}_{{TEST_METHOD_NAME_KEY}}() {\n" +
            "        // specially do nothing\n" +
            "    }\n" +
            "}";
    ;

    //endregion

    public static WTMSettings getInstance(Project project) {
        return ServiceManager.getService(project, WTMSettings.class);
    }

    @Nullable
    @Override
    public WTMSettings getState() {
        return this;
    }

    @Override
    public void loadState(WTMSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void setUserName(String userName) {
        this.railUserName = userName;
    }

    public void setURL(String url) {
        this.railUrl = url;
    }

    public void setPassword(char[] password) {
        CredentialAttributes attributes = new CredentialAttributes(RAIL_P_KEY, this.railUserName, this.getClass(), false);
        Credentials saveCredentials = new Credentials(attributes.getUserName(), password);
        PasswordSafe.getInstance().set(attributes, saveCredentials);
    }

    @Override
    public String getUserPassword() {
        CredentialAttributes attributes = new CredentialAttributes(RAIL_P_KEY, this.railUserName, this.getClass(), false);
        String password = PasswordSafe.getInstance().getPassword(attributes);
        return null == password ? "" : password;
    }

    @Override
    public String getUserName() {
        return this.railUserName;
    }

    @Override
    public String getURL() {
        return this.railUrl;
    }
}
