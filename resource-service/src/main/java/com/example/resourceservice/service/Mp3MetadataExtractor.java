package com.example.resourceservice.service;

import com.example.resourceservice.dto.SongMetadataDto;
import com.example.resourceservice.exception.InvalidMp3FileException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class Mp3MetadataExtractor {

    private static final String MP3_MIME_TYPE = "audio/mpeg";

    public SongMetadataDto extract(byte[] audioData) {
        Metadata metadata = new Metadata();
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();

        try (InputStream stream = new ByteArrayInputStream(audioData)) {
            parser.parse(stream, handler, metadata, new ParseContext());
        } catch (IOException | TikaException | org.xml.sax.SAXException e) {
            throw new InvalidMp3FileException("Uploaded file is not a valid MP3 file", e);
        }

        String detectedMimeType = metadata.get(Metadata.CONTENT_TYPE);
        if (detectedMimeType == null || !detectedMimeType.toLowerCase().contains("mpeg")) {
            throw new InvalidMp3FileException("Uploaded file is not a valid MP3 file");
        }

        String title = metadata.get("dc:title") != null ? metadata.get("dc:title") : metadata.get("title");
        String artist = metadata.get("xmpDM:artist") != null ? metadata.get("xmpDM:artist") : metadata.get("Author");
        String album = metadata.get("xmpDM:album");
        String year = extractYear(metadata);
        String durationFormatted = extractDurationFormatted(metadata);

        SongMetadataDto dto = new SongMetadataDto();
        dto.setName(title);
        dto.setArtist(artist);
        dto.setAlbum(album);
        dto.setYear(year);
        dto.setDuration(durationFormatted);
        return dto;
    }

    private String extractYear(Metadata metadata) {
        String date = metadata.get("xmpDM:releaseDate");
        if (date == null) {
            date = metadata.get("date");
        }
        if (date != null && date.length() >= 4) {
            return date.substring(0, 4);
        }
        return null;
    }

    private String extractDurationFormatted(Metadata metadata) {
        String durationSecondsStr = metadata.get("xmpDM:duration");
        if (durationSecondsStr == null) {
            return null;
        }
        try {
            double durationSeconds = Double.parseDouble(durationSecondsStr);
            int totalSeconds = (int) Math.round(durationSeconds);
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            return String.format("%02d:%02d", minutes, seconds);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}