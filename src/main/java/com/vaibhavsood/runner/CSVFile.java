package com.vaibhavsood.runner;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVFile {

    private String file;
    private Iterator<String> fileLines;
    private String header;

    CSVFile(String fileName) {
        this.file = fileName;
        this.fileLines = fileLinesAsIterator();
        this.header = fileLines.next();
    }

    private Iterator<String> fileLinesAsIterator() {
        try {
            Stream<String> lines = createReadersAndGetFileLines();
            return lines.map(String::toString).collect(Collectors.toList()).iterator();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<String> createReadersAndGetFileLines() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(file);
        InputStream inputStream = classPathResource.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        return bufferedReader.lines();
    }

    public String getHeader() {
        return header;
    }

    public Iterator<String> getFileLines(){
        return fileLines;
    }
}
