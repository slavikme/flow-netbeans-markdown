package flow.netbeans.markdown.csl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.spi.ParseException;
import org.openide.util.Exceptions;
import org.pegdown.ast.Node;
import org.pegdown.ast.RootNode;

/**
 *
 * @author Holger
 */
public class MarkdownStructureScanner implements StructureScanner {

    public MarkdownStructureScanner() {
    }

    @Override
    public List<? extends StructureItem> scan(ParserResult pr) {
        List<? extends StructureItem> items = null;
        if (pr instanceof MarkdownParserResult) {
            MarkdownParserResult result = (MarkdownParserResult) pr;
            try {
                RootNode rootNode = result.getRootNode();
                if (rootNode != null) {
                    MarkdownTOCVisitor visitor = new MarkdownTOCVisitor(pr.getSnapshot().getSource().getFileObject());
                    rootNode.accept(visitor);
                    items = visitor.getTOCEntryItems();
                }
            }
            catch (ParseException ex) {
                //Exceptions.printStackTrace(ex);
            }
        }
        if (items == null) {
            items = Collections.emptyList();
        }
        return items;
    }

    @Override
    public Map<String, List<OffsetRange>> folds(ParserResult pr) {
        Map<String, List<OffsetRange>> foldsByType = null;
        if (pr instanceof MarkdownParserResult) {
            MarkdownParserResult result = (MarkdownParserResult) pr;
            try {
                RootNode rootNode = result.getRootNode();
                if (rootNode != null) {
                    List<OffsetRange> sectionFolds = new ArrayList<OffsetRange>();
                    for (Node node : rootNode.getChildren()) {
                        MarkdownTOCVisitor visitor = new MarkdownTOCVisitor(pr.getSnapshot().getSource().getFileObject());
                        rootNode.accept(visitor);
                        sectionFolds = visitor.getOffsetRanges();
                    }
                    foldsByType = Collections.singletonMap("comments", sectionFolds);
                }
            }
            catch (ParseException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        if (foldsByType == null) {
            foldsByType = Collections.emptyMap();
        }
        return foldsByType;
    }

    @Override
    public Configuration getConfiguration() {
        return new Configuration(false, false);
    }
}
