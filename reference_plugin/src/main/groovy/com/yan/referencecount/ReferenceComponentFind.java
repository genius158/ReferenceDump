package com.yan.referencecount;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.BaseExtension;
import com.android.build.gradle.LibraryExtension;
import com.android.build.gradle.api.BaseVariant;

import org.gradle.api.DomainObjectSet;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since 2020/9/24
 */
public class ReferenceComponentFind {

    public static void find(BaseExtension extension) {
        DomainObjectSet<? extends BaseVariant> variants = null;
        if (extension instanceof AppExtension) {
            variants = ((AppExtension) extension).getApplicationVariants();
        } else if (extension instanceof LibraryExtension) {
            variants = ((LibraryExtension) extension).getLibraryVariants();
        }
        if (variants == null) return;
        variants.all(bv -> bv.getOutputs().all(baseVariantOutput -> {
            baseVariantOutput.getProcessManifest().doLast(task -> {
                File manifest = baseVariantOutput.getProcessResources().getManifestFile();
                try {
                    parseXml(manifest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }));
    }

    private static Element getNode(Element root, String tag) {
        List<Content> children = root.getContent();
        for (Content child : children) {
            if (child instanceof Element) {
                Element tmp = (Element) child;
                String name = tmp.getName();
                if (tag.equals(name)) return tmp;
            }
        }
        return null;
    }

    private static String getAttrName(Element element, String tag) {
        List<Attribute> children = element.getAttributes();
        for (Attribute child : children) {
            if (tag.equals(child.getName())) return child.getValue();
        }
        return null;
    }

    private static void parseXml(File manifest) throws Exception {
        if (!manifest.exists()) return;

        SAXBuilder saxBuilder = new SAXBuilder();
        InputStream is = new FileInputStream(manifest);
        Document document = saxBuilder.build(is);
        is.close();

        Element rootElement = document.getRootElement();
        Element application = getNode(rootElement, "application");
        if (application == null) return;

        List<Content> children = application.getContent();
        for (Content child : children) {
            if (!(child instanceof Element)) continue;
            Element tmp = (Element) child;
            String className;
            switch (tmp.getName()) {
                case "provider":
                    className = getAttrName(tmp, "name");
                    if (!providers.contains(className)) {
                        providers.add(className);
                    }
                    break;
                case "service":
                    className = getAttrName(tmp, "name");
                    if (!services.contains(className)) {
                        services.add(className);
                    }
                    break;
                case "receiver":
                    className = getAttrName(tmp, "name");
                    if (!broadcasts.contains(className)) {
                        broadcasts.add(className);
                    }
                    break;
                case "activity":
                    className = getAttrName(tmp, "name");
                    if (!activities.contains(className)) {
                        activities.add(className);
                    }
                    break;
            }
        }
        //
        ReferenceLog.info("parseXml   \n" + "providers:" + providers + " \n" + "services:" + services + "  \n"
                + "broadcasts:" + broadcasts + "  \n" + "activities:" + activities);
    }

    public static ArrayList<String> activities = new ArrayList<>();
    public static ArrayList<String> providers = new ArrayList<>();
    public static ArrayList<String> services = new ArrayList<>();
    public static ArrayList<String> broadcasts = new ArrayList<>();
}
