package utils;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.Collection;

public class ClassScanner {
    private static ClassScanner instance = null;

    private ClassScanner () {
    }

    public static ClassScanner getInstance(){
        if (instance == null) {
            instance = new ClassScanner();
        }

        return instance;
    }

    public Collection<File> getAllClassList(Project project) {
        File file = new File(project.getBasePath());
        Collection<File> fileList = FileUtils.listFiles(file, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        return fileList;
    }

//    public boolean isClassExistInProject(Project project, String className) {
//        return getAllClassList(project)
//                .stream()
//                .filter(clazz -> clazz.equals(className)) ;
//    }
}
