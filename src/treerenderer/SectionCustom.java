package treerenderer;

import com.codepine.api.testrail.model.Section;
import model.testrail.RailClient;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

public class SectionCustom {
   private java.util.List<Section> sections;

   public SectionCustom(List<Section> sections) {
      this.sections = sections;
   }

   public void build(){
      //Create root of all sections
      DefaultMutableTreeNode node = new DefaultMutableTreeNode();
      for (Section section : sections){
         if(null == section.getParentId()){
            //Create a folder in root
            DefaultMutableTreeNode sectionToRoot = new DefaultMutableTreeNode(section);
            node.add(sectionToRoot);
         } else if (null != section.getParentId()){

         }

      }
   }
}
