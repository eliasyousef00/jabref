package org.jabref.logic.exporter;

import org.jabref.logic.layout.LayoutFormatterPreferences;
import org.jabref.logic.xmp.XmpPreferences;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.database.BibDatabaseMode;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.BibEntryTypesManager;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Answers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;


public class JsonExporterTest {

    public BibDatabaseContext databaseContext;
    private Exporter exportFormat;
    private Exporter exporter;

    @BeforeEach
    public void setUp() {
        List<TemplateExporter> customFormats = new ArrayList<>();
        LayoutFormatterPreferences layoutPreferences = mock(LayoutFormatterPreferences.class, Answers.RETURNS_DEEP_STUBS);
        SavePreferences savePreferences = mock(SavePreferences.class);
        XmpPreferences xmpPreferences = mock(XmpPreferences.class);
        BibEntryTypesManager entryTypesManager = mock(BibEntryTypesManager.class);

        ExporterFactory exporterFactory = ExporterFactory.create(customFormats, layoutPreferences, savePreferences, xmpPreferences, BibDatabaseMode.BIBTEX, entryTypesManager);

        exportFormat = exporterFactory.getExporterByName("json").get();
        exporter = exporterFactory.getExporterByName("json").get();
        databaseContext = new BibDatabaseContext();
    }

    @Test
    public final void exportsSingleEntryWithAuthorField(@TempDir Path tempFile) throws Exception {
        BibEntry entry = new BibEntry(StandardEntryType.Article)
                .withCitationKey("entry1")
                .withField(StandardField.AUTHOR, "Author 1");

        Path file = tempFile.resolve("TDDTestFileName");
        Files.createFile(file);

        exporter.export(databaseContext, file, Collections.singletonList(entry));

        List<String> expected = List.of(
                "{",
                "\"references\": [",
                "    \"id\": \"entry1\"",
                "    \"type\": \"article\"",
                "    \"author\": {",
                "        \"literal\": \"Author 1\"",
                "    }",
                "]",
                "}");

        assertEquals(expected, Files.readAllLines(file));
    }
}
