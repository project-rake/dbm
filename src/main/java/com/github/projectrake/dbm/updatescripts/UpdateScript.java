package com.github.projectrake.dbm.updatescripts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.projectrake.dbm.api.v1.SQLScript;

import java.io.*;
import java.util.jar.JarFile;

/**
 * Created on 19.11.2017.
 * <p>
 * Class representing a complete SQLScript with header and body.
 */
public class UpdateScript {
    public static final String SIGNATURE = "-- $";
    private ScriptHeader header;
    private String script;

    public UpdateScript(SQLScript s) throws IOException {
        try (JarFile file = new JarFile(s.getJarfileSource());
             InputStream in = file.getInputStream(file.getJarEntry(s.getJarfileEntry()))) {
            constructFrom(in);
        }
    }

    private void constructFrom(final InputStream in) throws IOException {
        script = bufferScript(in);
        StringBuilder header = extractHeader(script);
        BufferedReader reader = new BufferedReader(new StringReader(header.toString()));
        StringBuilder pureJson = new StringBuilder();

        String line = null;
        while ((line = reader.readLine()) != null) {
            String sanitized = line.replace("-- $", "");
            if (sanitized.isEmpty()) {
                continue;
            }
            pureJson.append(sanitized);
        }

        this.header = new ObjectMapper().readerFor(ScriptHeader.class).readValue(pureJson.toString());
    }

    public ScriptHeader getHeader() {
        return header;
    }

    public void setHeader(ScriptHeader header) {
        this.header = header;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    private static String bufferScript(final InputStream in) throws IOException {
        StringBuilder scriptbuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;

        while ((line = reader.readLine()) != null) {
            scriptbuilder.append(line);
            scriptbuilder.append("\n");
        }

        return scriptbuilder.toString();
    }

    private static StringBuilder extractHeader(final String script) throws IOException {
        StringBuilder header = new StringBuilder();

        for (String line : script.split("[\n\r]+")) {
            if (line.trim().startsWith(SIGNATURE)) {
                header.append(line);
                header.append("\n");
            }
        }

        return header;
    }

    @Override
    public String toString() {
        return "UpdateScript{" +
                "header=" + header +
                ", script='" + script + '\'' +
                '}';
    }
}
