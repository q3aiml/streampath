package net.q3aiml.streampath;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

/**
 * @author q3aiml
 */
public class DocumentSets {
    public static DocumentSet ofSources(Source... documents) {
        Iterable<StreamDocument> streamDocuments = Iterables.transform(Arrays.asList(documents),
                new Function<Source, StreamDocument>() {
                    public StreamDocument apply(Source input) {
                        return new StreamDocument(input);
                    }
                });
        return new ImmutableDocumentSet(ImmutableSet.<Document>copyOf(streamDocuments));
    }

    private static class ImmutableDocumentSet implements DocumentSet {
        private final ImmutableSet<Document> documents;

        public ImmutableDocumentSet(ImmutableSet<Document> documents) {
            this.documents = documents;
        }

        public Document currentDocument() {
            return documents.iterator().next();
        }

        public Set<Document> documents() {
            return documents;
        }
    }

    private static class StreamDocument implements Document {
        private final Source input;

        public StreamDocument(Source input) {
            this.input = input;
        }

        public XMLEventReader stream() throws IOException {
            try {
                return XMLInputFactory.newInstance().createXMLEventReader(input);
            } catch (XMLStreamException e) {
                throw new IOException(e);
            }
        }

        public void close() throws IOException {
            // don't need to close Source
        }
    }
}
