package org.jabref.logic.exporter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jabref.logic.util.StandardFileType;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A custom exporter to write bib entries to a .json file for further processing
 * in other scenarios and applications.
 */
public class JsonExporter extends Exporter {


    public JsonExporter() {
        super("json", "JSON", StandardFileType.JSON);
    }

    /**
     * @param databaseContext the database to export from
     * @param file            the file to write to.
     * @param entries         a list containing all entries that should be exported
     */
    @Override
    public void export(BibDatabaseContext databaseContext, Path file, List<BibEntry> entries) throws Exception {
        JsonObject content = new JsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        content.add("references", parseEntriesToJson(entries));

        writeToFile(gson.toJson(content), file);
    }

    private JsonArray parseEntriesToJson(List<BibEntry> entries){
        JsonArray entriesJson = new JsonArray();

        entries.forEach(e -> entriesJson.add(parseEntryTojson(e)));

        return entriesJson;
    }

    private JsonObject parseEntryTojson(BibEntry entry){
        JsonObject entryJson = new JsonObject();

        entryJson.addProperty("type", entry.getType().getName());

        entry.getFields().forEach(
                f -> entry.getField(f)
                        .ifPresent(v -> entryJson.addProperty(f.getName(), v))
        );
        return entryJson;
    }

    private void writeToFile(String content, Path file) throws Exception{
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write(content);
            writer.flush();
        }
    }

}
