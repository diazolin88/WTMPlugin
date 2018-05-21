package marker;

import com.intellij.codeInsight.daemon.*;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import ru.yandex.qatools.allure.annotations.Stories;
import utils.GuiUtil;

import java.util.*;

public class StoryMarker extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        if (element instanceof PsiAnnotation) {
            PsiAnnotation elem = (PsiAnnotation) element;

            if (elem.getQualifiedName().equals(Stories.class.getName())) {
                PsiNameValuePair[] pair = elem.getParameterList().getAttributes();
                if (pair.length > 0) {
                    Arrays.stream(pair).forEach(k -> {
                        NavigationGutterIconBuilder<PsiElement> builder =
                                NavigationGutterIconBuilder.create(GuiUtil.loadIcon("browser.png")).
                                        setTarget(k.getValue()).
                                        setTooltipText("Navigate to a simple property");
                        result.add(builder.createLineMarkerInfo(element));
                    });
                }

            }
        }
    }


}
