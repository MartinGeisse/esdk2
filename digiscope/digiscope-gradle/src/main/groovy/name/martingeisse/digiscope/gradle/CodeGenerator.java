package name.martingeisse.digiscope.gradle;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public final class CodeGenerator {

    private static final Pattern classNamePattern = Pattern.compile("([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*");

    private static Configuration freemarkerConfiguration;

    static {
        freemarkerConfiguration = new Configuration(new Version(2, 3, 30));
        freemarkerConfiguration.setClassForTemplateLoading(CodeGenerator.class,
                "/" + CodeGenerator.class.getPackage().getName().replace('.', '/'));
        freemarkerConfiguration.setDefaultEncoding("UTF-8");
        freemarkerConfiguration.setLocale(Locale.US);
        freemarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    private final name.martingeisse.digiscope.gradle.DigiscopeCodegenTask task;

    public CodeGenerator(DigiscopeCodegenTask task) {
        this.task = task;
    }

    public void run() throws Exception {

        // validate settings
        String fullClassName = task.getClassName();
        if (!classNamePattern.matcher(fullClassName).matches()) {
            throw new Exception("invalid generated class name: " + fullClassName);
        }

        // determine output file/folder
        File outputFile = new File(task.getOutputDirectory(), '/' + fullClassName.replace('.', '/') + ".java");
        FileUtils.forceMkdir(outputFile.getParentFile());

        // determine input data for the template
        Map<String, Object> input = new HashMap<>();
        input.put("fullClassName", fullClassName);
        input.put("simpleClassName", fullClassName.substring(fullClassName.lastIndexOf('.') + 1));
        input.put("packageName", fullClassName.contains(".") ? fullClassName.substring(0, fullClassName.lastIndexOf('.')) : null);

        // render the template
        Template template = freemarkerConfiguration.getTemplate("digiscope.ftl");
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8)) {
                template.process(input, outputStreamWriter);
            }
        }

    }

}
