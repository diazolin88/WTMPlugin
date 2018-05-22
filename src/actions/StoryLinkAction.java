package actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import ru.yandex.qatools.allure.annotations.Stories;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class StoryLinkAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        VirtualFile vf = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        PsiFile psiFile = PsiManager.getInstance(anActionEvent.getProject()).findFile(vf);
        PsiClass[] classes = ((PsiJavaFileImpl) psiFile).getClasses();
        List<PsiMethod> methods = Arrays.stream(classes[0].getMethods()).filter(psiMethod -> psiMethod.getName().startsWith("test_")).collect(Collectors.toList());

        if (!methods.isEmpty()) {
            List<PsiAnnotation> annotations = Arrays.stream(methods.get(0).getAnnotations()).filter(an -> an.getQualifiedName().equals(Stories.class.getName())).collect(Collectors.toList());
            if (!annotations.isEmpty()) {
                Arrays.stream(annotations.get(0).getParameterList().getAttributes())
                        .filter(val -> val.getName().equals("value"))
                        .map(v -> v.getValue().getText())
                        .flatMap(s -> Arrays.stream(s.split(",")))
                        .flatMap(s -> Arrays.stream(new String[]{s.replaceAll("\\{|\\}", "").replaceAll("_", "-").trim()}))
                        .collect(Collectors.toList())
                        .forEach(attr -> {
                            try {
                                Desktop.getDesktop().browse(new URI("https://jira.wiley.com/browse/" + attr));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                        });
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        VirtualFile vf = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (vf != null && vf.exists()) {
            PsiFile psiFile = PsiManager.getInstance(e.getProject()).findFile(vf);
            try {
                PsiClass[] ann = ((PsiJavaFileImpl) psiFile).getClasses();

                List<PsiMethod> methods = Arrays.stream(ann[0].getMethods()).filter(psiMethod -> psiMethod.getName().startsWith("test_")).collect(Collectors.toList());

                if (!methods.isEmpty()) {
                    PsiMethod cMethod = methods.get(0);
                    if (Arrays.stream(cMethod.getAnnotations()).noneMatch(an -> an.getQualifiedName().equals(Stories.class.getName()))) {
                        e.getPresentation().setVisible(false);
                    }
                } else {
                    e.getPresentation().setVisible(false);
                }
            } catch (ClassCastException ignored) {
                e.getPresentation().setVisible(false);
            }
        } else {
            e.getPresentation().setVisible(false);
        }
    }
}
