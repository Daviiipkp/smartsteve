package com.daviipkp.smartstevex.implementations.future;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;
import com.daviipkp.smartstevex.Configuration;
import com.daviipkp.smartstevex.implementations.commands.CallbackCommand;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

@CommandDescription(value = "Search for a file and open its containing folder.")
public class FindFilesCommand extends InstantCommand {

    @Describe(description = "The exact name of the file to search for (including extension if possible).")
    private String fileName;

    @Describe(description = "Optional: The root path to start searching (defaults to User Home).")
    private String searchRoot;

    public FindFilesCommand() {
        setCommand(new Runnable() {
            @Override
            public void run() {
                String startPath = (searchRoot != null && !searchRoot.isEmpty())
                        ? searchRoot
                        : System.getProperty("user.home");

                Path root = Paths.get(startPath);
                if (!Files.exists(root)) {
                    throw new RuntimeException("Root path does not exist: " + startPath);
                }
                if(Configuration.MEMORY_DEBUG) {
                    SteveCommandLib.systemPrint("Searching for file: " + fileName + " in " + startPath);
                }

                try (Stream<Path> stream = Files.walk(root, 10)) {
                    Optional<Path> found = stream
                            .filter(path -> path.getFileName().toString().equalsIgnoreCase(fileName))
                            .findFirst();

                    if (found.isPresent()) {
                        File file = found.get().toFile();
                        File parentDir = file.getParentFile();

                        SteveCommandLib.systemPrint("File found at: " + file.getAbsolutePath());
                        SteveCommandLib.systemPrint("Opening folder...");

                        if (Desktop.isDesktopSupported()) {
                            Desktop desktop = Desktop.getDesktop();
                            if (parentDir.exists()) {
                                desktop.open(parentDir);
                            } else {
                                throw new RuntimeException("Parent directory not found for file.");
                            }
                        } else {
                            throw new RuntimeException("Desktop operations not supported on this system.");
                        }

                    } else {
                        throw new RuntimeException("File not found: " + fileName);
                    }

                } catch (IOException e) {
                    throw new RuntimeException("Error during file search: " + e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void handleError(Exception e) {
        super.handleError(e);
        try {
            CallbackCommand.asError(this.getClass(), e);
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}